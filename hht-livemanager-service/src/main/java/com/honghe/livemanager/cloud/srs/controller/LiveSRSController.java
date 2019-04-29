package com.honghe.livemanager.cloud.srs.controller;

import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.cloud.srs.api.LocalSRSApi;
import com.honghe.livemanager.cloud.srs.service.LiveSRSService;
import com.honghe.livemanager.common.util.TipsMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description:用于响应SRS事件回调的接口
 * Author：hyh
 * Date: 2018/10/17
 */

@CrossOrigin
@RestController("liveSRSController")
@RequestMapping("srs")
public class LiveSRSController {
    private Logger mLogger=Logger.getLogger(LiveSRSController.class);
    @Autowired
    LiveSRSService liveSRSService;

    /**
     * SRS回调的处理流程
     * @param json 收到的SRS传来的json
     * @return {"code": 0} 0为正常执行，非0 SRS会禁止该客户端推流
     */
    @ResponseBody
    @RequestMapping("srsCallback")
    public JSONObject srsCallback(@RequestBody String json){
        mLogger.debug("srsCallback:" + json);
        if (json != null && !"".equals(json)){
            return liveSRSService.srsCallback(json);
        }
        JSONObject result = new JSONObject();
        result.put("code",1);
        return result;
    }

}
