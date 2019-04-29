package com.honghe.livemanager.controller;
import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.cloud.tencent.api.CloudTencentApi;
import com.honghe.livemanager.common.pojo.model.Page;
import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.common.util.*;
import com.honghe.livemanager.entity.Live;
import com.honghe.livemanager.entity.LiveSysLog;
import com.honghe.livemanager.model.LiveTencentCloud;
import com.honghe.livemanager.model.LiveTencentSupervise;
import com.honghe.livemanager.service.LiveService;
import com.honghe.livemanager.service.LiveSuperviseService;
import com.honghe.livemanager.service.LiveSysLogService;
import com.honghe.livemanager.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@CrossOrigin
@RestController("liveManagerController")
@RequestMapping("live")
public class LiveManagerController {
    private Logger log= LoggerFactory.getLogger(LiveManagerController.class);
    @Autowired
    private LiveService liveService;
    @Autowired
    private LiveSysLogService liveSysLogService;
    @Autowired
    private LiveSuperviseService liveSuperviseService;
    private static final String ALL_STATUS="10";


    @Autowired
    CloudTencentApi cloudTencentApi;

    /**
     * 心跳接口
     * @author caoqian
     * @return
     */
    @RequestMapping(value = "heartBeat",method = RequestMethod.POST)
    public Result addLive(){
        Result result  =new Result();
        result.setCode(0);
        result.setResult(true);
        return  result;
    }

    /**
     * 添加直播,返回推流地址与接流地址
     * @author caoqian
     * @return
     */
    @MyLog("直播管理-添加直播")
    @RequestMapping(value = "add",method = RequestMethod.POST)
    public Result addLive(@RequestBody Live live){
        if(live==null){
            return ConvertResult.getParamErrorResult();
        }
        return liveService.getAddLiveResult(live);
    }

    /**
     * 根据直播id查询直播信息,宽带、在线人数等
     * @author caoqian
     * @param liveId  直播id
     * @return
     */
    @RequestMapping(value = "getLiveById",method = RequestMethod.GET)
    public Result getLiveById(int liveId){
        if(0==liveId){
            return ConvertResult.getParamErrorResult();
        }
        return liveService.getLiveDataById(liveId);
    }

    /**
     * 分页获取直播信息,可根据直播状态分页，status可为空
     * @param status          直播状态，1：正在直播；2：等待直播；3：已结束；4:禁用的直播
     * @param searchTime      查询时间点，格式:yyyy-MM-dd HH:mm:ss
     * @param currentDate     当前日期，格式：yyyy-MM-dd
     * @param beginTime       查询开始时间，格式：yyyy-MM-dd HH:mm:ss
     * @param endTime         查询结束时间，格式：yyyy-MM-dd HH:mm:ss
     * @param currentPage     当前页
     * @param pageSize        分页大小
     * @return
     */
    @RequestMapping(value = "getLiveListByPage",method = RequestMethod.GET)
    public Result getLiveListByPage(String status,String searchTime,String currentDate,
                                    String beginTime,String endTime,int currentPage,int pageSize){
        if(status==null){
            return ConvertResult.getParamErrorResult();
        }
        if(ParamUtil.isEmpty(status)){
            status=ALL_STATUS;
        }
        Page livePage=null;
        try{
             Map<String,Object> params=new HashMap<>();
             params.put("searchTime",searchTime);
             params.put("currentDate",currentDate);
             params.put("beginTime",beginTime);
             params.put("endTime",endTime);
             int start=(currentPage-1) * pageSize;
             params.put("start",start);
             params.put("pageSize",pageSize);
             params.put("status",status);
             params.put("pageFlag",true);
             List<Map<String,Object>> liveList=liveService.getLiveListByPage(params);

             //获取总条数
             params.put("pageFlag",false);
             int totalCount=0;
             List<Map<String,Object>> totalCountList=liveService.getLiveListByPage(params);
             if(totalCountList!=null && totalCountList.size()>0){
                 totalCount=Integer.parseInt(String.valueOf(totalCountList.get(0).get("totalCount")));
             }
             livePage=new Page(liveList,currentPage,pageSize,totalCount);
        }catch (Exception e){
            log.error("根据直播状态获取直播列表异常,status="+status,e);
            Result errorResult=new Result();
            errorResult.setMsg("根据直播状态获取直播列表异常");
            return ConvertResult.getErrorResult(errorResult);
        }
        return ConvertResult.getSuccessResult(livePage);
    }

