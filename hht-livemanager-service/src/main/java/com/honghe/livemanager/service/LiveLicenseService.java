package com.honghe.livemanager.service;

import com.honghe.livemanager.common.pojo.model.Page;
import com.honghe.livemanager.entity.LiveLicense;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 接入配置
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:46
 */
public interface LiveLicenseService {

	/**
	 * 添加授权
	 * @param license
	 */
	public boolean add(LiveLicense license);
	/**
	 * 添加授权附件
	 * @param
	 */
	public String uploadLicenseATT(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;


	/**
	 * 删除授权
	 * @param id
	 * @return
	 */
	public boolean delete(int id);

	 /**
	  *  重名校验
	  * @param license
	 */
	public boolean checkName(LiveLicense license);

	/**
	 * 切换授权状态
	 * @param license
	 * @return
	 */
	public boolean toggleStatus(LiveLicense license);

	/**
	 * 验证授权是否在有效期内
	 * @param license
	 * @return
	 */
	boolean isValid(LiveLicense license);

	/**
	 * 查询授权id
	 * @param id
	 * @return
	 */
	public LiveLicense selectById(int id);

	/**
	 * 分页查询授权
	 * @param page 页数
	 * @param pageSize 数据数量
	 * @param key 查询条件
	 * @return
	 */
	public Page selectByPage(int page,int pageSize,String key,String beginTime,String endTime);

	/**
	 * 更新授权
	 * @param license
	 */
	public boolean update(LiveLicense license);

	/**
	 * 删除附件
	 * @param attId
	 */
	public boolean deleteLicenseATTById(int attId,String fileAutoName);

	LiveLicense getLiveLicenseById(int licenseId);

}