package com.honghe.livemanager.common.util;

import com.honghe.livemanager.serviceManager.SQLFileAnnotation;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @Description 获取bean
 * @Author sunchao
 * @Date: 2017-12-22 9:49
 * @Mender:
 */
@Component
public class SpringUtil {

    private static ApplicationContext applicationContext = null;
    static org.slf4j.Logger logger = LoggerFactory.getLogger(SpringUtil.class);

    public static  void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
       }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);

    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

    public static void scannerBeans(){

        //扫描SQL文件注解
        String[] sqlFiles = getApplicationContext().getBeanNamesForAnnotation(SQLFileAnnotation.class);
        List<String> sqlFileNameList = new ArrayList<>();
        for(String string:sqlFiles){
            //扫描SQL文件注解
            Object sqlFile = getBean(string);
            try {
                String sqlFileName = sqlFile.getClass().getAnnotation(SQLFileAnnotation.class).SQLFileName();
                sqlFileNameList.add(sqlFileName);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        executeSql(sqlFileNameList);
    }

    private static void executeSql(List<String> sqlFileNameList){

        Collections.sort(sqlFileNameList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(o1.contains("-")){
                    o1 = o1.substring(o1.indexOf("-") + 1,o1.length());
                }
                if(o2.contains("-")){
                    o2 = o2.substring(o2.indexOf("-") + 1,o2.length());
                }
                return o1.compareTo(o2);
            }
        });

        for(String name : sqlFileNameList){
            String importedDBs = getImportedDBs();
            if(importedDBs.contains(name)){
                logger.debug("数据库已导入，不再重复导入！！");
                continue;
            }
            boolean isSuccess = false;
            try {
                //读取sql文件
                String content = IOUtils.toString(new FileInputStream(System.getProperty("user.dir") + "/config/" + name),"utf-8");
                isSuccess = SQLExecuteUtils.geInstance().executeUserSQLFile(content,"hht_livemanager",";");//注意：sql脚本中以##（替换;）表示一句完整语句
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(isSuccess){
                updateImportedDB(importedDBs + "\r\n" + name);
            }
        }
    }
    private static String getImportedDBs(){
        StringBuilder importedDB=new StringBuilder();
        try {
            File file =new File(PathUtil.getPath(PathUtil.PathType.CONFIG) + "dbImport.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileInputStream in=new FileInputStream(file);
            byte[] b=new byte[1024];
            int i;
            while((i=in.read(b))>0){
                importedDB.append(new String(b,0,i));
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return importedDB.toString();
    }

    private static void updateImportedDB(String string){
        try {
            File file =new File(PathUtil.getPath(PathUtil.PathType.CONFIG) + "dbImport.txt");
            FileOutputStream out =new FileOutputStream(file);
            byte[] newb = string.getBytes();
            out.write(newb);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 获取请求的ip地址
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     *
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     *
     * 如：X-Forwarded-For：192.168.1.110

     , 192.168.1.120

     , 192.168.1.130

     ,
     * 192.168.1.100

     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}