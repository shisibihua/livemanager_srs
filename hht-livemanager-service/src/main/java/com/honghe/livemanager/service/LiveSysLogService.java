package com.honghe.livemanager.service;

import com.honghe.livemanager.entity.LiveSysLog;

import java.util.List;

public interface LiveSysLogService {
    /**
     * 保存系统日志
     * @param log
     * @return
     */
    int addSysLog(LiveSysLog log);

    /**
     * 分页查询日志
     * @param level       日志级别
     * @param beginTime   查询开始时间，格式:yyyy-MM-dd HH:mm:ss
     * @param endTime     查询结束时间，格式:yyyy-MM-dd HH:mm:ss
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return
     */
    List<LiveSysLog> getLogByPage(String level,String beginTime, String endTime,
                                  int currentPage,int pageSize,boolean pageFlag);


    /**
     * 清空日志
     * @return
     */
    int deleteSysLog(int maxCount);

    /**
     * 获取分页日志总条数
     * @return
     */
    int getLogCountByPage(String level, String beginTime, String endTime);
}
