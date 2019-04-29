package com.honghe.livemanager.config;

import com.honghe.livemanager.serviceManager.SQLFileAnnotation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务配置
 *
 * @Author libing
 * @Date: 2018-04-28 12:47
 * @Mender:
 */
@Component
@SQLFileAnnotation(SQLFileName="live_manager.sql")
public class ServiceConfiguration {


    /**
     * 线程接池大小 初始
     */
    @Value("${honghe.connector.corePoolSize}")
    private int corePoolSize = 80;

    /**
     * 线程最大 默认100
     */
    @Value("${honghe.connector.maximumPoolSize}")
    private int maximumPoolSize = 100;


    /**
     * 线程保持时间  分钟
     */
    @Value("${honghe.connector.keepAliveTime}")
    private int keepAliveTime = 10;

    /**
     * 等待队列大小
     */
    @Value("${honghe.connector.workQueueSize}")
    private int workQueueSize = 200;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getWorkQueueSize() {
        return workQueueSize;
    }

    public void setWorkQueueSize(int workQueueSize) {
        this.workQueueSize = workQueueSize;
    }
}
