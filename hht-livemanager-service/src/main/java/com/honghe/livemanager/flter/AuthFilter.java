package com.honghe.livemanager.flter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.common.util.*;
import com.honghe.livemanager.dao.LiveConfigDao;
import com.honghe.livemanager.dao.LiveLicenseDao;
import com.honghe.livemanager.dao.LiveUserDao;
import com.honghe.livemanager.entity.*;
import com.honghe.livemanager.service.LiveSysLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Constants;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;

/**
 * @Auther: libing
 * @Date: 2018/8/23 16:11
 * @Description: 用户权限校验
 */

@Component
public class AuthFilter implements Filter {
    static Logger logger = LoggerFactory.getLogger("AuthFilter");
    private static final String OPTIONS = "OPTIONS";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Autowired
    LiveLicenseDao licenseDao;

    @Autowired
    private LiveSysLogService liveSysLogService;

    @Autowired
    private LiveUserDao liveUserDao;

    @Autowired
    private LiveConfigDao liveConfigDao;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        Result result = new Result();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String userName=request.getHeader("userName")==null?"":request.getHeader("userName");
        String urlPath=request.getServletPath();
        //保存系统日志
        LiveSysLog liveSysLog=new LiveSysLog();
        liveSysLog.setLevel(LiveSysLog.Level.INFO.value());
        liveSysLog.setCreateTime(new Date());
        liveSysLog.setSource(com.honghe.livemanager.util.Constants.LIVE_PLATFORM_SERVICE);
        if(request.getMethod().equals(OPTIONS)){
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "OPTIONS,GET,POST,DELETE,PUT");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me, " +
                    "userName,userId,TPD-SecretID,TPD-CallBack-Auth");
            filterChain.doFilter(request,response);
        }else {
            //登录时header可能没有userName
            if (!ParamUtil.isEmpty(userName) || urlPath.contains("user/login") || urlPath.contains("live/getCallBack") ||
                    urlPath.contains("live/getSuperviseCallBack") || urlPath.contains("live/downLoadLiveFile") ||
                    urlPath.contains("statistic/downLoadStatisticFile") || urlPath.contains("licenseATT/upload") ||
                    urlPath.contains("licenseATT/download") || urlPath.contains("health")) {
                //监黄回调验证、登录及腾讯云回调
                if (urlPath.contains("live/getSuperviseCallBack") || urlPath.contains("user/login") ||
                        urlPath.contains("live/getCallBack") || urlPath.contains("live/downLoadLiveFile") ||
                        urlPath.contains("statistic/downLoadStatisticFile") || urlPath.contains("licenseATT/upload") ||
                        urlPath.contains("licenseATT/download") || urlPath.contains("health")) {
                    filterChain.doFilter(request, response);
                } else {
                    checkUserIsValid(userName,liveSysLog,result,filterChain,request,response);
                }
            }
            else if (urlPath.contains("liveService/addLive") || urlPath.contains("liveService/deleteLiveByLiveId") ||
                    urlPath.contains("liveService/getLiveDescribe") || urlPath.contains("liveService/getLiveDescribeHistory") ||
                    urlPath.contains("liveService/getLiveStatus") || urlPath.contains("srs/srsCallback")) {
                //对外接口直接放过，在controller层判断合法性
                filterChain.doFilter(request, response);
            }
            else {
                //header无用户名
                result.setCode(TipsMessage.FAILED_CODE);
                result.setMsg(TipsMessage.HEDER_NO_USER);
                send(response, result);
            }
        }
    }

    @Override
    public void destroy() {

    }

    private void send(HttpServletResponse response, Object result) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(result));
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * 验证用户是否合法
     * @param userName
     * @param liveSysLog
     * @param result
     * @param filterChain
     * @param request
     * @param response
     */
    private void checkUserIsValid(String userName,LiveSysLog liveSysLog,Result result,FilterChain filterChain,
                                              HttpServletRequest request,HttpServletResponse response){
        try {
            LiveUser user = liveUserDao.getUserByName(userName);
            if (user == null) {
                //用户不存在
                result.setCode(TipsMessage.FAILED_RETURN);
                result.setMsg(TipsMessage.USER_NOT_EXIST);
                liveSysLog.setDescription(TipsMessage.USER_NOT_EXIST);
                send(response, result);
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            ;
        }
    }
}
