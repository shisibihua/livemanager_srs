package com.honghe.livemanager.util;

import java.lang.annotation.*;

/**
 * 自定义注解类，记录操作日志
 * @author caoqian
 * @date 2018-09-11
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented //生成文档
public @interface MyLog {
    String value() default "";
}

