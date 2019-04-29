package com.honghe.livemanager.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量表
 * Created by 超文 on 2017/05/02.
 * version 1.0
 */
public interface Constants {
    /**
     * 异常信息统一头信息<br>
     * 非常遗憾的通知您,程序发生了异常
     */
    public static final String Exception_Head = "boom。炸了。";
    /**
     * 缓存键值
     */
    public static final Map<Class<?>, String> cacheKeyMap = new HashMap<>();
    /**
     * 保存文件所在路径的key，eg.FILE_MD5:1243jkalsjflkwaejklgjawe
     */
    public static final String FILE_MD5_KEY = "FILE_MD5:";
    /**
     * 保存上传文件的状态
     */
    public static final String FILE_UPLOAD_STATUS = "FILE_UPLOAD_STATUS";
        /**
     * 正在直播
     */
    public static int LIVE_LIVING_STATUS=1;
    /**
     * 等待直播
     */
    public static final int LIVE_WAITTING_STATUS=2;
    /**
     * 直播结束
     */
    public static int LIVE_OVER_STATUS=3;
    /**
     * 直播关闭
     */
    public static final int LIVE_OFF_STATUS=4;
    /**
     * 直播已删除
     */
    public static final int LIVE_IS_DEL=0;
    /**
     * 启用直播
     */
    public static final int LIVE_ENABLE=0;
    /**
     * 禁用(删除)直播
     */
    public static final int LIVE_DISENABLE=1;
    /**
     * 启用
     */
    public static final int ENABLE=1;
    /**
     * 禁用
     */
    public static final int DISENABLE=0;

    /**
     * 允许推流
     */
    public static final String LIVE_PERMIT_PUSH="1";
    /**
     * 禁止推流
     */
    public static final String LIVE_BAN_PUSH="0";
    public static final String LIVE_NO_DELETE="该直播正在直播，不允许禁用。";
    public static final String LIVE_NO_UPDATE="该直播正在直播，不允许修改。";
    public static final String LIVE_NO_EXITS="该直播不存在。";
    public static final String LIVE_ENABLE_FAILED="该直播启用失败。";
    public static final String LIVE_DISENABLE_FAILED="该直播禁用失败。";
    public static final String LIVE_ENABLE_DISABLE="当前时间直播数量已达极限值，不允许启用。";
    public static final String LIVE_DELETE_FAILED="该直播删除失败。";
    public static final String LIVE_UPDATE_FAILED="该直播修改失败。";
    public static final String LIVE_ORDER_DISABLE="当前时间直播数量已达极限值，不允许预约。";
    public static final String LIVE_ORDER_INVALID="预约开始时间小于当前时间，无效预约。";
    public static final String LIVE_ORDER_TIME_FALSE="预约开始时间不能大于结束时间，无效预约。";
    public static final String LIVE_ORDER_TIME_ERROR="预约时间错误。";
    public static final String LIVE_DATAFORMATS_ERROR="直播json数据格式错误。";
    public static final String USER_LOGIN_FAILED = "用户登录失败。";
    public static final String USER_SEARCH_FAILED = "无此用户。";
    public static final String USER_UPDATE_PWD_FAILED = "修改密码失败。";
    public static final String STR_TO_INT_ERROR = "字符串转换整型异常。";
    public static final String USER_OLDPWD_FALSE = "用户输入的旧密码与原密码不一致。";
    public static final String PLATFORM_START_UP="程序启动完成...";
    public static final String LIVE_PLATFORM_SERVICE="平台服务";
    public static final String TENCENT_CLOUD_SOURCE = "腾讯云";
    public static final String LIVE_LICENSE = "直播授权";
    public static final String SRS_CLOUD_SOURCE = "SRS";

}
