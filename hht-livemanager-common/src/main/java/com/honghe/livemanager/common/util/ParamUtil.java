package com.honghe.livemanager.common.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuk on 2016/12/16.
 */
public class ParamUtil {

    private ParamUtil(){}
    /**
     * 某一个参数为空或不合法，返回true
     * @param objects
     * @return
     */

    public static boolean isOneEmpty(Object... objects){
        for(Object o : objects){
            if(o == null){
                return true;
            }
            if(o instanceof String){
                if("".equals(o)){
                    return true;
                }
            }else if(o instanceof Integer && Integer.parseInt(String.valueOf(o))<0){
                return true;
            }
        }
        return false;
    }

    /**
     * 所有参数都为空或不合法，返回true
     * @param objects
     * @return
     */
    public static boolean isAllEmpty(Object... objects){
        Boolean flag;
        int count = objects.length;
        for(Object o : objects){
            if(o == null){
                count = count -1;
            }
            if(o instanceof String){
                if("".equals(o)){
                    count = count -1;
                }
            }else if(o instanceof Integer && Integer.parseInt(String.valueOf(o))<=0){
                count = count -1;
            }
        }
        if(count == 0){
            flag = true;
        }else {
            flag = false;
        }
        return flag;

    }

    public static boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }

    /**
     * TODO
     * Convert String to long
     * @param value
     * @param def default value
     * @return
     */
    public static long toLong(String value, long def) {
        if (isEmpty(value)) {
            return def;
        }

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return def;
        }
    }

    /**
     * Convert String to int
     * @param value
     * @param def default value
     * @return
     */
    public static int toInt(String value, int def) {
        if (isEmpty(value)) {
            return def;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return def;
        }
    }


    public static Map calculatePage(int page,int pageSize){
        Map<String,Integer> map = new HashMap<>();
        if(page <=0){
            page = 1;
        }
        map.put("start",(page-1) * pageSize);
        map.put("size",pageSize);
        return map;
    }

    public static Integer changeObjectToInt(Object o){
        if(null==o||o.equals("")) return null;
        return  Integer.parseInt(o.toString());
    }
    public static String changeObjectToStr(Object o){
        if(null==o) return null;
        return  o.toString();
    }


    /**
     * 获取request参数
     *
     * @param request http请求信息
     * @return 参数map
     */
    public static  Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        Map<String, Object> params = new HashMap<>();
        String type = request.getHeader("Content-Type");
        if (request.getMethod().equalsIgnoreCase("GET") || !type.contains("application/json")) {
            parameters = request.getParameterMap();
            int len;
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                len = entry.getValue().length;
                if (len == 1) {
                    params.put(entry.getKey(), entry.getValue()[0]);
                } else if (len > 1) {
                    params.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            try {
                ServletInputStream inputStream = request.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line + "\n");
                }
                String strContent = content.toString();
                params = (Map<String, Object>) JSON.parse(strContent);
            } catch (IOException e) {
                Logger logger = LoggerFactory.getLogger(ParamUtil.class);
                logger.error("getParams" + TipsMessage.PARES_ERROR, e);
            }
        }
        return params;
    }

    /**
     * 获取软件代码信息
     * @param key
     * @return
     */
    private static int getSoftType(String key) {
        int keyPostion = 23;
        switch (key) {
            case "T":
                keyPostion = 23;
                break;
            case "S":
                keyPostion = 22;
                break;
            case "R":
                keyPostion = 21;
                break;
            case "Q":
                keyPostion = 20;
                break;
            case "P":
                keyPostion = 18;
                break;
            case "O":
                keyPostion = 17;
                break;
            case "N":
                keyPostion = 16;
                break;
            case "M":
                keyPostion = 15;
                break;
            case "L":
                keyPostion = 13;
                break;
            case "K":
                keyPostion = 12;
                break;
            case "J":
                keyPostion = 11;
                break;
        }
        return keyPostion;
    }
}
