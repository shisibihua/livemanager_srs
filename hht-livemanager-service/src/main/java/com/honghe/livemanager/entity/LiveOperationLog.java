package com.honghe.livemanager.entity;

import java.util.Date;

/**
 * 操作日志
 * @author caoqian
 * @date 20180910
 */
public class LiveOperationLog {
    private int logId;
    //模块名称
    private String module;
    //日志描述
    private String description;
    //远程访问ip
    private String userIp;
    //用户名
    private String userName;
    //日志创建时间
    private Date createTime;

    public LiveOperationLog() {
    }

    public LiveOperationLog(int logId, String module, String description, String userIp, String userName) {
        this.logId = logId;
        this.module = module;
        this.description = description;
        this.userIp = userIp;
        this.userName = userName;
        Date now=new Date();
        this.createTime = now;
    }

    public LiveOperationLog(String module, String description, String userIp, String userName) {
        this.module = module;
        this.description = description;
        this.userIp = userIp;
        this.userName = userName;
        Date now=new Date();
        this.createTime = now;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
