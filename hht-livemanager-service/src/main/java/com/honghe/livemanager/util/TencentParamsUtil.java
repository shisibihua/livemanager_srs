package com.honghe.livemanager.util;

import com.honghe.livemanager.common.util.SpringUtil;
import com.honghe.livemanager.dao.LiveConfigDao;
import com.honghe.livemanager.entity.LiveConfig;

/**
 * 腾讯参数更新
 *
 * @Author libing
 * @Date: 2018-09-13 18:09
 * @Mender:
 */
public class TencentParamsUtil {

    private LiveConfigDao liveConfigDao = (LiveConfigDao)SpringUtil.getBean("liveConfigDao");
    public void init(){
        updateParams(liveConfigDao.selectById(1));

    }
    public void updateParams(LiveConfig liveConfig){
        TencetLiveUtil.CLOUD_TENCENT_KEY=liveConfig.getApiAuthenticationKey();
        TencetLiveUtil.CLOUD_TENCENT_PUSH_KEY=liveConfig.getPushSecretKey();
        TencetLiveUtil.CLOUD_TENCENT_BIZID=liveConfig.getBizid();
        TencetLiveUtil.CLOUD_TENCENT_APPID=liveConfig.getAppid();

    }
}
