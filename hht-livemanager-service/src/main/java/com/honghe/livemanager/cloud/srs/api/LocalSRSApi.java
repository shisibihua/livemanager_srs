package com.honghe.livemanager.cloud.srs.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.cloud.srs.dao.LiveSRSDao;
import com.honghe.livemanager.cloud.srs.model.ClientModel;
import com.honghe.livemanager.cloud.srs.model.StreamModel;
import com.honghe.livemanager.cloud.srs.util.SRSLiveUtil;
import com.honghe.livemanager.common.util.HttpUtils;
import com.honghe.livemanager.common.util.TipsMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Description:对接SRS提供的Api接口
 * Author：hyh
 * Date: 2018/10/16
 */
@Component("localSRSApi")
public class LocalSRSApi {
    private static Logger logger=Logger.getLogger(LocalSRSApi.class);
    @Autowired
    LiveSRSDao srsDao;

    public static LocalSRSApi localSRSApi;

    @PostConstruct
    public void init() {
        localSRSApi = this;
        localSRSApi.srsDao = this.srsDao;
    }

    /**
     * 开启关闭推流
     * @param streamCode 直播码
     * @param status 开关状态，0 表示禁用，1 表示允许推流，2 表示断流
     * @return srs执行断流的结果{"code": 0} code为0表示成功
     *
     * 接口描述：
     * 当status值为0或者2时，调用SRS提供的断流接口，但仅能断流一次。
     * 若需要长久断流、禁用流，需要在on_publish回调中判断流地址是否可用，返回不同code值给SRS来限定
     * 开启流，只需要在on_publish回调时，判断流地址可用，返回code：0即可
     */
    public JSONObject setLiveStatus(String streamCode,String status){
        JSONObject result = new JSONObject();
        int code;
        boolean success;
        String errMsg = null;
        if ("0".equals(status) || "2".equals(status)){
            //先查询clientId
            Integer clientId = localSRSApi.srsDao.getClientId(streamCode);
            if (clientId != null){
                String url = getSRSAddress() + TipsMessage.SRS_API_CLIENTS + clientId;
                try {
                    //调用SRS踢流的命令，DELETE请求
                    String json = HttpUtils.httpDelete(url, "");
                    JSONObject jsonObj = JSONObject.parseObject(json);
                    //code=2049,踢流成功，无流状态
                    if (jsonObj != null && jsonObj.getInteger("code") != null &&
                            (jsonObj.getInteger("code") == 0 || jsonObj.getInteger("code")==2049)){
                        //踢流成功
                        code = 0;
                        success = true;
                        errMsg = "delete client success:" + clientId;
                    }else {
                        //踢流失败
                        code = -2;
                        success = false;
                        errMsg = "client is not exist:" + clientId;
                    }
                } catch (Exception e) {
                    code = -2;
                    success = false;
                    errMsg = "delete client failed:" + clientId;
                }
            }else {
                //未推过流
                code = 0;
                success = false;
                errMsg = "can not find streamCode:" + streamCode;
            }
            logger.debug(errMsg);
        }else {
            //允许推流
            code = 0;
            success = true;
        }

        result.put(TipsMessage.CODE_KEY,code);
        result.put("success",success);
        result.put("errMsg",errMsg);
        return result;
    }

    /**
     * 获取SRS地址
     * @return api地址
     */
    private String getSRSAddress() {
        return "http://" + SRSLiveUtil.SRS_SERVER_IP + ":" + SRSLiveUtil.SRS_SERVER_API_PORT + "/api/v1/";
    }

    /**
     * 查询直播状态
     * @param streamCode  直播码
     * @return
     *
     * 接口描述:
     * 用途:用于查询某条流是否处于正在直播的状态，其内部原理是基于腾讯云对音视频流的中断检测而实现的，因此实时性上可能不如 App
     *      的主动上报这么快速和准确，但在进行直播流定时清理检查的时候可以作为一种不错的补充手段
     * 说明:如果要查询的推流直播码从来没有推过流，会返回 20601 错误码
     */
    public JSONObject getStatus(String streamCode){
        JSONObject result = new JSONObject();
        //查询streamId
        Integer streamId = localSRSApi.srsDao.getStreamId(streamCode);
        int status;
        String msg;
        Integer code;
        if (streamId != null){
            String url = getSRSAddress() + TipsMessage.SRS_API_STREAMS + streamId;
            JSONObject streamJson = HttpUtils.httpGet(url);
            code = streamJson.getInteger(TipsMessage.CODE_KEY);
            if (code!= null && code == 0){
                StreamModel stream = JSON.parseObject(streamJson.getJSONObject("stream").toJSONString(),StreamModel.class);
                if (stream != null){
                    if (stream.getPublish().isActive()){
                        msg = "直播中";
                        status = 1;
                    }else {
                        status = 0;
                        msg = "无流";
                    }
                }else {
                    status = 0;
                    msg = "数据有误";
                }
            }else {
                code = -2;
                status = 0;
                msg = "无此直播流信息";
            }
        }else {
            code = -2;
            status = 0;
            msg = "无此直播码信息";
        }

        JSONArray jsonArr = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("status",status);
        jsonObj.put("rate_type",0);
        jsonObj.put("recv_type",3);
        jsonArr.add(jsonObj);
        result.put(TipsMessage.CODE_KEY,code);
        result.put("errMsg",msg);
        result.put("liveData",jsonArr);
        return result;
    }


