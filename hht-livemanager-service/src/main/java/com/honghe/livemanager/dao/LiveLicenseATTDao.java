package com.honghe.livemanager.dao;


import com.honghe.livemanager.entity.LiveLicenseATT;

import java.util.List;

/**
 * 授权附件
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:46
 */
public interface LiveLicenseATTDao {

	/**
	 * 按照id删除
	 * @param id
	 */
	public int deleteById(Integer id);

	/**
	 * 批量插入添加附件
	 * @param list
	 */
	public int addBatch(List<LiveLicenseATT> list);

	/**
	 * 插入添加附件
	 * @param record
	 */
	public int add(LiveLicenseATT record);




	/**
	 *	授权id查询附件
	 * @param licenseId
	 */
	public List<LiveLicenseATT> selectByLicenseId(int licenseId);

	/**
	 *	按授权id删除附件
	 * @param licenseId
	 */
	public int deleteByLicenseId(int licenseId);


}