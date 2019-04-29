package com.honghe.livemanager.dao;

import com.honghe.livemanager.entity.LiveOperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LiveOperationLogDao {
    /**
     * 保存操作日志
     * @param log
     * @return
     */
    int addOperationLog(LiveOperationLog log);

    /**
     * 分页查询日志
     * @param beginTime   查询开始时间，格式:yyyy-MM-dd
     * @param endTime     查询结束时间，格式:yyyy-MM-dd
     * @param start       起始页
     * @param pageSize    每页大小
     * @param pageFlag    true:分页；false:不分页
     * @return
     */
    List<LiveOperationLog> getLogByPage(@Param("beginTime") String beginTime,@Param("endTime") String endTime,
                                        @Param("start")int start,@Param("pageSize")int pageSize,
                                        @Param("pageFlag") boolean pageFlag);

    /**
     * 获取操作日志总条数
     * @return
     */
    int getOperationLogSum();

    /**
     * 清空操作日志
     * @return
     */
    int deleteLog(int maxCount);

    /**
     * 查询分页日志的总条数
     * @param beginTime   查询开始时间，格式:yyyy-MM-dd
     * @param endTime     查询结束时间，格式:yyyy-MM-dd
     * @return
     */
    int getLogCountByPage(@Param("beginTime") String beginTime,@Param("endTime") String endTime);
}
