package com.honghe.livemanager.entity;

import java.util.Date;

/**
 * 直播监黄，保存监黄图片链接及直播码
 * @author caoqian
 * @date 20180919
 */
public class LiveSupervise {
    private int superviseId;
    //图片链接
    private String img;
    //直播码
    private String streamCode;
    //截图时间
    private Date screenShotTime;
    //记录创建时间
    private Date createTime;

    public LiveSupervise() {
    }

    public LiveSupervise(int superviseId, String img, String streamCode, Date screenShotTime, Date createTime) {
        this.superviseId = superviseId;
        this.img = img;
        this.streamCode = streamCode;
        this.screenShotTime = screenShotTime;
        this.createTime = createTime;
    }

    public int getSuperviseId() {
        return superviseId;
    }

    public void setSuperviseId(int superviseId) {
        this.superviseId = superviseId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStreamCode() {
        return streamCode;
    }

    public void setStreamCode(String streamCode) {
        this.streamCode = streamCode;
    }

    public Date getScreenShotTime() {
        return screenShotTime;
    }

    public void setScreenShotTime(Date screenShotTime) {
        this.screenShotTime = screenShotTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
