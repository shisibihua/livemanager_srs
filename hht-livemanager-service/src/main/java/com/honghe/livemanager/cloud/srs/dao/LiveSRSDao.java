package com.honghe.livemanager.cloud.srs.dao;

import org.apache.ibatis.annotations.Param;


/**
 * Description:SRS直播数据的dao
 * Author：hyh
 * Date: 2018/10/17
 */
public interface LiveSRSDao {

    /**
     * 根据steamCode获取clientId
     * @param streamCode 直播码
     * @return clientId
     */
    Integer getClientId(@Param(value = "streamCode") String streamCode);

    /**
     * 根据streamCode获取streamId
     * @param streamCode 直播码
     * @return streamId
     */
    Integer getStreamId(@Param(value = "streamCode") String streamCode);

    /**
     * 根据streamCode获取vHostId
     * @param streamCode 直播码
     * @return vHostId
     */
    Integer getVHostId(@Param(value = "streamCode") String streamCode);

    /**
     * 插入一条流记录
     * @param streamCode 直播码
     * @param clientId SRS生成的客户端id
     * @param streamId SRS生成的流id
     * @return 影响行数
     */
    int insertStream(@Param(value = "streamCode") String streamCode,@Param(value = "clientId") int clientId,
                     @Param(value = "streamId") int streamId,@Param(value = "vHostId") int vHostId);

    /**
     * 根据直播码和时间获取直播id
     * @param streamCode 直播码
     * @param dateTime 当前时间
     * @return 直播id
     */
    Integer getLiveIdByTime(@Param(value = "streamCode")String streamCode, @Param(value = "dateTime")String dateTime);
}
