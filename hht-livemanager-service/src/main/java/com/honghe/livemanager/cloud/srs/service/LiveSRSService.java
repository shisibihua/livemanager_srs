package com.honghe.livemanager.cloud.srs.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Description:处理SRS回调的Service
 * Author：hyh
 * Date: 2018/10/17
 */
public interface LiveSRSService {

    JSONObject srsCallback(String json);
}
