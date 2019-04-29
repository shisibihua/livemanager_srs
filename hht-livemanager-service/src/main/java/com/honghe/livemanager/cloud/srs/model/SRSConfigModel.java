package com.honghe.livemanager.cloud.srs.model;

import java.util.Date;

/**
 * srs服务实体
 * @author caoqian
 * @date 20181019
 */
public class SRSConfigModel {
    private int srsId;
    //srs服务ip
    private String srsIp;
    //srs服务api端口
    private int apiPort;
    //srs服务推流端口
    private int pushPort;
    //srs服务拉流端口
    private int playPort;
    //创建时间
    private Date createTime;

    public SRSConfigModel() {
    }

    public SRSConfigModel(String srsIp, int apiPort, int pushPort, int playPort, Date createTime) {
        this.srsIp = srsIp;
        this.apiPort = apiPort;
        this.pushPort = pushPort;
        this.playPort = playPort;
        this.createTime=createTime;
    }

    public int getSrsId() {
        return srsId;
    }

    public void setSrsId(int srsId) {
        this.srsId = srsId;
    }

    public String getSrsIp() {
        return srsIp;
    }

    public void setSrsIp(String srsIp) {
        this.srsIp = srsIp;
    }

    public int getApiPort() {
        return apiPort;
    }

    public void setApiPort(int apiPort) {
        this.apiPort = apiPort;
    }

    public int getPushPort() {
        return pushPort;
    }

    public void setPushPort(int pushPort) {
        this.pushPort = pushPort;
    }

    public int getPlayPort() {
        return playPort;
    }

    public void setPlayPort(int playPort) {
        this.playPort = playPort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
