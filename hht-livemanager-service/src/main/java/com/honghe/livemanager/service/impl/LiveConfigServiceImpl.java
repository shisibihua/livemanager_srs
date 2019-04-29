package com.honghe.livemanager.service.impl;

import com.honghe.livemanager.dao.LiveConfigDao;
import com.honghe.livemanager.dao.LiveMaxLimitDao;
import com.honghe.livemanager.entity.LiveConfig;
import com.honghe.livemanager.entity.LiveMaxLimit;
import com.honghe.livemanager.service.LiveConfigService;
import com.honghe.livemanager.util.TencentParamsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:45
 */
@Service("configService")
public class LiveConfigServiceImpl implements LiveConfigService {

	@Autowired
	LiveConfigDao configDao;
	@Autowired
	LiveMaxLimitDao maxLimitDao;

	@Override
	public boolean add(LiveConfig liveConfig) {
		return configDao.add(liveConfig)>0;
	}

	@Override
	public boolean delete(int id) {
		return configDao.deleteById(id)>0;
	}

	@Override
	public boolean update(LiveConfig liveConfig) {
		if(configDao.update(liveConfig)>0){
			new TencentParamsUtil().updateParams(liveConfig);
			return true;
		}
		return false;
	}

	@Override
	public List<LiveConfig> selectAll() {
		return configDao.selectAll();
	}

	@Override
	public LiveConfig selectById(int id) {
		return configDao.selectById(id);
	}

	@Override
	public int getMaxCount() {
		return maxLimitDao.getMaxCount();
	}

	@Override
	public boolean updateMaxCount(LiveMaxLimit liveMaxLimit) {
		return maxLimitDao.updateMaxCount(liveMaxLimit)>0;
	}
}