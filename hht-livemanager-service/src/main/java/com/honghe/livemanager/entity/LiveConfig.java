package com.honghe.livemanager.entity;


import java.util.Date;

/**
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:45
 */
public class LiveConfig {

	private String apiAuthenticationKey;
	private String appid;
	private String bizid;
	private Date createTime;
	private Integer id;
	private String name;
	private String pushSecretKey;
	private String secretKey;
	private String secretId;

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getSecretId() {
		return secretId;
	}

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

	public String getApiAuthenticationKey() {
		return apiAuthenticationKey;
	}

	public void setApiAuthenticationKey(String apiAuthenticationKey) {
		this.apiAuthenticationKey = apiAuthenticationKey;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getBizid() {
		return bizid;
	}

	public void setBizid(String bizid) {
		this.bizid = bizid;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPushSecretKey() {
		return pushSecretKey;
	}

	public void setPushSecretKey(String pushSecretKey) {
		this.pushSecretKey = pushSecretKey;
	}
}