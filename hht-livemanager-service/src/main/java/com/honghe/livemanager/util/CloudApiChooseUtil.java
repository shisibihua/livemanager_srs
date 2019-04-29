package com.honghe.livemanager.util;

import com.honghe.livemanager.common.util.ConfigUtil;
import com.honghe.livemanager.common.util.ParamUtil;

/**
 * api接口选择,默认选择腾讯云
 */
public class CloudApiChooseUtil {
    public static String API_CHOOSE="1";
    public void init(){
        String apiChoose= ConfigUtil.getInstance().getPropertyValue("choose_live_api");
        if(!ParamUtil.isEmpty(apiChoose)) {
            updateParams(apiChoose);
        }
    }
    public void updateParams(String apiChoose){
        CloudApiChooseUtil.API_CHOOSE=apiChoose;
    }
}
