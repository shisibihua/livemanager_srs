package com.honghe.livemanager.cloud.srs.util;

import com.honghe.livemanager.common.util.ParamUtil;
import com.honghe.livemanager.common.util.UUIDUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取SRS服务数据
 * @author  caoqian
 * @date 20181019
 */
@Component("localSRSLiveUtil")
public class SRSLiveUtil {
    public static String SRS_SERVER_IP = "127.0.0.1";
    public static int SRS_SERVER_API_PORT = 1985;
    public static int SRS_SERVER_PUSH_PORT = 1935;
    public static int SRS_SERVER_PLAY_PORT = 1955;

    /**
     * 生成SRS直播的推流、播放地址
     * @param streamCode 直播码
     * @return 直播地址
     */
    public Map<String,String> getLiveStreamUrl(String streamCode) {
        if(ParamUtil.isEmpty(streamCode)) {
            streamCode = getStreamCode();
        }
        Map<String,String>  liveStreamMap=new HashMap<>();
        //推流地址
        liveStreamMap.put("livePushUrl","rtmp://" + SRS_SERVER_IP + ":" + SRS_SERVER_PUSH_PORT + "/live/" + streamCode + "?vhost=hht");
        //播放地址,分为rtmp、flv、hls
        liveStreamMap.put("livePlayRtmpUrl","rtmp://" + SRS_SERVER_IP + ":" + SRS_SERVER_PLAY_PORT + "/live/" + streamCode + "?vhost=hht");
        liveStreamMap.put("livePlayRtmpUrl_720","rtmp://" + SRS_SERVER_IP + ":" + SRS_SERVER_PLAY_PORT + "/live/" + streamCode + "_low?vhost=hht");
//        liveStreamMap.put("livePlayFlvUrl","http://"+ SRS_SERVER_IP + ":" + SRS_SERVER_PLAY_PORT + "/hht/live/" + streamCode + ".flv");
        liveStreamMap.put("livePlayHlsUrl","http://"+ SRS_SERVER_IP + ":" + SRS_SERVER_PLAY_PORT + "/hht/live/" + streamCode + ".m3u8");
        liveStreamMap.put("livePlayHlsUrl_720","http://"+ SRS_SERVER_IP + ":" + SRS_SERVER_PLAY_PORT + "/hht/live/" + streamCode + "_low.m3u8");
        return liveStreamMap;
    }

    /**
     * 生成直播码
     * @return
     */
   public String getStreamCode(){
        return "hht_" + UUIDUtil.getUUID();
   }
}
