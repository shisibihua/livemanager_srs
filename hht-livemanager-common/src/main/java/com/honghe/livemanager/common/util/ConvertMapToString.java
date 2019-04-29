package com.honghe.livemanager.common.util;

import java.util.Iterator;
import java.util.Map;

/**
 * 遍历map，获取key与value值
 * @author caoqian
 * @date 20180824
 */
public class ConvertMapToString {

    /**
     * 获取Map的key值与value值，并拼接成字符串
     * @param map        map集合
     * @return
     */
    public static String getKeyAndValue(Map<String,Object> map){
        return getKeyAndValue(map,null);
    }
    /**
     * 获取Map的key值与value值，并拼接成字符串
     * @param map        map集合
     * @param separator  拼接的分隔符
     * @return
     */
    public static String getKeyAndValue(Map<String,Object> map, String separator){
        String resultStr="";
        if(separator==null || "".equals(separator)){
            separator="&";
        }
        if(map!=null && !map.isEmpty()){
            Iterator it= map.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry entry=(Map.Entry)it.next();
                String key=entry.getKey().toString();
                String value=entry.getValue().toString();
                resultStr+=key+"="+value+separator;
            }
        }
        if(!"".equals(resultStr) && resultStr.endsWith(separator)) {
            resultStr = resultStr.substring(0, resultStr.lastIndexOf(separator));
        }
        return resultStr;
    }
}
