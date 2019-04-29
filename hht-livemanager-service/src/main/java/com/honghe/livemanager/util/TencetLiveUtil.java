package com.honghe.livemanager.util;

import com.honghe.livemanager.common.util.*;
import com.honghe.livemanager.dao.LiveConfigDao;
import com.honghe.livemanager.entity.LiveConfig;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取腾讯云数据
 */
@Component("tencetLiveUtil")
public class TencetLiveUtil implements LiveUtil{
    private org.slf4j.Logger logger = LoggerFactory.getLogger(TencetLiveUtil.class);
    public static String CLOUD_TENCENT_KEY="";
    public static String CLOUD_TENCENT_PUSH_KEY="";
    public static String CLOUD_TENCENT_BIZID="";
    public static String CLOUD_TENCENT_APPID="0";

    public static String CLOUD_TENCENT_FCGI_URL="http://fcgi.video.qcloud.com/common_access";
    public static String CLOUD_TENCENT_STATCGI_URL="http://statcgi.video.qcloud.com/common_access";

    private static String CLOUD_TENCENT_PUSHURL="livepush.myqcloud.com/live/";
    private static String CLOUD_TENCENT_PLAYURL="liveplay.myqcloud.com/live/";
    private static String CLOUD_TENCENT_PLAYDOMAIN="liveplay.myqcloud.com";

    private LiveConfigDao configDao=null;
    private LiveConfig liveConfig = null;



    @Override
    public String getSign() {
        String sign="";
        //获取当天有效时长
        int time=getValidTime();
        sign=MD5Util.MD5(CLOUD_TENCENT_KEY+time).toLowerCase();
        return sign;
    }

    @Override
    public String getSign(Map<String,Object> map) {
        String sign="";
        if(map!=null && !map.isEmpty()){
            String apiKey=map.get("appKey")==null?CLOUD_TENCENT_KEY:map.get("appKey").toString();
            int time=Integer.parseInt(String.valueOf(map.get("t")));
            sign=MD5Util.MD5(apiKey+time).toLowerCase();
        }else{
            sign=getSign();
        }
        return sign;
    }

    @Override
    public Map<String,String> getLiveStreamUrl(String streamCode,String time) {

        if(ParamUtil.isEmpty(streamCode)) {
            streamCode = getStreamCode();
        }
        Map<String,String>  liveStreamMap=new HashMap<>();
        //推流地址
        liveStreamMap.put("livePushUrl",getLivePushUrl(CLOUD_TENCENT_BIZID,streamCode,time));
        //播放地址,分为rtmp、flv、hls
        liveStreamMap.put("livePlayRtmpUrl","rtmp://"+getLivePlayUrl(CLOUD_TENCENT_BIZID,streamCode));
        liveStreamMap.put("livePlayFlvUrl","http://"+getLivePlayUrl(CLOUD_TENCENT_BIZID,streamCode)+".flv");
        liveStreamMap.put("livePlayHlsUrl","http://"+getLivePlayUrl(CLOUD_TENCENT_BIZID,streamCode)+".m3u8");
        return liveStreamMap;
    }




    @Override
    public int getValidTime() {
        Date now=new Date();
        Long validTime=0L;
        try {
            Date allDayDate=DateUtil.parseDatetime(DateUtil.formatDate(now)+" 23:59:59");
            validTime=allDayDate.getTime()/1000;
        } catch (ParseException e) {
            logger.error(this.getClass().getSimpleName()+"-----时间转换异常",e);
        }
        int time=new Long(validTime).intValue();
        return time;
    }
    @Override
    public int getValidTime(String time) {
        Long validTime=0L;
        try {
            Date allDayDate=DateUtil.parseDatetime(time);
            validTime=allDayDate.getTime()/1000;
        } catch (ParseException e) {
            logger.error(this.getClass().getSimpleName()+"-----时间转换异常",e);
        }
        int t=new Long(validTime).intValue();
        return t;
    }
    @Override
    public String getStreamCode(){
        return CLOUD_TENCENT_BIZID+"_"+UUIDUtil.getUUID();
    }

    @Override
    public String getTencentDomain(){
        return CLOUD_TENCENT_BIZID+"."+CLOUD_TENCENT_PLAYDOMAIN;
    }

    /**
     * 获取推流地址
     * @param bizid     直播码前缀，唯一值
     * @param streamCode  直播码
     * @param time      过期时间
     * @return
     */
    private String getLivePushUrl(String bizid,String streamCode,String time){
        StringBuffer pushStr=new StringBuffer();
        pushStr.append("rtmp://").append(bizid).append(".").append(CLOUD_TENCENT_PUSHURL);
        pushStr.append(streamCode).append("?").append("bizid=").append(bizid);
        int t= 0 ;
        if(ParamUtil.isEmpty(time)){
            t=getValidTime();
        }else{
            t=getValidTime(time);
        }
        pushStr.append("&").append(getSafeUrl(CLOUD_TENCENT_PUSH_KEY,streamCode,t));
        logger.debug("推流地址pushUrl="+pushStr.toString());
        return pushStr.toString();
    }

    /**
     * 获取播放地址
     * @param bizid     直播码前缀，唯一值
     * @param streamCode  直播码
     * @return
     */
    private String getLivePlayUrl(String bizid,String streamCode){
        StringBuffer playStr=new StringBuffer();
        playStr.append(bizid).append(".").append(CLOUD_TENCENT_PLAYURL).append(streamCode);
        logger.debug("播放地址playUrl="+playStr.toString());
        return playStr.toString();
    }

    //直接采用腾讯云代码，获取推流地址中的txSecret与txTime
    private String getSafeUrl(String key, String streamCode, long txTime) {
        String input = new StringBuilder().
                append(key).
                append(streamCode).
                append(Long.toHexString(txTime).toUpperCase()).toString();

        String txSecret = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            txSecret  = byteArrayToHexString(
                    messageDigest.digest(input.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return txSecret == null ? "" :
                new StringBuilder().
                        append("txSecret=").
                        append(txSecret).
                        append("&").
                        append("txTime=").
                        append(Long.toHexString(txTime).toUpperCase()).
                        toString();
    }
    private final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private String byteArrayToHexString(byte[] data) {
        char[] out = new char[data.length << 1];
        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

    /**
     * 时间转换成unix时间戳
     * @param time    时间
     * @return
     */
    private int convertTimeToUnixStamp(String time){
        if(!ParamUtil.isEmpty(time)){
            try {
                long t=DateUtil.parseDatetime(time).getTime()/1000;
                return new Long(t).intValue();
            } catch (ParseException e) {
                logger.error("时间转换成unix时间戳异常,time="+time);
            }
        }
        return 0;
    }


}
