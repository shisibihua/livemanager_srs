package com.honghe.livemanager.service;

import com.honghe.livemanager.entity.LiveOperationLog;

import java.util.List;

public interface LiveOperationLogService {
    /**
     * 保存操作日志
     * @param log
     * @return
     */
    int addOperationLog(LiveOperationLog log);

    /**
     * 分页查询日志
     * @param beginTime   查询开始时间，格式:yyyy-MM-dd HH:mm:ss
     * @param endTime     查询结束时间，格式:yyyy-MM-dd HH:mm:ss
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return
     */
    List<LiveOperationLog> getLogByPage(String beginTime, String endTime,int currentPage,int pageSize,boolean pageFlag);

    /**
     * 清空操作日志
     * @return
     */
    int deleteOperationLog(int maxCount);

    /**
     * 获取日志分页总条数
     * @param beginTime
     * @param endTime
     * @return
     */
    int getLogCountByPage(String beginTime, String endTime);
}
