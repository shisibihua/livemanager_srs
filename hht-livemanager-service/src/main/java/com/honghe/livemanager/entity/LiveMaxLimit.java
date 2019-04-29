package com.honghe.livemanager.entity;


import java.util.Date;

/**
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:46
 */
public class LiveMaxLimit {

	private Date createTime;
	private Integer id;
	private Integer maxCount;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}
}