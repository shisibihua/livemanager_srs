package com.honghe.livemanager.service.impl;

import com.honghe.livemanager.dao.LiveSysLogDao;
import com.honghe.livemanager.entity.LiveSysLog;
import com.honghe.livemanager.service.LiveSysLogService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiveSysLogServiceImpl implements LiveSysLogService {
    private org.slf4j.Logger logger= LoggerFactory.getLogger(LiveSysLogServiceImpl.class);
    @Autowired
    private LiveSysLogDao liveSysLogDao;

    @Override
    public int addSysLog(LiveSysLog log) {
        try{
            if(log!=null){
                return liveSysLogDao.addSysLog(log);
            }
        }catch (Exception e){
            logger.error("增加系统日志异常。",e);
        }
        return 0;
    }

    @Override
    public List<LiveSysLog> getLogByPage(String level,String beginTime, String endTime,
                                         int currentPage,int pageSize,boolean pageFlag) {
        List<LiveSysLog> logList=new ArrayList<>();
        try{
            int start = (currentPage-1) * pageSize;
            logList=liveSysLogDao.getLogByPage(convertLevelToStr(level),beginTime,endTime,start,pageSize,pageFlag);
        }catch (Exception e){
            logger.error("分页查询系统日志信息异常，level="+level+",beginTime="+beginTime+",endTime="+endTime,e);
        }
        return logList;
    }

    @Override
    public int deleteSysLog(int maxCount) {
        try{
            return liveSysLogDao.deleteLog(maxCount);
        }catch (Exception e){
            logger.error("清空系统日志异常",e);
        }
        return 0;
    }

    @Override
    public int getLogCountByPage(String level, String beginTime, String endTime) {
        return liveSysLogDao.getLogCountByPage(convertLevelToStr(level),beginTime,endTime);
    }

    /**
     * 转换日志级别为ERROR，INFO，DEBUG
     * @param level
     * @return
     */
    private String convertLevelToStr(String level){
        if(level!=null) {
            switch (level) {
                case "0":
                    level="ERROR";
                    break;
                case "1":
                    level="INFO";
                    break;
                case "2":
                    level="DEBUG";
                    break;
                default:
                    level="";
            }
        }
        return level;
    }
}
