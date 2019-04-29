package com.honghe.livemanager.thread;

import com.honghe.livemanager.common.util.SpringUtil;
import com.honghe.livemanager.config.ServiceConfiguration;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 通知线程池
 *
 * @Author libing
 * @Date: 2018-05-02 8:40
 * @Mender:
 */
public class ThreadPool {


    private ServiceConfiguration serviceConfiguration = (ServiceConfiguration) SpringUtil.getBean("serviceConfiguration");

    private static ThreadPool uniqueInstance = new ThreadPool();

    public ThreadPoolExecutor threadPoolExecutor;


    //#TimeUnit.DAYS;               //天
    //#TimeUnit.HOURS;             //小时
    //#TimeUnit.MINUTES;           //分钟
    //#TimeUnit.SECONDS;           //秒
    //#TimeUnit.MILLISECONDS;      //毫秒
    //#TimeUnit.MICROSECONDS;      //微妙
    //#TimeUnit.NANOSECONDS;       //纳秒
    //honghe.connector.timeUnit = TimeUnit.MINUTES

    //ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。
    //ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。
    //ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
    //ThreadPoolExecutor.CallerRunsPolicy：由调用线程处理该任务
    private ThreadPool(){
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        threadPoolExecutor = new ThreadPoolExecutor(serviceConfiguration.getCorePoolSize(), serviceConfiguration.getMaximumPoolSize(), serviceConfiguration.getKeepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(serviceConfiguration.getWorkQueueSize()),new ThreadPoolExecutor.DiscardOldestPolicy());
//        threadPoolExecutor = new ThreadPoolExecutor(50, 100,100 , TimeUnit.SECONDS,
//                new LinkedBlockingDeque<>(500));


    }

    public static ThreadPool getInstance(){

        return uniqueInstance;
    }


}