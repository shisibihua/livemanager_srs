package com.honghe.livemanager.common.exception;

/**
 * 连接异常
 *
 * @Author libing
 * @Date: 2018-02-06 16:46
 * @Mender:
 */
public class ConnectionException extends RuntimeException{
    public ConnectionException(String message){
        super(message);
    }
}
