package com.honghe.livemanager.common.pojo.model;

/**
 * @Description 返回结果实体对象
 * @Author sunchao
 * @Date: 2017-11-29 14:43
 * @Mender: libing
 */
//@JsonInclude(NON_NULL)
public final class Result {

    private int code = 0;
    private String msg;
    private Object result;

    public Result(){}


    /**
     * 构造方法
     * @param statusCode 错误码
     * @param msgInfo 提示信息
     */
    public Result(int statusCode,String msgInfo) {
        this.msg = msgInfo;
        this.code = statusCode;
    }

	public Result(int statusCode , Object result) {
		this(statusCode , result, "");
	}

    public Result(int statusCode) {
        this(statusCode, "", "");
    }

    public Result(Object result) {
        this(0,result, "");
    }


    public Result( int statusCode,Object result, String msgInfo) {
        this.msg=msgInfo;
        this.code = statusCode;
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        if(msg==null){
            msg="";
        }else {
            this.msg = msg;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int statusCode) {
        this.code = statusCode;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public enum Code{
        NoSuchMethod(-2),
        Success(0),
        ParamError(-1),
        UnKnowError(-3);

        private Code(int value){
            this.value = value;
        }
        private int value = 0;

        public int value(){
            return value;
        }
    }

}