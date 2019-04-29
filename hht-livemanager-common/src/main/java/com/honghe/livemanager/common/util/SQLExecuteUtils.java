package com.honghe.livemanager.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 执行sql文件工具
 *
 * @auther yuk
 * @Time 2017/9/16 14:08
 */
public class SQLExecuteUtils {
    /**
     * 连接信息
     */
    private static String url = "";
    private static String userName = "";
    private static String password = "";
    private static String[] params = new String[0];

    private SQLExecuteUtils() {
    }

    /**
     * 获取sql执行工具
     * 通过配置文件读取数据库url，用户名，密码信息
     *
     * @return
     */
    public static SQLExecuteUtils geInstance(String[] param) {
        loadProperties();
        params = param;
        return new SQLExecuteUtils();
    }

    private static void loadProperties() {
        Properties pro = new Properties();
        try {
            pro.load(new FileInputStream(PathUtil.getPath(PathUtil.PathType.CONFIG) + "application.properties"));
            //数据库连接地址
            url = pro.getProperty("spring.datasource.url");
            //数据库用户名
            userName = pro.getProperty("spring.datasource.username");
            //数据库密码
            password = pro.getProperty("spring.datasource.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取sql执行工具
     * 通过配置文件读取数据库url，用户名，密码信息
     *
     * @return
     */
    public static SQLExecuteUtils geInstance() {
        loadProperties();
        return new SQLExecuteUtils();
    }

    /**
     * 执行sql
     *
     * @param sqlContent
     * @param dbName
     * @param sqlDelimiter
     */
    public boolean executeUserSQLFile(String sqlContent, String dbName, String sqlDelimiter) {
        SQLExecutor sqlExecutor;

        String temp = "";
        if (params != null && params.length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : params) {
                stringBuilder.append(str + "&");
            }
            temp = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        }
        sqlExecutor = new SQLExecutor("com.mysql.jdbc.Driver", url.split("\\?")[0].replaceAll(dbName, "") + "?" + temp + "useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true", userName, password, sqlDelimiter);
        try {
            sqlExecutor.importFile(sqlContent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
