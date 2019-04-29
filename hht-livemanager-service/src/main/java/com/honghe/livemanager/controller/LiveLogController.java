package com.honghe.livemanager.controller;

import com.honghe.livemanager.common.pojo.model.Page;
import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.entity.LiveOperationLog;
import com.honghe.livemanager.entity.LiveSysLog;
import com.honghe.livemanager.service.LiveOperationLogService;
import com.honghe.livemanager.service.LiveSysLogService;
import com.honghe.livemanager.util.ConvertResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController("liveLogController")
@RequestMapping("log")
public class LiveLogController {
    @Autowired
    private LiveOperationLogService liveOperationLogService;
    @Autowired
    private LiveSysLogService liveSysLogService;

    /**
     * 分页查询操作日志信息
     * @param beginTime    查询开始时间,格式：yyyy-MM-dd HH:mm:ss
     * @param endTime      查询结束时间,格式：yyyy-MM-dd HH:mm:ss
     * @param currentPage  当前页
     * @param pageSize     每页大小
     * @return
     */
    @RequestMapping(value = "getOperationLogByPage",method = RequestMethod.GET)
    public Result getOperationLogByPage(String beginTime,String endTime,int currentPage,int pageSize){
        if(0==currentPage || 0==pageSize){
            return ConvertResult.getParamErrorResult();
        }
        List<LiveOperationLog> logList=liveOperationLogService.getLogByPage(beginTime,endTime,currentPage,pageSize,true);
        if(logList==null || logList.isEmpty()){
            List<LiveOperationLog> emptyLogList=new ArrayList<>();
            LiveOperationLog log=new LiveOperationLog();
            emptyLogList.add(log);
            logList.addAll(emptyLogList);
        }
        int totalCount=liveOperationLogService.getLogCountByPage(beginTime,endTime);
        return ConvertResult.getSuccessResult(getLogPage(currentPage,pageSize,totalCount,logList));
    }

    /**
     * 分页查询操作日志信息
     * @param level        日志级别----0:ERROR,1:INFO,2:DEBUG,可为空，默认查询全部日志级别；
     * @param beginTime    查询开始时间,格式：yyyy-MM-dd HH:mm:ss
     * @param endTime      查询结束时间,格式：yyyy-MM-dd HH:mm:ss
     * @param currentPage  当前页
     * @param pageSize     每页大小
     * @return
     */
    @RequestMapping(value = "getSysLogByPage",method = RequestMethod.GET)
    public Result getSysLogByPage(String level,String beginTime,String endTime,int currentPage,int pageSize){
        if(0==currentPage || 0==pageSize){
            return ConvertResult.getParamErrorResult();
        }
        List<LiveSysLog> logList=liveSysLogService.getLogByPage(level,beginTime,endTime,currentPage,pageSize,true);
        if(logList==null || logList.isEmpty()){
            List<LiveSysLog> emptyLogList=new ArrayList<>();
            LiveSysLog log=new LiveSysLog();
            emptyLogList.add(log);
            logList.addAll(emptyLogList);
        }
        int totalCount=liveSysLogService.getLogCountByPage(level,beginTime,endTime);
        return ConvertResult.getSuccessResult(getLogPage(currentPage,pageSize,totalCount,logList));
    }

    /**
     * 整理日志查询结果
     * @param currentPage
     * @param pageSize
     * @param totalCount
     * @param logList
     * @return
     */
    private Page getLogPage(int currentPage, int pageSize, int totalCount,List logList){
        return new Page(logList,currentPage,pageSize,totalCount);
    }
}
