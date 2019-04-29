package com.honghe.livemanager.dao;


import com.honghe.livemanager.entity.LiveMaxLimit;

/**
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:46
 */
public interface LiveMaxLimitDao {

	/**
	 * 查询同一时间段直播数量
	 * @return
	 */
	public int getMaxCount();

	/**
	 * 更新最大直播数量限制
	 * @param record
	 */
	public int updateMaxCount(LiveMaxLimit record);

}