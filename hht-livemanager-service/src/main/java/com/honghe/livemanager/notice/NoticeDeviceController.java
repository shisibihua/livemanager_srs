package com.honghe.livemanager.notice;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.cloud.srs.util.SRSLiveUtil;
import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.common.util.ConfigUtil;
import com.honghe.livemanager.common.util.DateUtil;
import com.honghe.livemanager.common.util.HttpUtils;
import com.honghe.livemanager.common.util.SpringUtil;
import com.honghe.livemanager.dao.LiveDao;
import com.honghe.livemanager.entity.Live;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * 通知
 * @author caoqian
 * @date 2018/11/23
 */
@Configuration
@Component
public class NoticeDeviceController {
    private static final int PUSH_CONTROLLER = 0;
    private static final int STOP_CONTROLLER = 1;
    private static final int RETRY_COUNT = 3;
    private SRSLiveUtil srsLiveUtil= new SRSLiveUtil();
    private static NoticeDeviceController INSTANCE=null;
    public static NoticeDeviceController getInstance(){
        if(INSTANCE==null){
            synchronized (NoticeDeviceController.class){
                if(INSTANCE==null){
                    INSTANCE=new NoticeDeviceController();
                }
            }
        }
        return INSTANCE;
    }
    private Logger logger= LoggerFactory.getLogger(NoticeDeviceController.class);

    /**
     * 通知录播主机推流、断流
     * @param live
     * @return
     */
    public boolean noticePushStream(List<Live> live,String operationType){
        //保存需要推流的推流地址
        List<String> pushStreamCodeList=new ArrayList<>();
        if(live!=null && !live.isEmpty()){
            for(Live l:live){
                System.out.println("操作方式："+operationType+">>>>>>>>>>直播id="+l.getLiveId());
                String now= DateUtil.formatDatetime(new Date());
                String nowTime=now.substring(0,now.lastIndexOf(":"));
                String time="";
                switch (operationType){
                    case "push": {
                        //提前一分钟推流
                        time = changeTime(l.getBeginTime(), -1);
                        break;
                    }
                    case "stop": {
                        //晚1分钟断流
                        time = changeTime(l.getEndTime(), 1);
                        break;
                    }
                }

                if(time.equals(nowTime)){
                    pushStreamCodeList=getStreamCodeList(l);
                    System.out.println("直播时间到了，开始准备通知录播主机，直播码code="+l.getStreamCode());
                }
            }
        }
        //调用资源平台接口，获取录播主机ip（推流主机）
        if (!pushStreamCodeList.isEmpty()){
            //获取录播主机ip后，调用集控接口，通知主机推流
            String deviceIpsAndUrls=getDeviceIpsByPushStreamUrls(StringUtil.join(pushStreamCodeList,","));
            if(!StringUtil.isEmpty(deviceIpsAndUrls)){
                String interfaceIp= ConfigUtil.getInstance().getPropertyValue("device_controller_ip");
                String interfacePort= ConfigUtil.getInstance().getPropertyValue("device_controller_port");
                String interfaceUrl= ConfigUtil.getInstance().getPropertyValue("device_controller_url");
                String deviceControllerUrl="http://"+interfaceIp+":"+interfacePort+interfaceUrl;
                switch (operationType){
                    case "push":
                        return noticeDeviceControllerPushStream(deviceControllerUrl,deviceIpsAndUrls);
                    case "stop":
                        return noticeDeviceControllerStopPushStream(deviceControllerUrl,deviceIpsAndUrls);
                }
            }
        }
        return false;
    }