    /**
     * 根据当前日期获取折线图当前时间点正直播数量（及开始时间在整点的直播）总和
     * @param currentDate   当前日期，格式:yyyy-MM-dd
     * @return
     */
    @RequestMapping(value = "getLineChartLiveNum",method = RequestMethod.GET)
    public Result getLineChartLiveNum(String currentDate){
        if(ParamUtil.isEmpty(currentDate)){
            return ConvertResult.getParamErrorResult();
        }
        Map<String,Object> lineChartMap=new HashMap<>();
        try{
            lineChartMap=liveService.getLineChartLiveNum(currentDate);
        }catch (Exception e){
            log.error("获取直播折线图直播数量异常",e);
            Result errorResult=new Result();
            errorResult.setMsg("获取直播折线图直播数量异常");
            return ConvertResult.getErrorResult(errorResult);
        }
        return ConvertResult.getSuccessResult(lineChartMap);
    }

    /**
     * 导出直播
     * @param currentDate     当前日期，格式:yyyy-MM-dd
     * @param status          直播状态，1：正在直播；2：等待直播；3：已结束；4:禁用的直播
     * @param beginTime       查询开始时间，格式：yyyy-MM-dd HH:mm:ss
     * @param endTime         查询结束时间，格式：yyyy-MM-dd HH:mm:ss
     * @return
     */
    @MyLog("直播管理-导出直播列表")
    @RequestMapping(value = "exportLiveList",method = RequestMethod.GET)
    public Result exportLiveList(String currentDate,String status,String beginTime,String endTime){
        if(ParamUtil.isEmpty(currentDate) || status==null){
            return ConvertResult.getParamErrorResult();
        }
        if("".equals(status)){
            status=ALL_STATUS;
        }
        Map<String,Object> params=new HashMap<>();
        params.put("currentDate",currentDate);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);
        params.put("status",status);
        params.put("pageFlag",false);
        List<Map<String,Object>> liveList=liveService.getLiveList(params);
        String fileName="";
        if(liveList!=null && !liveList.isEmpty()) {
            String[] headers = {"学校名称","直播名称","主讲人","计划直播时间","实际直播时间","推流设备IP","推流地址",
                    "RTMP流地址","FLV流地址","HLS流地址","推流帧率KB/S","推流码率KB/S","带宽Mbps","流量","观看人数","直播状态"};
            Date date=new Date();
            String name="LiveDownExcel-"+date.getTime();
            fileName=ExcelTools.getInstance().exportExcel(name,headers,liveList,params,"liveList");
        }else{
            Result result=new Result();
            result.setCode(Result.Code.Success.value());
            result.setMsg("直播列表为空");
            return result;
        }
        return ConvertResult.getSuccessResult(fileName);
    }

    /**
     * 下载导出的直播文件
     * @param fileName   文件名
     * @param res
     * @return
     */
    @RequestMapping(value = "downLoadLiveFile",method = RequestMethod.GET)
    public Result downLoadLiveFile(String fileName,HttpServletResponse res){
        if(ParamUtil.isEmpty(fileName)){
            return ConvertResult.getParamErrorResult();
        }
        return ConvertResult.getSuccessResult(ExcelTools.getInstance().downLoadFile(fileName,res));
    }

    /**
     * 根据开始日期、结束日期查询直播数量（月历查询）
     * @param startDate   开始日期，格式:yyyy-MM-dd
     * @param endDate     结束日期，格式:yyyy-MM-dd
     * @return
     */
    @RequestMapping(value = "getLiveNumByDate",method = RequestMethod.GET)
    public Result getLiveNumByDate(String startDate,String endDate){
        if(ParamUtil.isEmpty(startDate) || ParamUtil.isEmpty(endDate)){
            return ConvertResult.getParamErrorResult();
        }
        String beginTime=startDate+" 00:00:00";
        String endTime=endDate+" 23:59:59";
        Map<String,String> liveNum=liveService.getLiveNumByDate(beginTime,endTime);
        return ConvertResult.getSuccessResult(liveNum);
    }
    /**
     * 修改直播数据
     * 返回值有问题，需要禁用原直播流，生成新的直播流
     * @return
     */
    /*@MyLog("直播管理-修改直播")
    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Result updateLive(@RequestBody Live live){
        if(live==null || ParamUtil.isEmpty(String.valueOf(live.getLiveId()))){
            return ConvertResult.getParamErrorResult();
        }
        Result result=new Result(0);
        try{
            live=liveService.convertLive(live);
            Map<String,Object> liveMap=liveService.getLiveById(live.getLiveId());
            if(liveMap!=null && !liveMap.isEmpty()){
                if(String.valueOf(Constants.LIVE_LIVING_STATUS).equals(liveMap.get("status"))){
                    result.setMsg(Constants.LIVE_NO_UPDATE);
                }else{
                    int re_value=liveService.updateLive(live);
                    if(re_value>0){
                        result.setResult(true);
                    }else{
                        result.setResult(false);
                        result.setMsg(Constants.LIVE_UPDATE_FAILED);
                    }
                }
            }else{
                result.setResult(false);
                result.setMsg(Constants.LIVE_NO_EXITS);
            }
        }catch (JSONException e){
            log.error("直播数据转换json异常",e);
            return ConvertResult.getParamErrorResult(Constants.LIVE_DATAFORMATS_ERROR);
        }catch (Exception e){
            log.error("修改直播数据异常",e);
            Result errorResult=new Result();
            errorResult.setMsg("修改直播数据异常");
            return ConvertResult.getErrorResult(errorResult);
        }
        return result;
    }*/

    /**
     * 根据直播id删除直播信息（逻辑删除）
     * @param liveId    直播id
     * @param isEnable  是否禁用，0:启用；1:禁用
     * @return
     */
    @MyLog("直播管理-禁用/启用直播")
    @RequestMapping(value = "deleteLiveById",method = RequestMethod.GET)
    public Result deleteLiveById(int liveId,int isEnable){
        if(0==liveId){
            return ConvertResult.getParamErrorResult();
        }
        return liveService.deleteLiveById(liveId,isEnable);
    }

    /**
     * 暂停并延时恢复推流
     * @param streamCode    直播码
     * @param time        过期时间,格式:yyyy-MM-dd HH:mm:ss
     * @param endTime     禁播截止的时间,格式:yyyy-MM-dd HH:mm:ss
     * @param action      动作标识，0:断流；1：恢复推流
     * @param path        路径:多路径用户使用，可为空
     * @param domain      域名:多域名用户使用，可为空
     * @return
     */
    @MyLog("直播管理-暂停并延时恢复推流")
    @RequestMapping(value="channelManager",method = RequestMethod.GET)
    public Result channelManager(String streamCode,String time,String endTime,String action,String path,String domain ){
        if(ParamUtil.isEmpty(streamCode) || ParamUtil.isEmpty(time) || ParamUtil.isEmpty(endTime) || ParamUtil.isEmpty(action)){
            return ConvertResult.getParamErrorResult();
        }
        JSONObject re_value=cloudTencentApi.channelManager(streamCode,time,endTime,action,path,domain);
        Result result=new Result();
        if(re_value!=null && !re_value.isEmpty() &&
                TipsMessage.SUCCESS_CODE==Integer.parseInt(String.valueOf(re_value.get("code")))){
            result.setCode(TipsMessage.SUCCESS_CODE);
            result.setResult(re_value.get("success"));
        }else{
            result.setCode(TipsMessage.PASSWORD_ERROR_CODE);
            result.setResult(re_value.get("success"));
            result.setMsg(re_value.get("errMsg").toString());
        }
        return result;
    }

    /**
     * 设置延播
     * @param streamCode     直播码
     * @param delayTime    延播时间，单位：秒，上线为600s
     * @return
     */
    @MyLog("直播管理-设置延播")
    @RequestMapping(value="setLiveDelay",method = RequestMethod.GET)
    public Result setLiveDelay(String streamCode,String time,String delayTime){
        if(ParamUtil.isEmpty(streamCode) || ParamUtil.isEmpty(delayTime) || ParamUtil.isEmpty(time)){
            return ConvertResult.getParamErrorResult();
        }
        JSONObject re_value=cloudTencentApi.setLiveDelay(streamCode,time,Integer.parseInt(delayTime));
        Result result=new Result();
        if(re_value!=null && !re_value.isEmpty() &&
                TipsMessage.SUCCESS_CODE==Integer.parseInt(String.valueOf(re_value.get("code")))){
            result.setCode(TipsMessage.SUCCESS_CODE);
            result.setResult(re_value.get("success"));
        }else{
            result.setCode(TipsMessage.PASSWORD_ERROR_CODE);
            result.setResult(re_value.get("success"));
            result.setMsg(re_value.get("errMsg").toString());
        }
        return result;
    }

    /**
     * 根据直播码查询直播在线状态
     * @param streamCode  直播码
     * @param time        过期时间
     * @return
     */
    @RequestMapping(value = "getLiveStatus",method = RequestMethod.GET)
    public Result getLiveStatus(String streamCode,String time){
        if(ParamUtil.isEmpty(streamCode)){
            return ConvertResult.getParamErrorResult();
        }
        JSONObject re_value=liveService.getLiveStatusByApiChoose(streamCode,time);
        Result result=new Result();
        if(re_value!=null && !re_value.isEmpty() && TipsMessage.SUCCESS_CODE==Integer.parseInt(String.valueOf(re_value.get("code")))){
            result.setCode(TipsMessage.SUCCESS_CODE);
            result.setResult(re_value.get("liveData"));
        }else{
            result.setCode(TipsMessage.PASSWORD_ERROR_CODE);
            result.setResult(null);
            result.setMsg(re_value.get("errMsg").toString());
        }
        return result;
    }

    /**
     * 接收腾讯云断流、推流、截图回调信息
     * @return
     */
    @RequestMapping(value = "getCallBack",method = RequestMethod.POST)
    public String getCallBack(@RequestBody LiveTencentCloud liveTencentCloud){
        return liveService.getCallBack(liveTencentCloud);
    }

    /**
     * 腾讯云监黄回调
     * @param liveTencentSupervise
     * @return
     */
    @RequestMapping(value = "getSuperviseCallBack",method = RequestMethod.POST)
    public String getSuperviseCallBack(@RequestBody LiveTencentSupervise liveTencentSupervise, HttpServletRequest request){
        if(liveTencentSupervise==null){
            log.info("接收腾讯云监黄回调实体为空。");
            LiveSysLog liveSysLog=new LiveSysLog(LiveSysLog.Level.INFO.value(),"接收腾讯云监黄回调实体为空",
                    Constants.TENCENT_CLOUD_SOURCE);
            liveSysLogService.addSysLog(liveSysLog);
            return "";
        }
        String result=liveService.getSuperviseResult(liveTencentSupervise,request);
        return result;
    }

    /**
     * 获取涉黄图片数量
     * @return
     */
    @RequestMapping(value = "getSupervisePicCount",method = RequestMethod.GET)
    public Result getSupervisePicCount(){
        int count=liveSuperviseService.getPicCount();
        return ConvertResult.getSuccessResult(count);
    }

    /**
     * 分页获取监黄列表
     * @param currentPage  当前页
     * @param pageSize     每页大小
     * @return
     */
    @RequestMapping(value = "getSuperviseListByPage",method = RequestMethod.GET)
    public Result getSuperviseListByPage(int currentPage,int pageSize){
        if(currentPage==0 || pageSize==0){
            return ConvertResult.getParamErrorResult();
        }
        Page page=liveSuperviseService.getLiveSuperviseListByPage(currentPage,pageSize);
        return ConvertResult.getSuccessResult(page);
    }

    /**
     * 获取监黄列表
     * @return
     */
    @RequestMapping(value = "getSuperviseList",method = RequestMethod.GET)
    public Result getSuperviseList(){
        return ConvertResult.getSuccessResult(liveSuperviseService.getLiveSuperviseList());
    }
}
