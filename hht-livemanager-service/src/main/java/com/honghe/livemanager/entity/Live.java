package com.honghe.livemanager.entity;

import java.util.Date;

/**
 * 直播
 * @author caoqian
 * @date  20180822
 */
public class Live {
    private int liveId;
    //直播标题
    private String title;
    //直播类型
    private String type;
    //直播开始时间
    private String beginTime;
    //直播结束时间
    private String endTime;
    //实际直播开始时间
    private Date actualBeginTime;
    //实际直播结束时间
    private Date actualEndTime;
    //直播码
    private String streamCode;
    //直播详情（简介）
    private String details;
    //直播封面
    private String cover;
    // 创建时间
    private Date createTime;
    //直播状态，1：正在直播；2：等待直播；3：结束直播；4:禁用
    private int status;
    //删除状态  0:未删除;1:已删除
    private int isDel;
    //推流客户端ip
    private String pushClientIp;
    //推流帧率(腾讯对应videoFrameRate)
    private int videoFrameRate;
    private int audioFrameRate;
    //推流码率
    private int bitRate;
    //观看人数
    private int number;
    //带宽
    private float bandWidth;
    //流量
    private float trafficValue;
    //授权id
    private String licenseCode;
    //鸿合账号
    private String hitevisionAccount;
    //教师名称
    private String speakerName;
    //学校名称
    private String schoolName;

    //图片数量
    private Integer picCount;

    //直播数量
    private Integer liveCount;

    //记录录播主机推流、断流状态;  00：推流失败；01：推流成功;10：断流失败;11:断流成功;offline:设备离线
    private String controllerStatus;



    public Live() {
    }

    public Live(int liveId, String title, String type, int number, String beginTime, String endTime,
                String streamCode,String details, String cover, Date createTime, int status, int isDel) {
        this.liveId = liveId;
        this.title = title;
        this.type = type;
        this.number = number;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.streamCode = streamCode;
        this.details = details;
        this.cover = cover;
        this.createTime = createTime;
        this.status = status;
        this.isDel=isDel;
    }

    public Live(Date actualBeginTime, Date actualEndTime, String pushClientIp, int videoFrameRate,
                int audioFrameRate,int bitRate, int number, float bandWidth,float trafficValue) {
        this.actualBeginTime = actualBeginTime;
        this.actualEndTime = actualEndTime;
        this.pushClientIp = pushClientIp;
        this.videoFrameRate = videoFrameRate;
        this.audioFrameRate=audioFrameRate;
        this.bitRate = bitRate;
        this.number = number;
        this.bandWidth = bandWidth;
        this.trafficValue=trafficValue;
    }


    public Live(String licenseCode,String hitevisionAccount,String speakerName,String schoolName) {
        this.licenseCode=licenseCode;
        this.hitevisionAccount = hitevisionAccount;
        this.speakerName=speakerName;
        this.schoolName=schoolName;
    }

    public int getLiveId() {
        return liveId;
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String  beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Date getActualBeginTime() {
        return actualBeginTime;
    }

    public void setActualBeginTime(Date actualBeginTime) {
        this.actualBeginTime = actualBeginTime;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public String getStreamCode() {
        return streamCode;
    }

    public void setStreamCode(String streamCode) {
        this.streamCode = streamCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }

    public String getPushClientIp() {
        return pushClientIp;
    }

    public void setPushClientIp(String pushClientIp) {
        this.pushClientIp = pushClientIp;
    }

    public int getVideoFrameRate() {
        return videoFrameRate;
    }

    public void setVideoFrameRate(int videoFrameRate) {
        this.videoFrameRate = videoFrameRate;
    }

    public int getAudioFrameRate() {
        return audioFrameRate;
    }

    public void setAudioFrameRate(int audioFrameRate) {
        this.audioFrameRate = audioFrameRate;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(float bandWidth) {
        this.bandWidth = bandWidth;
    }

    public float getTrafficValue() {
        return trafficValue;
    }

    public void setTrafficValue(float trafficValue) {
        this.trafficValue = trafficValue;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public String getHitevisionAccount() {
        return hitevisionAccount;
    }

    public void setHitevisionAccount(String hitevisionAccount) {
        this.hitevisionAccount = hitevisionAccount;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Integer getPicCount() {
        return picCount;
    }

    public void setPicCount(Integer picCount) {
        this.picCount = picCount;
    }

    public Integer getLiveCount() {
        return liveCount;
    }

    public void setLiveCount(Integer liveCount) {
        this.liveCount = liveCount;
    }

    public String getControllerStatus() {
        return controllerStatus;
    }

    public void setControllerStatus(String controllerStatus) {
        this.controllerStatus = controllerStatus;
    }
}
