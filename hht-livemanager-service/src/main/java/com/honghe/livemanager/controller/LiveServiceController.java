package com.honghe.livemanager.controller;

import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.entity.Live;
import com.honghe.livemanager.service.LiveService;
import com.honghe.livemanager.util.ConvertResult;
import com.honghe.livemanager.util.MyLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用于提供外部接口
 */
@CrossOrigin
@RestController("liveServiceController")
@RequestMapping("liveService")
public class LiveServiceController {
    @Autowired
    private LiveService liveService;
    /**
     * 添加直播,返回推流地址与接流地址
     * @author caoqian
     * @return
     */
    @MyLog("直播管理-添加直播")
    @RequestMapping(value = "addLive",method = RequestMethod.POST)
    public Result addLive(@RequestBody Live live){
        if(live==null){
            return ConvertResult.getParamErrorResult();
        }
        Result result=liveService.checkLiveServiceIsValid(live);
        if(result.getCode()==Result.Code.Success.value()) {
            return liveService.getAddLiveResult(live);
        }else{
            return result;
        }
    }

    /**
     * 根据直播id删除直播信息（逻辑删除）
     * @param live  直播
     * @return
     */
    @MyLog("直播管理-删除直播")
    @RequestMapping(value = "deleteLiveByLiveId",method = RequestMethod.POST)
    public Result deleteLiveByLiveId(@RequestBody Live live){
        if(live==null){
            return ConvertResult.getParamErrorResult();
        }
        Result result=liveService.checkLiveServiceIsValid(live);
        if(result.getCode()==Result.Code.Success.value()) {
            return liveService.deleteLiveByLiveId(live);
        }else{
            return result;
        }
    }
    /**
     * 获取瞬时直播信息
     * 返回在线人数
     * @param live  直播
     * @return
     */
    @MyLog("直播管理-获取瞬时直播信息")
    @RequestMapping(value = "getLiveDescribe",method = RequestMethod.POST)
    public Result getLiveDescribe(@RequestBody Live live){
        if(live==null){
            return ConvertResult.getParamErrorResult();
        }
        Result result=liveService.checkLiveServiceIsValid(live);
        if(result.getCode()==Result.Code.Success.value()) {
            return liveService.getLiveDescribe(live);
        }else{
            return result;
        }
    }
    /**
     * 获取直播历史播放信息，七天内数据有效
     * 返回在线人数
     * @param live  直播
     * @return
     */
    @MyLog("直播管理-获取直播历史播放信息")
    @RequestMapping(value = "getLiveDescribeHistory",method = RequestMethod.POST)
    public Result getLiveDescribeHistory(@RequestBody Live live){
        if(live==null){
            return ConvertResult.getParamErrorResult();
        }
        Result result=liveService.checkLiveServiceIsValid(live);
        if(result.getCode()==Result.Code.Success.value()) {
            return liveService.getLiveDescribeHistory(live);
        }else{
            return result;
        }
    }
    /**
     * 根据直播码查询直播在线状态  0：断流；1：开启；3：关闭
     * @param live  直播
     * @return
     */
    @MyLog("直播管理-查询直播在线状态")
    @RequestMapping(value = "getLiveStatus",method = RequestMethod.POST)
    public Result getLiveStatus(@RequestBody Live live){
        if(live==null){
            return ConvertResult.getParamErrorResult();
        }
        Result result=liveService.checkLiveServiceIsValid(live);
        if(result.getCode()==Result.Code.Success.value()) {
            return liveService.getLiveStatus(live);
        }else{
            return result;
        }
    }
}
