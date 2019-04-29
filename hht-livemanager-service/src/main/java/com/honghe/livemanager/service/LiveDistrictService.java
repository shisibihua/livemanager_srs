package com.honghe.livemanager.service;


import com.honghe.livemanager.entity.LiveDistrict;

import java.util.List;

/** 行政区
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:45
 */
public interface LiveDistrictService {


	/**
	 * 通过上级id查询行政区
	 * @param parentId
	 * @return
	 */
	public List<LiveDistrict> selectByParentId(int parentId);

	/**
	 * 通过id查询行政区
	 * @param id
	 * @return
	 */
	public LiveDistrict selectById(int id);

}