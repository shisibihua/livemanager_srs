package com.honghe.livemanager.common.util;

/**
 * @Description log日志或异常提示语
 * @Author sunchao
 * @Date: 2017-11-29 17:15
 * @Mender:
 */
public class TipsMessage {

    //请求成功
    public static final int SUCCESS_CODE = 0;
    //请求失败
    public static final int FAILED_CODE = 1;
    //直播授权过期
    public static final int LICENSE_OUTDATE_CODE = 2;
    //直播api安全签名错误
    public static final int LICENSE_SIGN_FALSE_CODE = 3;

    //返回结果错误
    public static final  int FAILED_RETURN = -2 ;

    public static final String FAILED_MSG = "请求失败";

    //参数错误
    public static final int PARAM_ERROR_CODE = -1;

    //内部错误
    public static final int INNER_ERROR_CODE = -3;

    /**
     * i学中学校级别
     */
    public static final int SCHOOL_LEVEL=4;

    //内部错误
    public static final String INNER_ERROR_MSG = "內部錯誤";


    public static final String MSG_KEY = "msg";
    public static final String CODE_KEY = "code";

    public static final String REQUEST_SUCCESS = "请求成功";

    public static final String REQUEST_ERROR = "请求失败";

    public static final String SAVE_ERROR = "保存失败";

    public static final String UPDATE_ERROR = "更新失败";

    public static final String DELETE_ERROR = "删除失败";

    public static final String SELECT_ERROR = "查询失败";

    public static final String PARAM_ERROR = "参数错误";

    public static final String PARES_ERROR = "转换失败";

    public static final String LICENSE_NAME_ERROR = "名称重复";


    public static final String SERVICE_ERROR = "后端服务异常";

	public static final String SERVICE_TIMEOUT = "后端服务超时异常";


    public static final String SERVICE_CLIENT_MSG="请求失败，请检查请求数据，请求类型等是否正确";

    public static final String USER_PARAM_ERROR = "用户名密码不能为空";

    public static final String USER_NOT_EXIST = "用户不存在";

    public static final String USER_EXIST = "用户已存在";

    public static final String FORMAT_DATE_MSG = "日期格式错误";

    // 请求的读取数据超时
    public static final String API_REQ_SOCKET_TIMEOUT_ERROR = "读取数据超时";

    // 请求的获取链接超时
    public static final String API_REQ_CONN_TIMEOUT_ERROR = "获取http连接超时";

    public static final int USER_EXIST_CODE = -1;

    public static final int USER_NOT_EXIST_CODE = -1;

    public static final int PASSWORD_ERROR_CODE = -2;

    public static final String USER_PASSWORD_ERROR = "用户密码错误";

    public static final String USER_LOGIN_ERROR = "用户登录失败";

    public static final String SERVICE_RESPONSE_ERROR = "获取服务响应数据异常";

    public static final String THREAD_ERROR = "线程发生异常";

    // 未知错误
    public static final String ERROR = "未知错误";

    //应用错误信息
    public static final String APP_PARAMETER_ERROR = "请检查必填参数t,sign,appId";
    public static final String APP_POWER_ERROR = "应用没有直播权限";
    public static final String APP_LICENSE_OUTDATE = "直播权限已过期";
    public static final String APP_SIGN_FALSE = "直播安全签名错误";
    public static final String APP_LICENSE_CODE_NULL = "授权码为空";
    public static final String HEDER_NO_USER = "请检查Header信息是否完整";

    //SRS
    public static final String SRS_ACTION_PUBLISH = "on_publish";
    public static final String SRS_ACTION_UNPUBLISH = "on_unpublish";
    public static final String SRS_DEFAULT_IP = "127.0.0.1";
    public static final String SRS_API_STREAMS = "streams/";
    public static final String SRS_API_CLIENTS = "clients/";
    public static final String SRS_API_VHOSTS = "vhosts/";
    //srs服务api
    public static String LOCAL_SRS_API="0";
    //腾讯云api
    public static String TENCENT_CLOUD_API="1";

}
