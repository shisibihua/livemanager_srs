package com.honghe.livemanager.cloud.srs.model;

/**
 * Description:/api/v1/clients/{id} 接口返回的数据结构
 * Author：hyh
 * Date: 2018/10/17
 */
public class ClientModel {
    /**
     *{
     "id": 135,
     "vhost": 9,
     "stream": 12,
     "ip": "192.168.19.121",
     "pageUrl": "",
     "swfUrl": "",
     "tcUrl": "rtmp://192.168.17.68:1935/live",
     "url": "/live/live_teacher_full",
     "type": "fmle-publish",
     "publish": true,
     "alive": 14475284
     }
     */
    //clientId
    private int id;
    //vHostId
    private int vhost;
    //streamId
    private int stream;
    //推流端ip
    private String ip;
    private String pageUrl;
    private String swfUrl;
    //服务端推流地址(不含StreamCode)
    private String tcUrl;
    //live+streamCode
    private String url;
    //客户端类型：推流fmle-publish、播放play
    private String type;
    //是否是推流端
    private boolean publish;
    //连接时长
    private long alive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVhost() {
        return vhost;
    }

    public void setVhost(int vhost) {
        this.vhost = vhost;
    }

    public int getStream() {
        return stream;
    }

    public void setStream(int stream) {
        this.stream = stream;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getSwfUrl() {
        return swfUrl;
    }

    public void setSwfUrl(String swfUrl) {
        this.swfUrl = swfUrl;
    }

    public String getTcUrl() {
        return tcUrl;
    }

    public void setTcUrl(String tcUrl) {
        this.tcUrl = tcUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public long getAlive() {
        return alive;
    }

    public void setAlive(long alive) {
        this.alive = alive;
    }
}
