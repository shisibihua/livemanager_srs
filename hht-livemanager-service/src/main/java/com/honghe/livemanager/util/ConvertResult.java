package com.honghe.livemanager.util;

import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.common.util.ParamUtil;
import com.honghe.livemanager.common.util.TipsMessage;
import com.honghe.livemanager.entity.LiveSysLog;
import com.honghe.livemanager.service.LiveSysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 整理接口结果，返回Result
 * @author caoqian
 * @date 20180821
 */
@Service
public class ConvertResult {
    @Autowired
    private LiveSysLogService liveSysLogService;
    private static final String LOG_SOURCE = "接口调用";
    private static LiveSysLogService sysLogService;
    @PostConstruct
    public void init(){
        sysLogService=liveSysLogService;
    }

    /**
     * 返回成功结果
     * @return
     */
    public static Result getSuccessResult(){
        return getSuccessResult(null);
    }
    /**
     * 返回成功结果
     * @param result_obj   返回的结果
     * @return
     */
    public static Result getSuccessResult(Object result_obj){
        Result result=getResult(result_obj);
        result.setCode(Result.Code.Success.value());
        return result;
    }
    /**
     * 返回参数错误结果
     * @return
     */
    public static Result getParamErrorResult(){
        return getParamErrorResult(null);
    }
    /**
     * 返回参数错误结果
     * @param result_obj   返回的结果
     * @return
     */
    public static Result getParamErrorResult(Object result_obj){
        Result result=getResult(result_obj);
        result.setCode(Result.Code.ParamError.value());
        if(ParamUtil.isEmpty(result.getMsg())){
            result.setMsg(TipsMessage.PARAM_ERROR);
        }
        LiveSysLog liveSysLog = new LiveSysLog(LiveSysLog.Level.ERROR.value(), result.getMsg() , LOG_SOURCE);
        sysLogService.addSysLog(liveSysLog);
        return result;
    }
    /**
     * 返回内部错误结果
     * @return
     */
    public static Result getErrorResult(){
        return getErrorResult(null);
    }
    /**
     * 返回内部错误结果
     * @param result_obj   返回的结果
     * @return
     */
    public static Result getErrorResult(Object result_obj){
        Result result=getResult(result_obj);
        result.setCode(Result.Code.UnKnowError.value());
        if(ParamUtil.isEmpty(result.getMsg())){
            result.setMsg(TipsMessage.INNER_ERROR_MSG);
        }
        LiveSysLog liveSysLog = new LiveSysLog(LiveSysLog.Level.ERROR.value(), result.getMsg() , LOG_SOURCE);
        sysLogService.addSysLog(liveSysLog);
        return result;
    }
    private static Result getResult(Object result_obj){
        Result result=new Result();
        if (result_obj != null) {
            if(result_obj instanceof Result) {
                result = (Result) result_obj;
            }else{
                result.setResult(result_obj);
            }
        }else{
            result.setResult(result_obj);
        }
        if(result.getMsg()==null){
            result.setMsg("");
        }
        return result;
    }
}
