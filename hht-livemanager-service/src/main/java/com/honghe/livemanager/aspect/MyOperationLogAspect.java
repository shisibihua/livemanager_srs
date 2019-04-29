package com.honghe.livemanager.aspect;
import com.honghe.livemanager.common.util.DateUtil;
import com.honghe.livemanager.entity.LiveOperationLog;
import com.honghe.livemanager.service.LiveOperationLogService;
import com.honghe.livemanager.util.MyLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;

/**
 * 记录平台的操作日志
 * @author caoqian
 * @date 2018-09-12
 */
@Aspect
@Component
public class MyOperationLogAspect {

    @Autowired
    private LiveOperationLogService liveOperationLogService;
    private static final String HEADER_NAME="userName";
    @Pointcut("@annotation(com.honghe.livemanager.util.MyLog)")
    public void log(){}

    //配置后置返回通知,使用在方法aspect()上注册的切入点
    @AfterReturning("log()")
    public void afterReturn(JoinPoint joinPoint){
//        System.out.println("切面。。。。。。");
        LiveOperationLog operationLog=new LiveOperationLog();
        Date now=new Date();
        try {
            operationLog.setCreateTime(DateUtil.parseDatetime(DateUtil.formatDatetime(now)));
        } catch (ParseException e) {
            operationLog.setCreateTime(null);
        }
        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        //获取操作
        MyLog myLog = method.getAnnotation(MyLog.class);
        if (myLog != null) {
            String[] value = myLog.value().split("-");
            //保存获取的操作
            operationLog.setModule(value[0]);
            operationLog.setDescription(value[1]);
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        /*
            System.out.println("URL : " + request.getRequestURL().toString());
            System.out.println("HTTP_METHOD : " + request.getMethod());
            System.out.println("IP : " + request.getRemoteAddr());
            System.out.println("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            System.out.println("ARGS : " + Arrays.toString(joinPoint.getArgs()));
        */
        //获取用户名,用户名放到了header中
        String loginName=request.getHeader(HEADER_NAME);
        operationLog.setUserName(loginName);
        //获取用户ip地址
        operationLog.setUserIp(request.getRemoteAddr());
        liveOperationLogService.addOperationLog(operationLog);
    }
}
