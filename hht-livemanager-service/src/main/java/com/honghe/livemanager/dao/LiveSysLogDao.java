package com.honghe.livemanager.dao;

import com.honghe.livemanager.entity.LiveSysLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LiveSysLogDao {
    /**
     * 保存系统日志
     * @param log
     * @return
     */
    int addSysLog(LiveSysLog log);

    /**
     * 批量保存系统日志
     * @param list
     * @return
     */
    int addSysLogBatch(List<LiveSysLog> list);

    /**
     * 分页查询日志
     * @param level       日志级别
     * @param beginTime   查询开始时间，格式:yyyy-MM-dd
     * @param endTime     查询结束时间，格式:yyyy-MM-dd
     * @param start       起始页
     * @param pageSize    每页大小
     * @param pageFlag    true:分页；false:不分页
     * @return
     */
    List<LiveSysLog> getLogByPage(@Param("level") String level,@Param("beginTime") String beginTime,
                                  @Param("endTime") String endTime,@Param("start") int start,
                                  @Param("pageSize") int pageSize,@Param("pageFlag")boolean pageFlag);

    /**
     * 获取系统日志总条数
     * @return
     */
    int getSysLogSum();
    /**
     * 清空系统日志
     * @return
     */
    int deleteLog(int maxCount);
    /**
     * 查询分页日志总数
     * @param level       日志级别
     * @param beginTime   查询开始时间，格式:yyyy-MM-dd
     * @param endTime     查询结束时间，格式:yyyy-MM-dd
     * @return
     */
    int getLogCountByPage(@Param("level") String level,@Param("beginTime") String beginTime,
                          @Param("endTime") String endTime);
}
