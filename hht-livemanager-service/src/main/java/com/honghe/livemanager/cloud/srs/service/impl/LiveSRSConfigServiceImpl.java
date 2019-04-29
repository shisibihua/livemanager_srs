package com.honghe.livemanager.cloud.srs.service.impl;

import com.honghe.livemanager.cloud.srs.dao.LiveSRSConfigDao;
import com.honghe.livemanager.cloud.srs.model.SRSConfigModel;
import com.honghe.livemanager.cloud.srs.service.LiveSRSConfigService;
import com.honghe.livemanager.cloud.srs.util.SRSParamsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LiveSRSConfigServiceImpl implements LiveSRSConfigService{
    @Autowired
    private LiveSRSConfigDao liveSRSConfigDao;
    @Override
    public boolean updateSRSConfig(SRSConfigModel srsConfigModel) {
        if(liveSRSConfigDao.updateSrsConfig(srsConfigModel)>0){
            //TODO:SRS读取数据库时打开下面注释代码
//            new SRSParamsUtil().updateParams(srsConfigModel);
            return true;
        }
        return false;
    }
}
