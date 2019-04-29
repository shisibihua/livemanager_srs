package com.honghe.livemanager.entity;

import java.util.Date;

public class LiveHistory {
    private Integer liveHistoryDId;

    private Date createDate;

    private Date timePoint;

    private Date createTime;

    //观看人数
    private Integer viewersNumber;
    //截图数量
    private Integer picCount;
    //流量
    private Float trafficValue;
    //ֱ直播数量
    private Integer liveCount;
    //学校名称
    private String schoolName;
    //排序类型 countOrder:直播数量排序; screenShotPicOrder:截图数量排序; trafficValueOrder:流量排序; viewersNumberOrder:观看人数
    private String orderType;
    //升序:asc;降序:desc
    private String sort;
    //开始时间
    private String beginTime;
    //ֱ结束时间
    private String endTime;
    //当前页
    private int currentPage;
    //每页大小
    private int pageSize;

    public Integer getLiveHistoryDId() {
        return liveHistoryDId;
    }

    public void setLiveHistoryDId(Integer liveHistoryDId) {
        this.liveHistoryDId = liveHistoryDId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(Date timePoint) {
        this.timePoint = timePoint;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getViewersNumber() {
        return viewersNumber;
    }

    public void setViewersNumber(Integer viewersNumber) {
        this.viewersNumber = viewersNumber;
    }

    public Integer getPicCount() {
        return picCount;
    }

    public void setPicCount(Integer picCount) {
        this.picCount = picCount;
    }

    public Float getTrafficValue() {
        return trafficValue;
    }

    public void setTrafficValue(Float trafficValue) {
        this.trafficValue = trafficValue;
    }

    public Integer getLiveCount() {
        return liveCount;
    }

    public void setLiveCount(Integer liveCount) {
        this.liveCount = liveCount;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getBeginTime() {
        return beginTime+" 00:00:00";
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime+" 23:59:59";
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}