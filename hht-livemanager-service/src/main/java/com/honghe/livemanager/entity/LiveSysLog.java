package com.honghe.livemanager.entity;

import java.util.Date;

/**
 * 系统日志
 * @author caoqian
 * @date 20180910
 */
public class LiveSysLog {
    private int logId;
    //日志级别，ERROR,INFO,DEBUG
    private String level;
    //日志描述
    private String message;
    //日志描述
    private String description;
    //来源
    private String source;
    //日志创建时间
    private Date createTime;

    public LiveSysLog() {
    }

    public LiveSysLog(int logId, String level, String message, String description, String source) {
        this.logId = logId;
        this.level = level;
        this.message = message;
        this.description = description;
        this.source = source;
        Date now=new Date();
        this.createTime = now;
    }

    public LiveSysLog(String level, String description, String source) {
        this.level = level;
        this.description = description;
        this.source = source;
        Date now=new Date();
        this.createTime = now;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public enum Level{
        ERROR("ERROR"),
        INFO("INFO"),
        DEBUG("DEBUG");

        private Level(String value){
            this.value = value;
        }
        private String value = "";

        public String value(){
            return value;
        }
    }
}
