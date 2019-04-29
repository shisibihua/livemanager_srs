package com.honghe.livemanager.cloud.srs.service;

import com.honghe.livemanager.cloud.srs.model.SRSConfigModel;

public interface LiveSRSConfigService {
    /**
     * 修改SRS服务配置
     * @param srsConfigModel
     * @return
     */
    boolean updateSRSConfig(SRSConfigModel srsConfigModel);
}
