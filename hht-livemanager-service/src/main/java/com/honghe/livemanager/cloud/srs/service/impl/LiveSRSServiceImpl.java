package com.honghe.livemanager.cloud.srs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.cloud.srs.dao.LiveSRSDao;
import com.honghe.livemanager.cloud.srs.model.ClientModel;
import com.honghe.livemanager.cloud.srs.service.LiveSRSService;
import com.honghe.livemanager.cloud.srs.util.SRSLiveUtil;
import com.honghe.livemanager.common.util.DateUtil;
import com.honghe.livemanager.common.util.HttpUtils;
import com.honghe.livemanager.common.util.TipsMessage;
import com.honghe.livemanager.entity.LiveSysLog;
import com.honghe.livemanager.service.LiveSysLogService;
import com.honghe.livemanager.service.impl.LiveServiceImpl;
import com.honghe.livemanager.util.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * Description:处理SRS回调的Service
 * Author：hyh
 * Date: 2018/10/17
 */
@Service
public class LiveSRSServiceImpl implements LiveSRSService {
    private Logger mLogger=Logger.getLogger(LiveSRSServiceImpl.class);
    @Autowired
    LiveSRSDao srsDao;
    @Autowired
    private LiveSysLogService liveSysLogDao;
    @Autowired
    private LiveServiceImpl liveService;

    /**
     * 处理SRS推流关流的回调
     * @param json 回调数据
     * @return {"code": 0} 0为正常执行，非0 SRS会禁止该客户端推流
     */
    @Override
    public JSONObject srsCallback(String json) {
        JSONObject result = new JSONObject();
        int code = 0;
        JSONObject jsonObject = JSONObject.parseObject(json);
        //回调类型，"on_connect","on_publish","on_unpublish","on_close"
        String action = jsonObject.getString("action");
        //推流客户端ip
        String ip = jsonObject.getString("ip");
        //是推流回调，并且不是转码出的流
        if (TipsMessage.SRS_ACTION_PUBLISH.equals(action) && !TipsMessage.SRS_DEFAULT_IP.equals(ip)){
            String streamCode = jsonObject.getString("stream");
            // 判断直播码是否可用
            boolean isAvailable = isStreamAvailable(streamCode);
            if (isAvailable){
                // 更新直播管理数据库中的直播状态
                liveService.updateLiveByStreamCode(Constants.LIVE_PERMIT_PUSH,streamCode,ip,DateUtil.currentDatetime(), 0);
                //直播码可用：根据clientId获取client信息，存储streamCode以及相关id
                code = 0;
                int clientId = jsonObject.getInteger("client_id");
                ClientModel clientInfo = getClientInfo(clientId);
                try {
                    if (clientInfo != null) {
                        srsDao.insertStream(streamCode, clientId, clientInfo.getStream(), clientInfo.getVhost());
                    } else {
                        srsDao.insertStream(streamCode, clientId, 0, 0);
                        mLogger.error("clientInfo is null:" + json);
                    }
                }catch (Exception e){
                    mLogger.error("insert into srs异常:" + json);
                }
                //保存日志
                saveSysLog(LiveSysLog.Level.INFO.value(),"开启直播,推流直播码: "+streamCode);
            }else {
                //直播码不可用：返回非0值，禁止推流
                code = 1;
                mLogger.error("stream is not available:" + json);
                //保存日志
                saveSysLog(LiveSysLog.Level.INFO.value(),"开启直播,直播码不可用: "+streamCode);
            }
        }else if (TipsMessage.SRS_ACTION_UNPUBLISH.equals(action) && !TipsMessage.SRS_DEFAULT_IP.equals(ip)){
            code = 0;
            //断流的回调：更新直播管理数据库中的直播状态
            String streamCode = jsonObject.getString("stream");
            liveService.updateLiveByStreamCode(String.valueOf(Constants.LIVE_OVER_STATUS),streamCode,ip,DateUtil.currentDatetime(), 0);
            saveSysLog(LiveSysLog.Level.INFO.value(),"关闭直播,直播码: "+streamCode);
        }
        result.put(TipsMessage.CODE_KEY,code);
        return result;
    }

    /**
     * 保存系统日志
     * @param level           日志级别
     * @param description     日志描述
     */
    private void saveSysLog(String level,String description){
        //保存日志
        LiveSysLog liveSysLog=new LiveSysLog();
        liveSysLog.setSource(Constants.SRS_CLOUD_SOURCE);
        liveSysLog.setCreateTime(new Date());
        liveSysLog.setLevel(level);
        liveSysLog.setDescription(description);
        liveSysLogDao.addSysLog(liveSysLog);
    }
    /**
     * 判断该直播码在当前时间是否可用
     * @param streamCode 直播码
     * @return 是否可用
     */
    private boolean isStreamAvailable(String streamCode) {
        Integer liveId = srsDao.getLiveIdByTime(streamCode, DateUtil.currentDatetime());
        return liveId != null && liveId > 0;
    }

    /**
     * 根据clientId获取client信息
     * @param clientId 客户端id
     * @return SRS生成的client信息
     */
    private ClientModel getClientInfo(int clientId) {
        JSONObject srsJson = HttpUtils.httpGet("http://" + SRSLiveUtil.SRS_SERVER_IP + ":" + SRSLiveUtil.SRS_SERVER_API_PORT + "/api/v1/" + TipsMessage.SRS_API_CLIENTS + clientId);
        Integer srsCode = srsJson.getInteger(TipsMessage.CODE_KEY);
        if (srsCode != null && srsCode == 0){
            return JSON.parseObject(srsJson.getJSONObject("client").toJSONString(), ClientModel.class);
        }else {
            mLogger.error("can not find this client: " + clientId);
        }
        return null;
    }
}
