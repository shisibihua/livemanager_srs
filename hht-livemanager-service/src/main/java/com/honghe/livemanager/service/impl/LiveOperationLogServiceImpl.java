package com.honghe.livemanager.service.impl;

import com.honghe.livemanager.dao.LiveOperationLogDao;
import com.honghe.livemanager.entity.LiveOperationLog;
import com.honghe.livemanager.service.LiveOperationLogService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiveOperationLogServiceImpl implements LiveOperationLogService {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(LiveOperationLogServiceImpl.class);
    @Autowired
    private LiveOperationLogDao liveOperationLogDao;


    @Override
    public int addOperationLog(LiveOperationLog log) {
        try{
            if(log!=null){
                return liveOperationLogDao.addOperationLog(log);
            }
        }catch (Exception e){
            logger.error("增加操作日志异常。",e);
        }
        return 0;
    }

    @Override
    public List<LiveOperationLog> getLogByPage(String beginTime, String endTime,
                                               int currentPage,int pageSize,boolean pageFlag) {
        List<LiveOperationLog> logList=new ArrayList<>();
        try{
            int start=(currentPage-1) * pageSize;
            logList=liveOperationLogDao.getLogByPage(beginTime,endTime,start,pageSize,pageFlag);
        }catch (Exception e){
            logger.error("分页查询操作日志信息异常，beginTime="+beginTime+",endTime="+endTime,e);
        }
        return logList;
    }

    @Override
    public int deleteOperationLog(int maxCount) {
        try{
            return liveOperationLogDao.deleteLog(maxCount);
        }catch (Exception e){
            logger.error("清空操作日志异常",e);
        }
        return 0;
    }

    @Override
    public int getLogCountByPage(String beginTime, String endTime) {
        return liveOperationLogDao.getLogCountByPage(beginTime,endTime);
    }
}
