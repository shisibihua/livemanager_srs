package com.honghe.livemanager.model;

/**
 * 腾讯云监黄实体
 */
public class LiveTencentSupervise {
    //预警策略 ID
    private int tid;
    //直播码
    private String streamId;
    private String channelId;
    //预警图片链接
    private String img;
    //识别为黄图的置信度，范围 0-100；confidence 大于 83 定为疑似图片
    private int confidence;
    //截图时间 Unix 时间戳
    private int screenshotTime;
    //请求发送时间 时间戳
    private int sendTime;
    //图片房间类型
    private int[] type;
    //图片为正常图片的评分
    private int normalScore;
    //图片为性感图片的评分
    private int hotScore;
    //图片为色情图片的评分
    private int pornScore;
    //图片的级别
    private int level;
    //图片的 OCR 识别信息（如果有）
    private String ocrMsg;
    //一个包含 AbductionRisk 结构的数组
    private int[] abductionRisk;


    public LiveTencentSupervise() {
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public int getScreenshotTime() {
        return screenshotTime;
    }

    public void setScreenshotTime(int screenshotTime) {
        this.screenshotTime = screenshotTime;
    }

    public int getSendTime() {
        return sendTime;
    }

    public void setSendTime(int sendTime) {
        this.sendTime = sendTime;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int[] getType() {
        return type;
    }

    public void setType(int[] type) {
        this.type = type;
    }

    public int getNormalScore() {
        return normalScore;
    }

    public void setNormalScore(int normalScore) {
        this.normalScore = normalScore;
    }

    public int getHotScore() {
        return hotScore;
    }

    public void setHotScore(int hotScore) {
        this.hotScore = hotScore;
    }

    public int getPornScore() {
        return pornScore;
    }

    public void setPornScore(int pornScore) {
        this.pornScore = pornScore;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getOcrMsg() {
        return ocrMsg;
    }

    public void setOcrMsg(String ocrMsg) {
        this.ocrMsg = ocrMsg;
    }

    public int[] getAbductionRisk() {
        return abductionRisk;
    }

    public void setAbductionRisk(int[] abductionRisk) {
        this.abductionRisk = abductionRisk;
    }
}
