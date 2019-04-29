package com.honghe.livemanager.entity;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:46
 */
public class LiveLicense {

	private String beginTime;
	private String contact;
	private String contactNumber;
	private Date createTime;
	private String endTime;
	private String licenseCode;
	private Integer id;
	private String name;
	private Integer status;
	private Integer cityId;
	private Integer countyId;
	private Integer provinceId;
	private String address;
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	private List<LiveLicenseATT> liveLicenseATTs;



	public List<LiveLicenseATT> getLiveLicenseATTs() {
		return liveLicenseATTs;
	}

	public void setLiveLicenseATTs(List<LiveLicenseATT> liveLicenseATTs) {
		this.liveLicenseATTs = liveLicenseATTs;
	}


	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getEndTime() {
		if(endTime!=null && endTime.endsWith(".0")){
			endTime=endTime.substring(0,endTime.lastIndexOf("."));
		}
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getBeginTime() {
		if(beginTime!=null && beginTime.endsWith(".0")){
			beginTime=beginTime.substring(0,beginTime.lastIndexOf("."));
		}
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getLicenseCode() {
		return licenseCode;
	}

	public void setLicenseCode(String licenseCode) {
		this.licenseCode = licenseCode;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getCountyId() {
		return countyId;
	}

	public void setCountyId(Integer countyId) {
		this.countyId = countyId;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
