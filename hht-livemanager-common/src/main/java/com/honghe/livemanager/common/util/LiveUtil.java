package com.honghe.livemanager.common.util;

import java.util.Map;

public interface LiveUtil {


    /**
     * 获取过期时间
     * @return
     */
    int getValidTime();
    /**
     * 获取过期时间
     * @param  time   过期时间，格式:yyyy-MM-dd HH:mm:ss
     * @return
     */
    int getValidTime(String time);

    /**
     * 获取签名
     * @return
     */
    String  getSign();
    String  getSign(Map<String,Object> map);

    /**
     * 获取直播推流、播放地址
     * @param streamCode    直播码
     * @param time        过期时间
     * @return
     */
    Map<String,String> getLiveStreamUrl(String streamCode,String time);

    /**
     * 获取直播码
     * @return
     */
    String getStreamCode();

    /**
     * 获取直播域名
     * @return
     */
    String getTencentDomain();
}
