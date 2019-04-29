package com.honghe.livemanager.dao;


import com.honghe.livemanager.entity.LiveDistrict;

import java.util.List;

/**
 * 行政区
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:45
 */
public interface LiveDistrictDao {



	/**
	 *  根据id查询行政区
	 * @param liveDistrictId
	 */
	public LiveDistrict selectById(Integer liveDistrictId);
	/**
	 * 根据上级id查询行政区
	 * @param parentId
	 */
	public List<LiveDistrict> selectByParentId(Integer parentId);


}