package com.honghe.livemanager.common.util;

import java.util.UUID;

/**
 * uuid工具类
 *
 * @auther yuk
 * @Time 2018/3/3 12:38
 */
public class UUIDUtil {
    /**
     * 获取32位UUID
     * @return
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        String temp = str.replaceAll("-","");
        return temp;
    }

}
