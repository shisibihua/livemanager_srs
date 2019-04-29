package com.honghe.livemanager.entity;


import java.util.Date;

/**
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:46
 */
public class LiveLicenseATT {

	private Date createTime;
	private Integer id;
	private String path;
	private String name;
	private Integer licenseId;

	public Integer getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Integer licenseId) {
		this.licenseId = licenseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}