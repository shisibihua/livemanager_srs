package com.honghe.livemanager.dao;



import com.honghe.livemanager.entity.LiveConfig;

import java.util.List;
import java.util.Map;

/**
 * 配置
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:45
 */
public interface LiveConfigDao {

	/**
	 * 添加接入配置
	 * @param license
	 */
	public int add(LiveConfig license);

	/**
	 * 删除接入配置
	 * @param id
	 * @return
	 */
	public int deleteById(int id);

	/**
	 * 查询接入配置id
	 * @param id
	 * @return
	 */
	public LiveConfig selectById(int id);


	/**
	 * 查询接入配置
	 */
	public List<LiveConfig> selectAll( );


	/**
	 * 更新接入配置
	 * @param license
	 */
	public int update(LiveConfig license);

}