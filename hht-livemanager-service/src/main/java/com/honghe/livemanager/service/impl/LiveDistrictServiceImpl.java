package com.honghe.livemanager.service.impl;


import com.honghe.livemanager.dao.LiveDistrictDao;
import com.honghe.livemanager.entity.LiveDistrict;
import com.honghe.livemanager.service.LiveDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:45
 */
@Service("districtService")
public class LiveDistrictServiceImpl implements LiveDistrictService {


	@Autowired
	LiveDistrictDao districtDao;
	/**
	 * 按上级Id查询信息
	 *
	 * @param parentId
	 */
	public List<LiveDistrict> selectByParentId(int parentId){
		return districtDao.selectByParentId(parentId);
	}
	/**
	 * 按id查询信息
	 *
	 * @param id
	 */
	public LiveDistrict selectById(int id){
		return districtDao.selectById(id);
	}

}