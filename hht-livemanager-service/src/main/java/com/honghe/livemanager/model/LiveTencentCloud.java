package com.honghe.livemanager.model;

/**
 * 腾讯云回调实体，用于接收腾讯云回调信息
 * @author caoqian
 * @date 20180907
 */
public class LiveTencentCloud {
    //有效时间,UNIX 时间戳（十进制）
    private String t;
    //安全签名
    private String sign;
    //事件类型	目前可能值为： 0、1、100、200
    private int event_type;
    //直播码
    private String stream_id;
    //直播码，同stream_id
    private String channel_id;
    //推流路径
    private String appname;
    //推流域名
    private String app;
    //消息产生的时间
    private int event_time;
    //消息序列号，标识一次推流活动，一次推流活动会产生相同序列号的推流和断流消息
    private String sequence;
    //Upload 接入点的 IP
    private String node;
    //用户推流 IP
    private String user_ip;
    //断流错误码
    private int errcode;
    //断流错误信息
    private String errmsg;
    //推流 url 参数
    private String stream_param;
    //推流时长
    private String push_duration;
    //截图时间戳	int	截图时间戳（unix 时间戳，由于 I 帧干扰，暂时不能精确到秒级）
    private String create_time;
    //截图全路径
    private String pic_full_url;

    public LiveTencentCloud() {
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getEvent_type() {
        return event_type;
    }

    public void setEvent_type(int event_type) {
        this.event_type = event_type;
    }

    public String getStream_id() {
        return stream_id;
    }

    public void setStream_id(String stream_id) {
        this.stream_id = stream_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public int getEvent_time() {
        return event_time;
    }

    public void setEvent_time(int event_time) {
        this.event_time = event_time;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getUser_ip() {
        return user_ip;
    }

    public void setUser_ip(String user_ip) {
        this.user_ip = user_ip;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getStream_param() {
        return stream_param;
    }

    public void setStream_param(String stream_param) {
        this.stream_param = stream_param;
    }

    public String getPush_duration() {
        return push_duration;
    }

    public void setPush_duration(String push_duration) {
        this.push_duration = push_duration;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getPic_full_url() {
        return pic_full_url;
    }

    public void setPic_full_url(String pic_full_url) {
        this.pic_full_url = pic_full_url;
    }
}
