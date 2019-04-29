package com.honghe.livemanager.cache;


import java.util.concurrent.ConcurrentHashMap;

/**
 * 直播截图数量缓存
 *
 * @Author libing
 * @Date: 2018-09-25 13:28
 * @Mender:
 */
public class LivePicCountCache {
    private static ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap<>();



    /**
     * 增加，每次+1。
     * @param streamCode
     * @return
     */
    public  static void increase(String streamCode){
        //增加1
        increase(streamCode,1);
    }
    /**
     * 增加，每次increase。
     * @param streamCode
     * @return
     */
    private  static void increase(String streamCode,int increase){
        //增加1
        map.merge(streamCode,increase,Integer::sum);
   }

    /**
     *
     * 获取直播截图数量
     * @param streamCode
     */
    public  static int getCount(String streamCode){
        Integer value  = map.get(streamCode);
        return null==value?0:value;
    }
    /**
     *
     * 获取直播截图数量
     * @param streamCode
     */
    public  static void remove(String streamCode){
        map.remove(streamCode);
    }


}