    /**
     * 根据推流地址获取录播主机ip
     * @param urls  推流地址，多个","分割，如"a,b,c,d"
     * @return
     */
    private String getDeviceIpsByPushStreamUrls(String urls){
        String re_values="";
        if(StringUtil.isEmpty(urls)){
            return String.valueOf(Result.Code.ParamError.value());
        }else{
            JSONObject params=new JSONObject();
            params.put("pushUrls",urls);
            String interfaceIp= ConfigUtil.getInstance().getPropertyValue("resource_ip");
            String interfacePort= ConfigUtil.getInstance().getPropertyValue("resource_port");
            String interfaceUrl= ConfigUtil.getInstance().getPropertyValue("resource_url");
            String resourceUrl="http://"+interfaceIp+":"+interfacePort+interfaceUrl;
            JSONObject result = HttpUtils.httpPostOutTimeAndRetry(resourceUrl,params.toString(),RETRY_COUNT);
            if(result!=null && !result.isEmpty()){
                try {
                    re_values=result.get("result").toString();
                }catch (JSONException e){
                    return null;
                }
            }
        }
        return re_values;
    }
   private final static String DEVICE_CONTROLLER_CMD="liveManager";
   private final static String DEVICE_CONTROLLER_PUSH="startLiveByOthers";
   private final static String DEVICE_CONTROLLER_STOP="stopLiveByOthers";
    /**
     * 通知集控平台推流
     * @param  url   接口地址
     * @param params  包括录播主机ip及推流地址，如[{"deviceIp":"192.168.16.10","pushPath":"a"},{"deviceIp":"192.168.16.11,"pushPath":"b"}]
     * @return
     */
    private boolean noticeDeviceControllerPushStream(String url,String params){
        if (StringUtil.isEmpty(params)){
            return false;
        }else{
            JSONObject parmasMap=new JSONObject();
            parmasMap.put("cmd",DEVICE_CONTROLLER_CMD);
            parmasMap.put("cmd_op",DEVICE_CONTROLLER_PUSH);
            parmasMap.put("deviceFilmList",params);
            JSONObject result=HttpUtils.httpPostOutTimeAndRetry(url,parmasMap.toString(),RETRY_COUNT);
            System.out.println("通知集控平台推流，params="+params);
            noticeControllerPlatform(PUSH_CONTROLLER,result);
            return true;
        }
    }

    /**
     * 通知集控平台断流
     * @param  url   接口地址
     * @param params  包括录播主机ip及推流地址，如[{"deviceIp":"192.168.16.10","pushPath":""}]
     * @return
     */
    private boolean noticeDeviceControllerStopPushStream(String url,String params){
        if (StringUtil.isEmpty(params)){
            return false;
        }else{
            JSONObject parmasMap=new JSONObject();
            parmasMap.put("cmd",DEVICE_CONTROLLER_CMD);
            parmasMap.put("cmd_op",DEVICE_CONTROLLER_STOP);
            parmasMap.put("deviceFilmList",params);
            JSONObject result=HttpUtils.httpPostOutTimeAndRetry(url,parmasMap.toString(),RETRY_COUNT);
            System.out.println("通知集控平台断流，params="+params);
            noticeControllerPlatform(STOP_CONTROLLER,result);
            return true;
        }
    }

    /**
     * 获取推流地址list
     * @param live  直播
     * @return
     */
   private List<String>  getPushStreamUrlList(Live live){
        List<String> pushStreamUrlList=new ArrayList<>();
        Map<String, String> streamUrl=srsLiveUtil.getLiveStreamUrl(live.getStreamCode());
        if(streamUrl!=null && !streamUrl.isEmpty()) {
           pushStreamUrlList.add(streamUrl.get("livePushUrl"));
        }
        return pushStreamUrlList;
   }

    /**
     * 获取直播码集合
     * @param live
     * @return
     */
    private List<String>  getStreamCodeList(Live live){
        List<String> streamCodeList=new ArrayList<>();
        if(!StringUtil.isEmpty(live.getStreamCode())){
            streamCodeList.add(live.getStreamCode());
        }
        return streamCodeList;
    }
    /**
     * 修改时间
     * @param liveTime  直播时间
     * @param amount    改变的时间
     * @return
     */
    private String changeTime(String liveTime,int amount){
        Calendar calendar=Calendar.getInstance();
        try {
            calendar.setTime(DateUtil.parseDatetime(liveTime));
        } catch (ParseException e) {
            return liveTime;
        }
        calendar.add(Calendar.MINUTE,amount);
        Date date=calendar.getTime();
        String time=DateUtil.formatDatetime(date);
        return time.substring(0,time.lastIndexOf(":"));
    }

