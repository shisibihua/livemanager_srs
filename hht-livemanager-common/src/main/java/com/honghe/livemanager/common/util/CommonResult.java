package com.honghe.livemanager.common.util;

import com.honghe.livemanager.common.pojo.model.Result;

/**
 * result返回值
 *
 * @Author libing
 * @Date: 2018-02-28 9:54
 * @Mender:
 */
public class CommonResult {

    public static CommonResult Instance = new CommonResult();

    public  void  getResult(Object t,Result result){
        if(null==t){
            result.setResult("");
            result.setCode(TipsMessage.FAILED_CODE);
            result.setMsg(TipsMessage.FAILED_MSG);
        }else {
            result.setCode(TipsMessage.SUCCESS_CODE);
            result.setResult(t);
        }
    }




}
