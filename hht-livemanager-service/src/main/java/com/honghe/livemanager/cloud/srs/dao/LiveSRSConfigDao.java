package com.honghe.livemanager.cloud.srs.dao;

import com.honghe.livemanager.cloud.srs.model.SRSConfigModel;

/**
 * 读取srs服务配置
 * @author caoqian
 * @date 20181019
 */
public interface LiveSRSConfigDao {
    /**
     * 根据id查询SRS配置信息
     * @param srsId
     * @return
     */
    SRSConfigModel getSrsConfig(int srsId);

    /**
     * 修改SRS配置信息
     * @param srsConfigModel
     * @return
     */
    int updateSrsConfig(SRSConfigModel srsConfigModel);
}