   /* public static void main(String[] args) {
        String str="{\n" +
                "    \"type\": \"startLiveByOthers_ACK\",\n" +
                "    \"code\": 0,\n" +
                "    \"msg\": \"\",\n" +
                "    \"result\": {\n" +
                "        \"offline\": [\n" +
                "            \"rtmp://192.168.11.11:1935/live/hht_00dbd3220b91449995fe5b5056a1d17e?vhost=hht\",\n" +
                "            \"rtmp://192.168.21.139:1935/live/hht_00dbd3220b91449995fe5b5056a1d17e?vhost=hht\"\n" +
                "        ],\n" +
                "        \"successList\": [],\n" +
                "        \"failList\": []\n" +
                "    },\n" +
                "    \"other\": {\n" +
                "        \"msg\": null\n" +
                "    }\n" +
                "}";
        JSONObject result=JSONObject.parseObject(str);
        NoticeDeviceController noticeDeviceController=NoticeDeviceController.getInstance();
        noticeDeviceController.saveNoticeResult(0,"offline",JSONObject.parseObject(result.get("result").toString()),null);
    }*/
    private void noticeControllerPlatform(int controlType,JSONObject resultJson){
        if(resultJson!=null && !resultJson.isEmpty()){
            JSONObject json=JSONObject.parseObject(resultJson.get("result").toString());
            logger.debug("录播主机推流/断流结果，resultJson="+json+",controlType="+controlType);
            if(json!=null && !json.isEmpty()){
                LiveDao liveDao= (LiveDao) SpringUtil.getBean("liveDao");
                saveNoticeResult(controlType,"successList",json,liveDao);
                saveNoticeResult(controlType,"failList",json,liveDao);
                saveNoticeResult(controlType,"offline",json,liveDao);
            }
        }
    }

    /**
     * 保存通知录播主机结果
     * @param controlType
     * @param param
     * @param json
     * @return
     */
    private boolean saveNoticeResult(int controlType,String param,JSONObject json,LiveDao liveDao){
        if(json!=null && json.containsKey(param)) {
            String status=getNoticeResultStatus(controlType,param);
            String[] streamUrl = (String[]) json.get(param).toString().replace("[", "").replace("]", "").
                    replace("\"","").split(",");
            List<String> codeList = interceptStreamCode(streamUrl);
            if (codeList != null && !codeList.isEmpty()) {
                return liveDao.updateControllerStatus(status, codeList);
            }
        }
        return false;
    }
    /**
     * 截取集控返回的直播码
     * @param streamUrlArr
     * @return
     */
    private List<String> interceptStreamCode(String[] streamUrlArr){
        List<String> streamCodeList=new ArrayList<>();
        if(streamUrlArr.length>0){
            for(String streamUrl:streamUrlArr){
                if(!StringUtil.isEmpty(streamUrl)) {
                    streamUrl = streamUrl.replace("?vhost=hht", "");
                    streamUrl = streamUrl.substring(streamUrl.lastIndexOf("/live/") + 6);
                    streamCodeList.add(streamUrl);
                }
            }
        }
        return streamCodeList;
    }

    private String getNoticeResultStatus(int controlType, String param) {
        String type="";
        if(0==controlType){
            if("successList".equals(param)){
                type="01";
            }else if("failList".equals(param)){
                type="00";
            }else{
                type="offline";
            }
        }else if(1==controlType){
            if("successList".equals(param)){
                type="11";
            }else if("failList".equals(param)){
                type="10";
            }else{
                type="offline";
            }
        }
        return type;
    }
}
