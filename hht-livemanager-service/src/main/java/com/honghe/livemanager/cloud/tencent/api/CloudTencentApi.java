package com.honghe.livemanager.cloud.tencent.api;

import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.common.util.*;
import com.honghe.livemanager.util.TencetLiveUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯云api接口
 * @author caoqian
 * @date 20180821
 */
@Component("cloudTencentApi")
public class CloudTencentApi {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CloudTencentApi.class);
    private static final String CURRENTPAGE="1";
    private static final String PAGESIZE="10";
    private static final String STATS_PAGESIZE="300";
    @Autowired
    @Qualifier("tencetLiveUtil")
    LiveUtil tencetLiveUtil;

    /**
     * 开启关闭推流
     * @param streamCode    直播码
     * @param status      开关状态，0 表示禁用，1 表示允许推流，2 表示断流
     * @param time        有效时间，格式：yyyy-MM-dd HH:mm:ss
     * @param path        多路径用户使用-----非必需
     * @param domain      多域名用户使用-----非必需
     * @return
     *
     * 接口描述：
     * 用途:主要用于鉴黄时的禁播场景，比如您如果在后台发现某个主播有涉黄或者反动内容，可以随时断流或者禁用这条流。
     * 说明:一条直播流一旦被设置为【禁用】状态，推流链路将被腾讯云主动断开，并且后续的推流请求也会被拒绝，
     *      一条流最长禁用 3 个月，超过 3 个月，禁用失效。
     */
    public JSONObject setLiveStatus(String streamCode,String status,String time,String path,String domain){
        String apiUrl= TencetLiveUtil.CLOUD_TENCENT_FCGI_URL;
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("appid",TencetLiveUtil.CLOUD_TENCENT_APPID);
        params.put("interface", GetCTIntefaceName.OPERATION_SETSTATUS.value());
        params.put("Param.s.channel_id",streamCode);
        if(ParamUtil.isEmpty(status)){
            status="1";
        }
        params.put("Param.n.status",Integer.parseInt(status));
        if(ParamUtil.isEmpty(path)){
            path="";
        }
        params.put("Param.s.path",path);
        if(ParamUtil.isEmpty(domain)){
            domain="";
        }
        params.put("domain",domain);
        int t=0;
        if(ParamUtil.isEmpty(time)){
            t=tencetLiveUtil.getValidTime();
        }else{
            t=tencetLiveUtil.getValidTime(time);
        }
        params.put("t",t);
        params.put("sign",tencetLiveUtil.getSign(params));
        String url=getApiUrl(apiUrl,params);
        logger.debug("开启关闭推流url="+url);
        JSONObject json=HttpUtils.httpGet(url);
        logger.debug("开启关闭推流返回结果json="+json);
        JSONObject result=new JSONObject();
        getReturnResult(json,result);
        return result;
    }

    /**
     * 暂停并延迟恢复
     * @param streamCode   直播码id
     * @param time       有效时间,格式:yyyy-MM-dd HH:mm:ss
     * @param endTime    禁播截止的时间,格式:yyyy-MM-dd HH:mm:ss
     * @param action     动作：断流：forbid；恢复推流：resume
     * @param path       路径:多路径用户使用------非必需
     * @param domain     域名:多域名用户使用------非必需
     * @return
     *
     * 接口描述：
     * 用途：当某些原因不允许主播推流时，可调用此接口。
     * 说明：调用该接口可以暂停推某路流（即禁止推流），如果要恢复主播推流，可再次调用该接口或者设置一个恢复时间，
     *       到达指定时间后允许推流，最长禁止推流 3 个月，即禁止推流截止时间最长为当前时间往后 3 个月，
     *       如果超过 3 个月，则以 3 个月为准。
     */
    public JSONObject channelManager(String streamCode,String time,String endTime,String action,String path,String domain){
        String apiUrl= TencetLiveUtil.CLOUD_TENCENT_FCGI_URL;
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("appid",TencetLiveUtil.CLOUD_TENCENT_APPID);
        params.put("interface", GetCTIntefaceName.OPERATION_CHANNELMANAGER.value());
        params.put("Param.s.channel_id",streamCode);
        params.put("Param.n.abstime_end",convertTimeStamp(endTime));
        if(ParamUtil.isEmpty(path)){
            path="";
        }
        params.put("Param.s.path",path);
        if(ParamUtil.isEmpty(domain)){
            domain=tencetLiveUtil.getTencentDomain();
        }
        params.put("Param.s.domain",domain);
        int t=tencetLiveUtil.getValidTime(time);
        params.put("t",t);
        params.put("sign",tencetLiveUtil.getSign(params));
        if("0".equals(action)){
            //断流
            action="forbid";
        }else if("1".equals(action)){
            //恢复推流
            action="resume";
        }
        params.put("Param.s.action",action);
        String url=getApiUrl(apiUrl,params);
        logger.debug("暂停并延迟恢复url="+url);
        JSONObject json=HttpUtils.httpGet(url);
        logger.debug("暂停并延迟恢复返回结果json="+json);
        JSONObject result=new JSONObject();
        getReturnResult(json,result);
        return result;
    }

    /**
     * 延播
     * @param streamCode 直播码
     * @param time      过期时间
     * @param delayTime 延播时长:单位秒，上限 600s
     * @return
     *
     * 接口描述：
     * 用途:延播功能主要用于直播流的延迟播放。
     * 说明:
     *      延播上限为 10 分钟；
     *      若需取消延播、可设置 time_length=0；
     *      推流前设置延播，则推流时生效；
     *      推流中设置延播，则当前流会在 time_length 时间内处于黑屏状态，达到延播时间后恢复播放；
     */
    public JSONObject setLiveDelay(String streamCode,String time,int delayTime){
        String apiUrl= TencetLiveUtil.CLOUD_TENCENT_FCGI_URL;
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("appid",TencetLiveUtil.CLOUD_TENCENT_APPID);
        params.put("interface", GetCTIntefaceName.OPERATION_SETDELAY.value());
        params.put("Param.s.channel_id",streamCode);
        params.put("Param.n.time_length",delayTime);
        int t=tencetLiveUtil.getValidTime(time);
        params.put("t",t);
        params.put("sign",tencetLiveUtil.getSign(params));
        String url=getApiUrl(apiUrl,params);
        logger.debug("延播url="+url);
        JSONObject json=HttpUtils.httpGet(url);
        logger.debug("延播返回结果json="+json);
        JSONObject result=new JSONObject();
        getReturnResult(json,result);
        return result;
    }

    /**
     * 查询直播状态
     * @param streamCode  直播码，一次只能查询一条直播流
     * @return
     *
     * 接口描述:
     * 用途:用于查询某条流是否处于正在直播的状态，其内部原理是基于腾讯云对音视频流的中断检测而实现的，因此实时性上可能不如 App
     *      的主动上报这么快速和准确，但在进行直播流定时清理检查的时候可以作为一种不错的补充手段
     * 说明:如果要查询的推流直播码从来没有推过流，会返回 20601 错误码
     */
    public JSONObject getStatus(String streamCode,String time){
        String apiUrl= TencetLiveUtil.CLOUD_TENCENT_FCGI_URL;
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("appid",TencetLiveUtil.CLOUD_TENCENT_APPID);
        params.put("interface", GetCTIntefaceName.SEARCH_GETSTATUS.value());
        params.put("Param.s.channel_id",streamCode);
        if(ParamUtil.isEmpty(time)){
            params.put("t",tencetLiveUtil.getValidTime());
        }else {
            params.put("t", tencetLiveUtil.getValidTime(time));
        }
        params.put("sign",tencetLiveUtil.getSign());
        String url=getApiUrl(apiUrl,params);
        logger.debug("查询直播状态url="+url);
        JSONObject json=HttpUtils.httpGet(url);
        logger.debug("查询直播状态返回结果json="+json);
        JSONObject result=new JSONObject();
        if(json!=null && !json.isEmpty()){
            if("0".equals(String.valueOf(json.get("ret")))){
                result.put("code",TipsMessage.SUCCESS_CODE);
                result.put("success",true);
                /**
                 * 输出output说明
                 * output":[
                                {
                                    "rate_type":0,     0：原始码率；10：普清；20：高清
                                    "recv_type":1,     1：rtmp/flv；2：hls；3：rtmp/flv+hls
                                    "status":0         0：断流；1：开启；3：关闭
                                }
                            ],
                 */
                result.put("liveData",json.get("output"));
            }else{
                result.put("code",TipsMessage.FAILED_RETURN);
                result.put("success",false);
                result.put("errMsg",json.get("message").toString());
            }
        }else{
            getConnectionFailedResult(result);
        }
        return result;
    }

    /**
     * 查询统计信息
     * @param type         接口查询类型，all查询Get_LiveStat；push查询Get_LivePushStat；play查询Get_LivePlayStat -----非必需
     * @param streamCode   直播码,如不设置 stream_id：查询所有正在直播中的流 -----非必需
     * @param domain       拉流域名,即播放域名，如果不填则返回所有域名的播放数据 -----非必需
     * @param currentPage  分页页码	,从 1 开始，默认为 1 -----非必需
     * @param pageSize     分页大小,1~300，默认为 300 -----非必需
     * @return
     *
     * 接口描述:
     * 接口:
     *      Get_LiveStat：查询指定直播流的推流和播放信息；
     *      Get_LivePushStat ：仅返回推流统计信息以提高查询效率；
     *      Get_LivePlayStat ：仅返回播放统计信息以提高查询效率。
     * 说明: 统计数据均为查询时间点的瞬时统计数据，而并非历史累计数据；
     *       如果目标流不在直播中，则返回结果中的 output 字段为空；
     *       推流信息的统计数据每 5 秒钟更新一次，也就是快于 5 秒的查询频率是浪费的；
     *       播放信息的统计数据每 1 分钟更新一次，也就是快于 60 秒的查询频率是浪费的。
     * 用途: 查询某条直播流的统计信息（如观看人数、带宽、码率、帧率等）；
     *       查询当前正在直播状态中的若干条直播流的统计信息（建议采用分页查询避免单次回包数据过大）。
     * 注：
     *    统计接口目前尚处于 Beta 阶段，并未全员放开，未开通即调用此接口会收到【cmd is invalid】提示，如您急需请联系我们。
     */
    public JSONObject getLiveStatatis(String type,String streamCode,String domain,String currentPage,String pageSize){
        String apiUrl= TencetLiveUtil.CLOUD_TENCENT_STATCGI_URL;
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("cmd",TencetLiveUtil.CLOUD_TENCENT_APPID);
        String interfaceName="";
        switch (type){
            case "all":
                interfaceName= GetCTIntefaceName.SEARCH_LIVESTAT.value();
                break;
            case "push":
                interfaceName= GetCTIntefaceName.SEARCH_LIVEPUSHSTAT.value();
                break;
            case "play":
                interfaceName= GetCTIntefaceName.SEARCH_LIVEPLAYSTAT.value();
                break;
            default:
                interfaceName= GetCTIntefaceName.SEARCH_LIVESTAT.value();
        }
        params.put("interface",interfaceName);
        if(ParamUtil.isEmpty(currentPage)) {
            currentPage=CURRENTPAGE;
        }
        params.put("Param.n.page_no", Integer.parseInt(currentPage));
        if(ParamUtil.isEmpty(pageSize)) {
            pageSize=STATS_PAGESIZE;
        }
        params.put("Param.n.page_size", Integer.parseInt(pageSize));
        if(ParamUtil.isEmpty(streamCode)){
            streamCode="";
        }
        params.put("Param.s.stream_id",streamCode);
        if(ParamUtil.isEmpty(domain)){
            domain=tencetLiveUtil.getTencentDomain();
        }
        params.put("Param.s.pull_domain",domain);
        params.put("t",tencetLiveUtil.getValidTime());
        params.put("sign",tencetLiveUtil.getSign());
        String url=getApiUrl(apiUrl,params);
        logger.debug("查询统计信息url="+url);
        JSONObject json=HttpUtils.httpGet(url);
        logger.debug("查询统计信息返回结果json="+json);
        JSONObject result=new JSONObject();
        if(json!=null && !json.isEmpty()){
            if("0".equals(String.valueOf(json.get("ret")))){
                result.put("code",TipsMessage.SUCCESS_CODE);
                result.put("success",true);
                JSONObject returnJson=JSONObject.parseObject(json.get("output").toString());
                if(returnJson!=null && !returnJson.isEmpty()){
                    //所有在线的直播流数量
                    result.put("streamCount",String.valueOf(returnJson.get("stream_count")==null?"0": returnJson.get("stream_count")));
                    //直播流统计信息
                    /**
                     * 输出stream_info说明
                     * stream_info":[
                                     {
                                         "stream_name":"",     	    直播码
                                         "bandwidth":1454545.0,     该直播流的瞬时带宽占用,单位:Mpbs
                                         "online":300,              该直播流的瞬时在线人数
                                         "client_ip":"",            推流客户端 IP
                                         "server_ip":"",            接流服务器 IP
                                         "fps":6589,                瞬时推流帧率
                                         "speed":5200               瞬时推流码率
                                     }
                                ],
                     */
                    result.put("streamInfo",String.valueOf(returnJson.get("stream_info")==null?"": returnJson.get("stream_info")));
                    //当前账号在查询时间点的总带宽
                    result.put("totalBandwidth",String.valueOf(returnJson.get("total_bandwidth")==null?"0": returnJson.get("total_bandwidth")));
                    //当前账号在查询时间点的总在线人数
                    result.put("totalOnline",String.valueOf(returnJson.get("total_online")==null?"0": returnJson.get("total_online")));
                }
            }else{
                result.put("code",TipsMessage.FAILED_RETURN);
                result.put("success",false);
                result.put("errMsg",json.get("message").toString());
            }
        }else{
            getConnectionFailedResult(result);
        }
        return result;
    }
    /**
     * 查询频道列表
     * @param status       0：表示断流，1：表示开启，3：表示关闭----非必需
     * @param field        排序字段,可选字段：create_time，默认为create_time----非必需
     * @param type         排序方法,0：表示正序，1：表示倒序----非必需
     * @param currentPage  分页页码，从 1 开始，默认为 1----非必需
     * @param pageSize     分页大小,10~100，默认为 10----非必需
     * @return
     *
     * 接口描述:
     * 用途：在直播码模式下，用于查询当前频道列表。
     * 说明：可以查询特定状态的频道列表，如可过滤当前处于开启状态的频道。
     */
    public JSONObject getChannelList(String status,String field,String type,String currentPage,String pageSize){
        String apiUrl= TencetLiveUtil.CLOUD_TENCENT_FCGI_URL;
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("appid",TencetLiveUtil.CLOUD_TENCENT_APPID);
        params.put("interface", GetCTIntefaceName.SEARCH_GETCHANNELlIST.value());
        params.put("t",tencetLiveUtil.getValidTime());
        params.put("sign",tencetLiveUtil.getSign());
        if(!ParamUtil.isEmpty(status)) {
            params.put("Param.n.status", Integer.parseInt(status));
        }
        if(ParamUtil.isEmpty(currentPage)) {
            currentPage=CURRENTPAGE;
        }
        params.put("Param.n.page_no", Integer.parseInt(currentPage));
        if(ParamUtil.isEmpty(pageSize)) {
            pageSize=PAGESIZE;
        }
        params.put("Param.n.page_size", Integer.parseInt(pageSize));
        if(!ParamUtil.isEmpty(field)) {
            params.put("Param.s.order_field", field);
        }
        if(!ParamUtil.isEmpty(type)) {
            params.put("Param.n.order_by_type", Integer.parseInt(type));
        }
        String url=getApiUrl(apiUrl,params);
        logger.debug("查询频道列表url="+url);
        JSONObject json= HttpUtils.httpGet(url);
        logger.debug("查询频道列表返回数据json="+json);
        JSONObject result=new JSONObject();
        if(json!=null && !json.isEmpty()){
            //成功获取返回值
            if("0".equals(String.valueOf(json.get("ret")))){
                JSONObject channelJson=JSONObject.parseObject(json.get("output").toString());
                if(channelJson!=null && !channelJson.isEmpty()){
                    result.put("code",TipsMessage.SUCCESS_CODE);
                    result.put("success",true);
                    result.put("totalCount",channelJson.get("all_count")==null? 0:channelJson.get("all_count"));
                    result.put("channelList",channelJson.get("channel_list")==null?"":channelJson.get("channel_list"));
                }
            }else{
                result.put("code",TipsMessage.FAILED_RETURN);
                result.put("success",false);
                result.put("errMsg",json.get("message").toString());
                result.put("totalCount","");
                result.put("channelList","");
            }
        }else{
            getConnectionFailedResult(result);
        }
        return result;
    }

    /**
     * 获取正在直播的频道列表
     * @return
     *
     * 接收描述:
     * 说明：在直播码模式下，用于查询直播中频道列表。
     */
    public JSONObject getLiveChannelList(){
        String apiUrl= TencetLiveUtil.CLOUD_TENCENT_FCGI_URL;
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("appid",TencetLiveUtil.CLOUD_TENCENT_APPID);
        params.put("interface", GetCTIntefaceName.SEARCH_GETLIVECHANNELlIST.value());
        params.put("t",tencetLiveUtil.getValidTime());
        params.put("sign",tencetLiveUtil.getSign());
        String url=getApiUrl(apiUrl,params);
        logger.debug("获取正在直播的频道列表url="+url);
        JSONObject json=HttpUtils.httpGet(url);
        logger.debug("获取正在直播的频道列表返回结果json="+json);
        JSONObject result=new JSONObject();
        if(json!=null && !json.isEmpty()){
            //成功获取返回值
            if("0".equals(String.valueOf(json.get("ret")))){
                JSONObject channelJson=JSONObject.parseObject(json.get("output").toString());
                if(channelJson!=null && !channelJson.isEmpty()){
                    result.put("code",TipsMessage.SUCCESS_CODE);
                    result.put("success",true);
                    result.put("totalCount",channelJson.get("all_count")==null? 0:channelJson.get("all_count"));
                    List<String> channelList=new ArrayList<>();
                    if(channelJson.get("channel_list")!=null && !"".equals(channelJson.get("channel_list").toString())){
                        String channels = channelJson.get("channel_list").toString();
                        channels=channels.substring(1,channels.length()-1);
                        String channelArray[] = channels.split(",");
                        for(String channelId:channelArray){
                            channelId=channelId.substring(1,channelId.length()-1);
                            channelList.add(channelId);
                        }
                    }
                    result.put("streamCodes",channelList);
                }
            }else{
                result.put("code",TipsMessage.FAILED_RETURN);
                result.put("success",false);
                result.put("errMsg",json.get("message").toString());
                result.put("totalCount","");
                result.put("streamCodes","");
            }
        }else{
            getConnectionFailedResult(result);
        }
        return result;
    }

    /**
     * 获取计费带宽数据
     * @param startTime       查询起始时间,时间格式:2018-08-01 10:00:00
     * @param endTime         查询结束时间,时间格式:2018-08-01 10:30:00
     * @param domain          域名------非必需
     * @param homeForeign     国内外的数据  0：不区分国内外，查询总数据（default） 1：只查询国内数据  2：只查询国外数据 ------非必需
     * @param topBd           获取峰值带宽  0：不是获取峰值带宽和流量，而是列表（default） 1：获取峰值带宽和流量 ------非必需
     * @return
     *
     * 接口描述:
     * 说明:使用该接口需要后台配置，如需调用该接口，请联系腾讯商务人员或者 提交工单，联系电话：4009-100-100
     */
    public JSONObject getBillingBandWidth(String startTime,String endTime,String domain,String homeForeign,String topBd){
        String apiUrl= TencetLiveUtil.CLOUD_TENCENT_STATCGI_URL;
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("cmd",TencetLiveUtil.CLOUD_TENCENT_APPID);
        params.put("interface", GetCTIntefaceName.SEARCH_GETBANDWIDTH.value());
        if(ParamUtil.isEmpty(domain)){
            domain=tencetLiveUtil.getTencentDomain();
        }
        params.put("Param.s.domain",domain);
        params.put("t",tencetLiveUtil.getValidTime());
        params.put("sign",tencetLiveUtil.getSign());
        //时间转换成时间戳
        params.put("Param.n.start_time",convertTimeStamp(startTime));
        params.put("Param.n.end_time",convertTimeStamp(endTime));
        if(ParamUtil.isEmpty(homeForeign)){
            homeForeign="";
        }
        params.put("Param.n.home_foreign",homeForeign);
        if(ParamUtil.isEmpty(topBd)){
            topBd="";
        }
        params.put("Param.n.get_top_bd",topBd);
        String url=getApiUrl(apiUrl,params);
        logger.debug("获取计费带宽数据url="+url);
        JSONObject json=HttpUtils.httpGet(url);
        logger.debug("获取计费带宽数据返回结果json="+json);
        JSONObject result=new JSONObject();
        if(json!=null && !json.isEmpty()){
            if("0".equals(String.valueOf(json.get("ret")))){
                result.put("code",TipsMessage.SUCCESS_CODE);
                result.put("success",true);
                JSONObject returnJson=JSONObject.parseObject(json.get("output").toString());
                if(returnJson!=null && !returnJson.isEmpty()){
                    //域名，传入参后才有
                    result.put("domain",returnJson.get("domain")==null?"":returnJson.get("domain"));
                    //计费总带宽统计信息,5分钟粒度
                    /**
                     * total_Info说明
                     * total_Info":[
                                        {
                                            "time":"",   统计时间
                                            "bandwidth": 200.0 Mbps,   带宽
                                            "flux":300.0 MB            流量
                                         }
                                     ],
                     */
                    result.put("totalInfo",returnJson.get("total_info")==null?"":returnJson.get("total_info"));
                }
            }else{
                result.put("code",TipsMessage.FAILED_RETURN);
                result.put("success",false);
                result.put("errMsg",json.get("message").toString());
            }
        }else{
            getConnectionFailedResult(result);
        }
        return null;
    }

    /**
     * 获取推流历史信息
     * @param streamCode    直播码
     * @param startTime   查询起始时间,3 天内的数据时间戳
     * @param endTime     查询终止时间,建议查询跨度不大于 2 小时时间戳
     * @return
     *
     * 接口描述：
     * 说明:可获取指定时间段内推流信息；
     *      推流信息的统计数据每 5 秒钟更新一次。
     *      使用该接口需要后台配置，如需调用该接口，请联系腾讯商务人员或者 提交工单，联系电话：4009-100-100。
     */
    public JSONObject getLivePushStatHistory(String streamCode,String startTime,String endTime){
        String apiUrl= TencetLiveUtil.CLOUD_TENCENT_STATCGI_URL;
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("cmd",TencetLiveUtil.CLOUD_TENCENT_APPID);
        params.put("interface", GetCTIntefaceName.STATS_LIVEPUSHSTATHISTORY.value());
        //时间转换成时间戳
        params.put("Param.n.start_time",convertTimeStamp(startTime));
        params.put("Param.n.end_time",convertTimeStamp(endTime));
        params.put("Param.s.stream_id",streamCode);
        params.put("t",tencetLiveUtil.getValidTime());
        params.put("sign",tencetLiveUtil.getSign());
        String url=getApiUrl(apiUrl,params);
//        System.out.println("获取推流历史记录url="+url);
        logger.debug("获取推流历史记录url="+url);
        JSONObject json=HttpUtils.httpGet(url);
        logger.debug("获取推流历史记录返回结果json="+json);
        JSONObject result=new JSONObject();
        if(json!=null && !json.isEmpty()){
            if("0".equals(String.valueOf(json.get("ret")))){
                result.put("code",TipsMessage.SUCCESS_CODE);
                result.put("success",true);
                JSONObject returnJson=JSONObject.parseObject(json.get("output").toString());
                if(returnJson!=null && !returnJson.isEmpty()){
                    /**
                     * 推流时间流统计信息stream_Info说明
                     * stream_Info":[
                                         {
                                             "time":"",   推流时刻
                                             "client_ip": "",   推流客户端ip
                                             "server_ip":"",    接流服务器ip
                                             "fps":123354 ,     推流帧率
                                             "speed":12134633   推流码率，单位：bps
                                         }
                                    ],
                     */
                    result.put("streamInfo",returnJson.get("stream_info")==null?"":returnJson.get("stream_info"));
                }
            }else{
                result.put("code",TipsMessage.FAILED_RETURN);
                result.put("success",false);
                result.put("errMsg",json.get("message").toString());
            }
        }else{
            getConnectionFailedResult(result);
        }
        return result;
    }

    /**
     * 获取播放历史信息
     * @param streamCode    直播码,不填就是获取总带宽
     * @param startTime   查询起始时间,15天内的数据时间戳
     * @param endTime     查询终止时间,建议查询跨度不大于 2 小时时间戳
     * @return
     *
     * 接口描述：
     * 说明:    可获取指定时间段内播放信息。
     *          播放信息的统计数据每 1 分钟更新一次。
     *          该接口支持直播码模式，同时也支持频道模式。
     *          使用该接口需要后台配置，如需调用该接口，请联系腾讯商务人员或者 提交工单，联系电话：4009-100-100。
     */
    public JSONObject getLivePlayStatHistory(String streamCode,String startTime,String endTime){
        String apiUrl= TencetLiveUtil.CLOUD_TENCENT_STATCGI_URL;
        Map<String,Object> params=new LinkedHashMap<>();
        params.put("cmd",TencetLiveUtil.CLOUD_TENCENT_APPID);
        params.put("interface", GetCTIntefaceName.STATS_LIVEPLAYSTATHISTORY.value());
        //时间转换成时间戳
        params.put("Param.n.start_time",convertTimeStamp(startTime));
        params.put("Param.n.end_time",convertTimeStamp(endTime));
        if(ParamUtil.isEmpty(streamCode)){
            streamCode="";
        }
        params.put("Param.s.stream_id",streamCode);
        params.put("Param.s.domain",tencetLiveUtil.getTencentDomain());
        params.put("t",tencetLiveUtil.getValidTime());
        params.put("sign",tencetLiveUtil.getSign());
        String url=getApiUrl(apiUrl,params);
//        System.out.println("获取接流历史信息url="+url);
        logger.debug("获取接流历史信息url="+url);
        JSONObject json=HttpUtils.httpGet(url);
        logger.debug("获取接流历史信息返回结果json="+json);
        JSONObject result=new JSONObject();
        if(json!=null && !json.isEmpty()){
            if("0".equals(String.valueOf(json.get("ret")))){
                result.put("code",TipsMessage.SUCCESS_CODE);
                result.put("success",true);
                JSONObject returnJson=JSONObject.parseObject(json.get("output").toString());
                if(returnJson!=null && !returnJson.isEmpty()){
                    /**
                     * 直播流的统计信息stat_Info说明
                     * stat_info":[
                                         {
                                             "time":"",    统计时间
                                             "bandWidth": 200.0,   带宽，单位:Mbps
                                             "online":500,      在线人数
                                             "flux":1000.0      流量,单位MB
                                         }
                                  ],
                     */
                    result.put("statInfo",returnJson.get("stat_info")==null?"":returnJson.get("stat_info"));
                    /**
                     * 流量总和sum_flux说明
                     * sum_flux":[
                                     {
                                         "sum_flux":568546.0    流量总和，单位:MB
                                     }
                                 ],
                     */
                    float trafficValue=0;
                    if(returnJson.get("sum_info")!=null && !"".equals(String.valueOf(returnJson.get("sum_info")))){
                        JSONObject flux=JSONObject.parseObject(String.valueOf(returnJson.get("sum_info")));
                        trafficValue=Float.parseFloat(String.valueOf(flux.get("sum_flux")));
                    }
                    //总流量
                    result.put("sumTrafficValue",trafficValue);
                    result.put("domain",returnJson.get("domain")==null?"":returnJson.get("domain"));
                    result.put("streamCode",returnJson.get("stream_id")==null?"":returnJson.get("stream_id"));
                }
            }else{
                result.put("code",TipsMessage.FAILED_RETURN);
                result.put("success",false);
                result.put("errMsg",json.get("message").toString());
            }
        }else{
            getConnectionFailedResult(result);
        }
        return result;
    }
    /**
     * 获取api接口url
     * @param url      api地址
     * @param params   参数
     * @return
     */
    private String getApiUrl(String url,Map<String,Object> params){
        String apiUrl= url+ "?" +ConvertMapToString.getKeyAndValue(params);
        return apiUrl;
    }
    /**
     * 提取出公共的返回结果
     * @param json     api返回结果
     * @param result   处理返回结果
     * @return
     */
    private JSONObject getReturnResult(JSONObject json,JSONObject result){
        if(json!=null && !json.isEmpty()){
            if("0".equals(String.valueOf(json.get("ret")))){
                result.put("code",TipsMessage.SUCCESS_CODE);
                result.put("success",true);
            }else{
                result.put("code",TipsMessage.FAILED_RETURN);
                result.put("success",false);
                result.put("errMsg",json.get("message").toString());
            }
        }else{
            getConnectionFailedResult(result);
        }
        return result;
    }

    /**
     * 返回连接错误提示
     * @param result
     * @return
     */
    private JSONObject getConnectionFailedResult(JSONObject result){
        result.put("code",TipsMessage.FAILED_CODE);
        result.put("success",false);
        result.put("errMsg",TipsMessage.FAILED_MSG);
        return result;
    }

    /**
     * 时间转换成unix时间戳
     * @param time   格式：yyyy-MM-dd HH:mm:ss
     * @return
     */
    private int convertTimeStamp(String time){
        int timeStamp=0;
        try {
            timeStamp=new Long(DateUtil.parseDatetime(time).getTime()/1000).intValue();
        } catch (ParseException e) {
            timeStamp=0;
        }
        return timeStamp;
    }


    /**
     * 获取腾讯云api接口方法名
     */
   private enum GetCTIntefaceName{
        //开启关闭推流
        OPERATION_SETSTATUS("Live_Channel_SetStatus"),
        //暂停并延迟恢复
        OPERATION_CHANNELMANAGER("channel_manager"),
        //延播
        OPERATION_SETDELAY("Live_Set_Delay"),
        //查询直播状态
        SEARCH_GETSTATUS("Live_Channel_GetStatus"),
        //查询统计信息：查询指定直播流的推流和播放信息
        SEARCH_LIVESTAT("Get_LiveStat"),
        //查询统计信息：仅返回推流统计信息以提高查询效率
        SEARCH_LIVEPUSHSTAT("Get_LivePushStat"),
        //查询统计信息：仅返回播放统计信息以提高查询效率
        SEARCH_LIVEPLAYSTAT("Get_LivePlayStat"),
        //查询频道列表
        SEARCH_GETCHANNELlIST("Live_Channel_GetChannelList"),
        //查询直播中频道列表
        SEARCH_GETLIVECHANNELlIST("Live_Channel_GetLiveChannelList"),
        //获取计费宽带数据
        SEARCH_GETBANDWIDTH("Get_BillingBandwidth"),
        //获取推流历史信息
        STATS_LIVEPUSHSTATHISTORY("Get_LivePushStatHistory"),
        //获取播放统计历史信息
        STATS_LIVEPLAYSTATHISTORY("Get_LivePlayStatHistory");

        private String intefaceName = "";

        GetCTIntefaceName(String intefaceName){
            this.intefaceName = intefaceName;
        }

        public String value(){
            return this.intefaceName;
        }
    }
}
