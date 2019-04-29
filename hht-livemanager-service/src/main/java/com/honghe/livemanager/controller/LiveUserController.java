package com.honghe.livemanager.controller;

import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.common.util.ParamUtil;
import com.honghe.livemanager.entity.LiveOperationLog;
import com.honghe.livemanager.entity.LiveSysLog;
import com.honghe.livemanager.entity.LiveUser;
import com.honghe.livemanager.service.LiveOperationLogService;
import com.honghe.livemanager.service.LiveSysLogService;
import com.honghe.livemanager.service.LiveUserService;
import com.honghe.livemanager.util.Constants;
import com.honghe.livemanager.util.ConvertResult;
import com.honghe.livemanager.util.MyLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@CrossOrigin
@RestController("liveUserController")
@RequestMapping("user")
public class LiveUserController {
    @Autowired
    private LiveUserService liveUserService;
    @Autowired
    private LiveOperationLogService liveOperationLogService;
    @Autowired
    private LiveSysLogService liveSysLogService;

    private static final String USER_MODEL="用户管理";
    private static final String USER_LOGIN="用户登录";

    /**
     * 验证用户登录
     * @return
     */
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public Result checkUserLogin(@RequestBody LiveUser liveUser, HttpServletRequest request){
        if(liveUser==null){
            return ConvertResult.getParamErrorResult();
        }
        LiveUser user=liveUserService.checkUserByName(liveUser.getUserName(),liveUser.getUserPwd());
        Result result=new Result(Result.Code.Success.value());
        if(user==null){
            result.setMsg(Constants.USER_LOGIN_FAILED);
            user=new LiveUser();
        }
        result.setResult(user);
        //保存日志
        saveLoginLog(liveUser.getUserName(),request);
        return result;
    }

    /**
     * 修改登录密码
     * @return
     */
    @MyLog(value = "用户管理-修改用户密码")
    @RequestMapping(value = "updateLoginPwd",method = RequestMethod.POST)
    public Result updateLoginPwd(@RequestBody Map map ){
        if(map==null || map.isEmpty() || (!map.containsKey("userId") || !map.containsKey("loginPwd") ||
                !map.containsKey("loginOldPwd"))){
            return ConvertResult.getParamErrorResult();
        }
        String userId=String.valueOf(map.get("userId"));
        String loginPwd=map.get("loginPwd").toString();
        String loginOldPwd=map.get("loginOldPwd").toString();
        int re_value=liveUserService.updateLoginPwd(userId,loginPwd,loginOldPwd);
        Result result=new Result(Result.Code.Success.value());
        if(re_value>0){
            result.setResult(true);
        }else if(re_value==-1){
            result.setMsg(Constants.USER_OLDPWD_FALSE);
            result.setResult(false);
        }else{
            result.setMsg(Constants.USER_UPDATE_PWD_FAILED);
            result.setResult(false);
        }
        return result;
    }

    /**
     * 根据用户id查询用户信息
     * @param userId  用户id
     * @return
     */
    @RequestMapping(value = "getUserById",method = RequestMethod.GET)
    public Result getUserById(String userId){
        if(ParamUtil.isEmpty(userId)){
            return ConvertResult.getParamErrorResult();
        }
        LiveUser user=liveUserService.getUserById(userId);
        Result result=new Result(Result.Code.Success.value());
        if(user==null){
            result.setMsg(Constants.USER_SEARCH_FAILED);
            user=new LiveUser();
            saveSearhUserLog(result.getMsg());
        }
        result.setResult(user);
        return result;
    }

    /**
     * 保存用户登录日志
     * @param userName   用户名
     * @param request
     */
    private void saveLoginLog(String userName,HttpServletRequest request){
        LiveOperationLog operationLog=new LiveOperationLog();
        operationLog.setModule(USER_MODEL);
        operationLog.setUserIp(request.getRemoteAddr());
        operationLog.setDescription(USER_LOGIN);
        operationLog.setUserName(userName);
        operationLog.setCreateTime(new Date());
        liveOperationLogService.addOperationLog(operationLog);
    }

    /**
     * 保存系统日志
     * @param description  日志描述
     */
    private void saveSearhUserLog(String description){
        LiveSysLog sysLog=new LiveSysLog();
        sysLog.setDescription(description);
        sysLog.setSource(USER_MODEL);
        sysLog.setLevel(LiveSysLog.Level.INFO.value());
        sysLog.setCreateTime(new Date());
        liveSysLogService.addSysLog(sysLog);
    }
}
