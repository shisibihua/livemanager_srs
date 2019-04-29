package com.honghe.livemanager.serviceManager;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 服务注解
 *
 * @auther yuk
 * @Time 2018/2/9 9:35
 */
@Target(ElementType.TYPE) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Component
public @interface SQLFileAnnotation {
    String SQLFileName();
}