    /**
     * 查询统计信息
     * @param type         接口查询类型，all查询所有；push查询推流；play查询播放
     * @param streamCode   直播码
     * @return 直播信息
     */
    public JSONObject getLiveStatatis(String type,String streamCode){
        String streamInfo;
        switch (type){
            case "all":
                streamInfo = getALLStreamInfo(streamCode);
                break;
            case "push":
                streamInfo = getPushStreamInfo(streamCode);
                break;
            case "play":
                streamInfo = getPlayStreamInfo(streamCode);
                break;
            default:
                streamInfo = "";
        }
        int totalOnline = 0;
        int streamCount = 0;
        float totalBandwidth = 0;
        //获取到直播虚拟主机的整体信息
        Integer vHostId = localSRSApi.srsDao.getVHostId(streamCode);
        if (vHostId != null){
            String url = getSRSAddress() + TipsMessage.SRS_API_VHOSTS + vHostId;
            JSONObject jsonObject = HttpUtils.httpGet(url);
            Integer code = jsonObject.getInteger(TipsMessage.CODE_KEY);
            if (code != null && code == 0){
                JSONObject vHost = jsonObject.getJSONObject("vhost");
                if (vHost != null){
                    streamCount = vHost.getInteger("streams")/2;
                    totalOnline = vHost.getInteger("clients") - streamCount*3;
                    totalBandwidth = vHost.getJSONObject("kbps").getFloat("send_30s")/1000;
                }
            }else {
                logger.error("can not find this vHost:" + vHostId);
            }
        }else {
            logger.error("can not find this streamCode:" + streamCode);
        }
        JSONObject result = new JSONObject();
        result.put(TipsMessage.CODE_KEY,0);
        result.put("success",true);
        result.put("totalBandwidth",totalBandwidth);
        result.put("streamInfo",streamInfo);
        result.put("totalOnline",totalOnline);
        result.put("streamCount",streamCount);
        return result;

    }

    /**
     * 获取播流的信息
     * @param streamCode 直播码
     * @return streamInfo
     */
    private String getPlayStreamInfo(String streamCode) {
        //获取SRS的stream信息
        JSONObject stream = putPlayInfo(streamCode);
        JSONArray jsonArr = new JSONArray();
        jsonArr.add(stream);
        return jsonArr.toJSONString();
    }

    /**
     * 解析转格式playInfo
     * @param streamCode 直播码
     * @return playInfo
     */
    private JSONObject putPlayInfo(String streamCode) {
        Integer streamId = localSRSApi.srsDao.getStreamId(streamCode);
        JSONObject stream = new JSONObject();
        if (streamId != null){
            String url = getSRSAddress() + TipsMessage.SRS_API_STREAMS + streamId;
            JSONObject jsonObject = HttpUtils.httpGet(url);
            Integer code = jsonObject.getInteger(TipsMessage.CODE_KEY);
            if (code != null && code == 0){
                //解析，转格式
                StreamModel streamModel = JSON.parseObject(jsonObject.getJSONObject("stream").toJSONString(),StreamModel.class);
                stream.put("stream_name",streamCode);
                stream.put("bandwidth",streamModel.getKbps().getSend_30s()/1000);
                stream.put("online",streamModel.getClients() - 2);

            }else {
                logger.error("can not find this stream:" + streamId);
            }
        }else {
            logger.error("can not find this streamCode:" + streamCode);
        }
        return stream;
    }


    /**
     * 获取推流的信息
     * @param streamCode 直播码
     * @return streamInfo
     */
    private String getPushStreamInfo(String streamCode) {
        JSONObject stream = putPushInfo(streamCode);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(stream);
        return jsonArray.toJSONString();
    }

    /**
     * 解析转格式pushInfo
     * @param streamCode 直播码
     * @return pushInfo
     */
    private JSONObject putPushInfo(String streamCode) {
        JSONObject stream = new JSONObject();
        Integer clientId = localSRSApi.srsDao.getClientId(streamCode);
        if (clientId != null){
            //获取SRS的client信息
            String url = getSRSAddress() + TipsMessage.SRS_API_CLIENTS + clientId;
            JSONObject jsonObject = HttpUtils.httpGet(url);
            Integer code = jsonObject.getInteger(TipsMessage.CODE_KEY);
            if (code != null && code == 0){
                //解析，转格式
                ClientModel clientModel = JSON.parseObject(jsonObject.getJSONObject("client").toJSONString(),ClientModel.class);
                stream.put("stream_name",streamCode);
                stream.put("client_ip",clientModel.getIp());
                String tcUrl = clientModel.getTcUrl();
                String serverIp = tcUrl.substring(tcUrl.lastIndexOf("//") + 2, tcUrl.lastIndexOf(":"));
                stream.put("server_ip",serverIp);
                stream.put("fps",0);
                stream.put("speed",0);
            }else {
                logger.error("can not find this client:" + clientId);
            }
        }else {
            logger.error("can not find streamCode:" + streamCode);
        }
        return stream;
    }

    /**
     * 获取推流和播流的信息，
     * @param streamCode 直播码
     * @return streamInfo
     */
    private String getALLStreamInfo(String streamCode) {
        JSONArray jsonArr = new JSONArray();
        JSONObject playInfo = putPlayInfo(streamCode);
        JSONObject pushInfo = putPushInfo(streamCode);
        pushInfo.put("bandwidth",playInfo.getFloat("bandwidth"));
        pushInfo.put("online", playInfo.getInteger("online"));
        jsonArr.add(pushInfo);
        return jsonArr.toJSONString();
    }

}
