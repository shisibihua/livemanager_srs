package com.honghe.livemanager.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.cache.LivePicCountCache;
import com.honghe.livemanager.cloud.srs.api.LocalSRSApi;
import com.honghe.livemanager.cloud.srs.util.SRSLiveUtil;
import com.honghe.livemanager.cloud.tencent.api.CloudTencentApi;
import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.common.util.*;
import com.honghe.livemanager.dao.*;
import com.honghe.livemanager.entity.*;
import com.honghe.livemanager.model.LiveTencentCloud;
import com.honghe.livemanager.model.LiveTencentSupervise;
import com.honghe.livemanager.service.LiveLicenseService;
import com.honghe.livemanager.service.LiveService;
import com.honghe.livemanager.service.LiveSysLogService;
import com.honghe.livemanager.util.CloudApiChooseUtil;
import com.honghe.livemanager.util.Constants;
import com.honghe.livemanager.util.ConvertResult;
import com.honghe.livemanager.util.TencetLiveUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;

@Service
public class LiveServiceImpl implements LiveService {
    private static final String LOG_SOURCE = "外部接口调用";
    private org.slf4j.Logger logger = LoggerFactory.getLogger(LiveService.class);
    private static int DUBIOUS_PIC=83;
    @Autowired
    private LiveDao liveDao;
    @Autowired
    private LiveLicenseDao licenseDao;
    @Autowired
    private LiveLicenseService liveLicenseService;
    @Autowired
    private CloudTencentApi  cloudTencentApi;
    @Autowired
    private LiveMaxLimitDao maxLimitDao;
    @Autowired
    private LiveSysLogDao liveSysLogDao;
    @Autowired
    private LiveSuperviseDao liveSuperviseDao;
    @Autowired
    private LiveSysLogService liveSysLogService;
    @Autowired
    private LiveConfigDao liveConfigDao;
    @Autowired
    private LocalSRSApi localSRSApi;

    private TencetLiveUtil  tencetLiveUtil = new TencetLiveUtil();
    private SRSLiveUtil srsLiveUtil= new SRSLiveUtil();

    @Override
    public int saveLive(Live live) {
        int result=0;
        try{
            if(live!=null){
                result=liveDao.addLive(live);
            }
        }catch (Exception e){
            result=0;
            logger.error("保存直播信息异常",e);
        }
        return result;
    }

    @Override
    public int getLivingCount(){
        return liveDao.getLivingCount();
    }
    @Override
    public Map<String,Object> getLiveById(int liveId) {
        Map<String,Object> live=new HashMap<>();
        try{
            live=liveDao.getLiveById(liveId);
            //没有实际结束时间，以预约结束时间为准
            if(live.get("actualEndTime")==null){
                live.put("timeLength",live.get("planTimeLength")==null ? 0: live.get("planTimeLength"));
            }
        }catch (Exception e){
            logger.error("根据直播id查询直播信息异常,liveId="+liveId,e);
        }
        return live;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public int updateLive(Live live) {
        int result=0;
        try{
            if(live!=null){
                result=liveDao.updateLive(live);
            }
        }catch (Exception e){
            result=0;
            logger.error("修改直播信息异常",e);
        }
        return result;
    }

    @Override
    public boolean updateLiveByStreamCode(String eventType,String streamCode,String pushClientIp,String eventTime,int picCount){
        boolean re_value=false;
        Live live=liveDao.getLiveEntityByStreamCode(streamCode);
        if(live!=null) {
            try {
                String actualBeginTime = "";
                String actualEndTime = "";
                int status = 0;
                switch (Integer.parseInt(eventType)) {
                    case 1:
                        //开始直播
                        status = Constants.LIVE_LIVING_STATUS;
                        actualBeginTime = eventTime;
                        if (!"".equals(actualBeginTime))
                            live.setActualBeginTime(DateUtil.parseDatetime(actualBeginTime));
                            live.setActualEndTime(null);
                        break;
                    case 3:
                        //直播关闭
                        status = Constants.LIVE_OVER_STATUS;
                        actualEndTime = eventTime;
                        if (!"".equals(actualEndTime)) {
                            live.setActualEndTime(DateUtil.parseDatetime(actualEndTime));
                        }
                        String apiChoose= CloudApiChooseUtil.API_CHOOSE;
                        if(!ParamUtil.isEmpty(apiChoose)){
                            if(TipsMessage.TENCENT_CLOUD_API.equals(apiChoose)){
                                //腾讯云保存历史记录
                                saveLiveHitory(streamCode, live);
                            }
                        }
                        break;
                }
                live.setStreamCode(streamCode);
                live.setPicCount(picCount);
                live.setPushClientIp(pushClientIp);
                //isDel=1,status=4为禁用直播状态，此时不用修改直播状态
                if(live.getStatus()!=4 || live.getIsDel()!=1) {
                    live.setStatus(status);
                    live.setIsDel(0);
                }
                int result = liveDao.updateLiveByStreamCode(live);
                if (result > 0) {
                    re_value = true;
                } else {
                    re_value = false;
                }
            } catch (Exception e) {
                re_value = false;
                logger.error("根据直播码修改直播实际开始、结束时间及直播状态异常，streamCode=" + streamCode);
            }
        }
        return re_value;
    }

    /**
     * 保存直播历史记录
     * @param streamCode  直播码
     * @param live
     * @return
     */
    private Live saveLiveHitory(String streamCode,Live live){
        Map liveMap = getLiveHistoryEntity(streamCode,live);
        live.setVideoFrameRate(Integer.parseInt(String.valueOf(liveMap.get("videoFrameRate")==null?
                "0": liveMap.get("videoFrameRate"))));
        live.setBitRate(Integer.parseInt(String.valueOf(liveMap.get("bitRate")==null?
                "0":liveMap.get("bitRate")))/(8*1024));
        live.setBandWidth(Float.parseFloat(String.valueOf(liveMap.get("bandWidth")==null?
                "0.0":liveMap.get("bandWidth"))));
        live.setNumber(Integer.parseInt(String.valueOf(liveMap.get("viewersNumber")==null?
                "0":liveMap.get("viewersNumber"))));
        //转换单位为G
        live.setTrafficValue(Float.parseFloat(String.valueOf(liveMap.get("trafficValue")==null?
                "0.0":liveMap.get("trafficValue")))/1024);
        return live;
    }

    /**
     * 根据直播码查询历史记录
     * @param streamCode  直播码
     * @return
     */
    private Map<String,Object> getLiveHistoryEntity(String streamCode,Live live){
        Map<String,Object> liveEntity=new HashMap<>();
        if(!ParamUtil.isEmpty(streamCode) && live!=null){
            //直播实际开始时间
            String actualBeginTime = live.getActualBeginTime() == null ? "" :
                    DateUtil.formatDatetime(live.getActualBeginTime());
            //直播实际结束时间
            String actualEndTime = live.getActualEndTime() == null ? live.getEndTime() :
                    DateUtil.formatDatetime(live.getActualEndTime());
            if(ParamUtil.isEmpty(actualBeginTime)){
                return liveEntity;
            }else {
                getSumLiveHistory(liveEntity, streamCode, actualBeginTime, actualEndTime);
                liveEntity.put("liveId", live.getLiveId());
            }
        }
        return liveEntity;
    }
    @Override
    public Map<String,Object> getSumLiveHistory(Map<String,Object> liveEntity,String streamCode,String actualBeginTime,String actualEndTime){
        //处理推流历史记录
        try {
            Map<String, Object> livePushHistory = new HashMap<>();
            livePushHistory = getSumLivePushHistory(livePushHistory, streamCode, actualBeginTime, actualEndTime);
            if (livePushHistory != null && !livePushHistory.isEmpty()) {
                liveEntity.put("pushClientIp", livePushHistory.get("clientIp").toString());
                liveEntity.put("videoFrameRate", Integer.parseInt(String.valueOf(livePushHistory.get("videoFrameRate"))));
                liveEntity.put("bitRate", Integer.parseInt(String.valueOf(livePushHistory.get("bitRate"))));
            }
            //处理播放历史记录
            Map<String, Object> livePlayHistory = new HashMap<>();
            livePlayHistory = getSumLivePlayHistory(livePlayHistory, streamCode, actualBeginTime, actualEndTime);
            if (livePlayHistory != null && !livePlayHistory.isEmpty()) {
                liveEntity.put("bandWidth", Float.parseFloat(String.valueOf(livePlayHistory.get("bandWidth"))));
                liveEntity.put("viewersNumber", Integer.parseInt(String.valueOf(livePlayHistory.get("viewersNumber"))));
                liveEntity.put("trafficValue", Float.parseFloat(String.valueOf(livePlayHistory.get("trafficValue"))));
            }
        }catch (Exception e){
            logger.error("统计直播推流/播放历史记录异常，直播码streamCode="+streamCode,e);
        }
        return liveEntity;
    }

    @Override
    public Map<String,String> getLiveNumByDate(String beginTime, String endTime){
        Map<String,String> result=new LinkedHashMap<>();
        List<Map<String, Object>> dayList=new ArrayList<>();
        try {
            dayList = liveDao.getLiveNumByDate(beginTime, endTime);
        }catch (Exception e){
            logger.error("查询日期范围内直播数量异常",e);
            return result;
        }
        if (dayList != null && !dayList.isEmpty()) {
            for (Map<String, Object> map : dayList) {
                String day = String.valueOf(map.get("day"));
                result.put(day, String.valueOf(map.get("liveNum")));
            }
        }
        return result;
    }

    @Override
    public Map<String,Object> getLineChartLiveNum(String currentDate) {
        List<String> timeList=new ArrayList<>();
        for(int i=0;i<24;i++){
            String t1="";
            String t2="";
            if(i<10){
                t1+="0"+i+":00";
                t2+="0"+i+":30";
            }else{
                t1+=i+":00";
                t2+=i+":30";
            }
            timeList.add(t1);
            timeList.add(t2);
        }
        List<Map<String,Object>> allLiveList=liveDao.getAllLiveNumByDate(currentDate);
        List<Integer> lineChartLiveNum=new ArrayList<>();
        if(allLiveList!=null && !allLiveList.isEmpty()){
            for(String time:timeList){
                try {
                    Date currentTime=DateUtil.parseDatetime(currentDate+" "+time+":00");
                    int sum=0;
                    for(Map<String,Object> live:allLiveList){
                        Date beginDate=DateUtil.parseDatetime(live.get("beginTime").toString());
                        Date endDate=DateUtil.parseDatetime(live.get("endTime").toString());
                        if(currentTime.getTime()>=beginDate.getTime() && currentTime.getTime()<=endDate.getTime()){
                            sum++;
                        }
                    }
                    lineChartLiveNum.add(sum);
                } catch (ParseException e) {
                    logger.error("获取折线图直播数量异常",e);
                }
            }
        }
        Map<String,Object> result=new HashMap<>();
        result.put("xData",timeList);
        result.put("yData",lineChartLiveNum);
        result.put("totalCount",allLiveList.size());
        return result;
    }

    @Override
    public List<Map<String, Object>> getLiveListByPage(Map<String, Object> params) {
        try {
            if (params != null && !params.isEmpty()) {
                return liveDao.getLiveListByPage(params);
            }
        }catch (Exception e){
            logger.error("查询直播列表异常",e);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> getLiveList(Map<String, Object> params) {
        try {
            if (params != null && !params.isEmpty()) {
                return liveDao.getLiveList(params);
            }
        }catch (Exception e){
            logger.error("查询直播列表异常",e);
        }
        return null;
    }

    @Override
    public Map<String,Object> getLiveByStreamCode(String streamCode){
        try {
            return liveDao.getLiveByStreamCode(streamCode);
        }catch (Exception e){
            logger.error("根据直播码查询直播数据异常",e);
        }
        return null;
    }

    @Override
    public Result getAddLiveResult(Live live) {
        Result re_value=new Result(Result.Code.Success.value());
        //当前时间段正在直播的数量等于最大直播限制数量，则不允许预约成功
        String apiChoose=CloudApiChooseUtil.API_CHOOSE;
        live = convertLive(apiChoose,live);
        //最大限制数量
        int maxCount=maxLimitDao.getMaxCount();
        //预约开始时间或结束时间为空，则视为即时直播
        if(live.getBeginTime()==null || live.getEndTime()==null){
            //当前正直播数量
            int livingCount=getLivingCount();
            if(livingCount>=maxCount){
                re_value.setMsg(Constants.LIVE_ORDER_DISABLE);
            }else{
                saveLiveData(live,re_value);
            }
        }else{
            /**
             * 预约直播，判断预约时间段内直播数量是否等于最大直播限制数量，等于则不允许预约，否则可以预约
             * 情况1：预约开始时间>=begin_time && 预约开始时间<=end_time,开始时间所在时间段内直播数量<最大限制数量
             * 情况2：预约开始时间<=begin_time && 预约结束时间<=end_time,结束时间所在时间段内直播数量<最大限制数量
             * 情况3：预约开始时间<=begin_time && 预约结束时间>=end_time,预约时间所在时间段内直播数量<最大限制数量
             */
            String beginTime=live.getBeginTime();
            String endTime=live.getEndTime();
            int isPermit=isPermitOrderLiveByTime(beginTime,endTime,maxCount);
            if(isPermit==1){
                saveLiveData(live,re_value);
            }else if(isPermit==-1){
                re_value.setMsg(Constants.LIVE_ORDER_INVALID);
            }else if(isPermit==-2){
                re_value.setMsg(Constants.LIVE_ORDER_TIME_FALSE);
            }else if(isPermit==-3){
                re_value.setMsg(Constants.LIVE_ORDER_TIME_ERROR);
            }else if(isPermit==2){
                re_value.setMsg(Constants.LIVE_ORDER_DISABLE);
            }
        }
        return re_value;
    }

    @Override
    public Result getLiveDescribe(Live live) {
        Result result=new Result();
        JSONObject re_value=getLiveStatatisByApiChoose(live.getStreamCode(),"play");
        Map<String,Object> map=new HashMap<>();
        if(re_value!=null && !re_value.isEmpty() &&
                TipsMessage.SUCCESS_CODE==Integer.parseInt(String.valueOf(re_value.get("code")))){
            result.setCode(TipsMessage.SUCCESS_CODE);
            JSONArray jsonArr=JSONArray.parseArray(re_value.get("streamInfo").toString());
            if(jsonArr!=null && !jsonArr.isEmpty()){
                JSONObject streamJson=jsonArr.getJSONObject(0);
                map.put("onlineUserCount",Integer.parseInt(String.valueOf(streamJson.get("online"))));
            }else{
                map.put("onlineUserCount",0);
            }
        }else{
            result.setCode(TipsMessage.PASSWORD_ERROR_CODE);
            result.setMsg(re_value.get("errMsg").toString());
            map.put("onlineUserCount",0);
        }
        result.setResult(map);
        return result;
    }

    @Override
    public Result getLiveDescribeHistory(Live live) {
        String beginTime=live.getBeginTime();
        String endTime=live.getEndTime();
        if (ParamUtil.isEmpty(beginTime) || ParamUtil.isEmpty(endTime)) {
            Result errorResult=new Result(Result.Code.ParamError.value());
            errorResult.setMsg("查询时间不能为空!");
            return ConvertResult.getParamErrorResult(errorResult);
        }
        Result result=new Result();
        //七天前日期
        Date sevenDaysBefore=DateUtil.addDay(new Date(),-7);
        //验证日期是否有效
        try {
            if(sevenDaysBefore.before(DateUtil.parseDatetime(beginTime))){
                /*
                //结束时间延迟1小时
                String delayEndTime=DateUtil.formatDatetime(DateUtil.addHour(DateUtil.parseDatetime(endTime),1));
                Map<String, Object> livePlayHistory = new HashMap<>();
                livePlayHistory=getSumLivePlayHistory(livePlayHistory,live.getStreamCode(),beginTime,delayEndTime);
                */
                Map<String,Object> livePlayHistory=liveDao.getLiveByStreamCode(live.getStreamCode());
                Map<String,Object> map=new HashMap<>();
                if(livePlayHistory!=null && !livePlayHistory.isEmpty()){
                    map.put("onlineUserCount",livePlayHistory.get("viewersNumber"));
                }else{
                    map.put("onlineUserCount",0);
                }
                result.setResult(map);
            }else{
                result.setCode(TipsMessage.PASSWORD_ERROR_CODE);
                result.setMsg("直播查询时间不在七天有效期内。");
            }
        } catch (ParseException e) {
            result.setCode(Result.Code.UnKnowError.value());
            result.setMsg("获取直播历史播放信息失败");
        }
        return result;
    }

    @Override
    public String getCallBack(LiveTencentCloud liveTencentCloud) {
        LiveSysLog liveSysLog=new LiveSysLog();
        liveSysLog.setSource(Constants.TENCENT_CLOUD_SOURCE);
        liveSysLog.setCreateTime(new Date());

        if(liveTencentCloud==null || liveTencentCloud.getT()==null || liveTencentCloud.getSign()==null){
            logger.info("接收腾讯云回调实体为空。");
            liveSysLog.setLevel(LiveSysLog.Level.INFO.value());
            liveSysLog.setDescription("接收腾讯云回调实体为空");
            liveSysLogService.addSysLog(liveSysLog);
            return "";
        }
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>腾讯云回调实体>>>>>>>>"+ JSON.toJSONString(liveTencentCloud));
        JSONObject json=new JSONObject();
        try{
            Map<String,String> callBack = revieveCallBackMsg(liveTencentCloud);
            if(callBack!=null && !callBack.isEmpty()){
                json.put("code",0);
                String eventType=callBack.get("eventType");
                //直播断流
                String streamCode=callBack.get("streamCode");
                if("0".equals(eventType)){
                    //保存直播结束时间及更新status=3
                    String liveStatus=String.valueOf(Constants.LIVE_OVER_STATUS);
                    String pushClientIp=callBack.get("pushClientIp");

                    //获取截图数量,并在map中删除
                    updateLiveByStreamCode(liveStatus,streamCode,pushClientIp,DateUtil.currentDatetime(),
                            LivePicCountCache.getCount(streamCode));
                    LivePicCountCache.remove(streamCode);
                    liveSysLog.setLevel(LiveSysLog.Level.INFO.value());
                    liveSysLog.setDescription("关闭直播,推流直播码:"+streamCode);
                    //添加直播截图数量
                    LivePicCountCache.getCount(streamCode);
                }
                //推流，保存直播实际开始时间
                else if("1".equals(eventType)){
                    String pushClientIp=callBack.get("pushClientIp");
//                    String eventTime="";
//                    try {
//                        eventTime=DateUtil.convertTimeStampToDate(callBack.get("eventTime")+"000");
//                    } catch (ParseException e) {
//                        eventTime=DateUtil.currentDatetime();
//                    }
                    updateLiveByStreamCode(eventType,streamCode,pushClientIp,DateUtil.currentDatetime(), 0);

                    liveSysLog.setLevel(LiveSysLog.Level.INFO.value());
                    liveSysLog.setDescription("开启直播,推流直播码:"+streamCode);
                }
                //获取截图
                else if("200".equals(eventType)){
                    String picUrl=callBack.get("picUrl");
                    updateLivePicUrl(picUrl,streamCode);
                    /*liveSysLog.setLevel(LiveSysLog.Level.INFO.value());
                    liveSysLog.setDescription("直播截图,推流直播码:"+streamCode);*/
                    //添加直播截图数量
                    LivePicCountCache.increase(streamCode);

                }
            }
        }catch (JSONException e){
            liveSysLog.setLevel(LiveSysLog.Level.ERROR.value());
            liveSysLog.setDescription("接收腾讯云回调信息异常。");
            logger.error("接收腾讯云回调信息异常。",e);
        }
        //截图回调10s一次，比较频繁，所以日志不再保存截图信息
        if(liveTencentCloud.getEvent_type()!=200) {
            liveSysLogService.addSysLog(liveSysLog);
        }
        return json.toString();
    }

    @Override
    public String getSuperviseResult(LiveTencentSupervise liveTencentSupervise, HttpServletRequest request) {
        String valid=checkLiveSuperviseIsValid(liveTencentSupervise,request);
        JSONObject result = new JSONObject();
        if("".equals(valid)) {
            /**
             * 可疑图片
             * 操作：1、禁用涉黄直播  2、保存回调记录  3、保存系统日志
             */
            int confidence = liveTencentSupervise.getConfidence();
            if (confidence > DUBIOUS_PIC) {
                String streamCode = liveTencentSupervise.getStreamId();
                setLiveStatusByApiChoose(streamCode,Constants.LIVE_BAN_PUSH);
                LiveSysLog liveSysLog = new LiveSysLog(LiveSysLog.Level.INFO.value(), "直播发现违规内容",
                        Constants.TENCENT_CLOUD_SOURCE);
                liveSysLogDao.addSysLog(liveSysLog);
                liveSuperviseDao.add(getLiveSupervise(liveTencentSupervise));
            }
            result.put("code", 0);
            valid=result.toString();
        }
        return valid;
    }

    @Override
    public Result getLiveDataById(int liveId) {
        Map<String,Object> live=new HashMap<>();
        try{
            live=getLiveById(liveId);
            if(live!=null && !live.isEmpty()){
                String streamCode=live.get("streamCode").toString();
                String endTime=live.get("endTime").toString();
                if(Constants.LIVE_LIVING_STATUS==Integer.parseInt(String.valueOf(live.get("status")))) {
                    //正在直播
                    getLivingStreamInfo(live, streamCode);
                }
                getStreamUrl(live,streamCode,endTime);
            }
        }catch (Exception e){
            logger.error("根据直播id查询直播信息异常,id="+liveId,e);
            Result errorResult=new Result();
            errorResult.setMsg("根据直播id查询直播信息异常");
            return ConvertResult.getErrorResult(errorResult);
        }
        return ConvertResult.getSuccessResult(live);
    }

    @Override
    public Result checkLiveServiceIsValid(Live live) {
        Result result=new Result();
        if(!ParamUtil.isEmpty(live.getSchoolName())){
            checkSchoolNameIsValid(live.getSchoolName(),result);
        }else{
            result.setCode(Result.Code.ParamError.value());
            result.setMsg("缺少学校名称！");
        }

//        if(!ParamUtil.isEmpty(live.getLicenseCode())){
//            checkLicenseCodeIsValid(live.getLicenseCode(),result);
//        }else if(!ParamUtil.isEmpty(live.getHitevisionAccount())){
//            checkhitevisonAccountIsValid(live.getHitevisionAccount(),result);
//        }else{
//            result.setCode(Result.Code.ParamError.value());
//            result.setMsg("缺少授权码或鸿合账号！");
//        }
        return result;
    }

    @Override
    public Result getLiveStatus(Live live) {
        JSONObject re_value=getLiveStatusByApiChoose(live.getStreamCode(),null);
        Result result=new Result();
        if(re_value!=null && !re_value.isEmpty() && TipsMessage.SUCCESS_CODE==Integer.parseInt(String.valueOf(re_value.get("code")))){
            result.setCode(TipsMessage.SUCCESS_CODE);
            JSONArray jsonArr=JSONArray.parseArray(re_value.get("liveData").toString());
            if(jsonArr!=null && !jsonArr.isEmpty()){
                JSONObject json=jsonArr.getJSONObject(0);
                result.setResult(json.get("status"));
            }else{
                result.setResult(null);
            }
        }else{
            result.setCode(TipsMessage.PASSWORD_ERROR_CODE);
            result.setResult(null);
            result.setMsg(re_value.get("errMsg").toString());
        }
        return result;
    }
    @Override
    public JSONObject setLiveStatusByApiChoose(String streamCode,String liveStatus){
        String apiChoose=CloudApiChooseUtil.API_CHOOSE;
        JSONObject cloudJson=new JSONObject();
        if(TipsMessage.LOCAL_SRS_API.equals(apiChoose)) {
            cloudJson=localSRSApi.setLiveStatus(streamCode,liveStatus);
        }else if(TipsMessage.TENCENT_CLOUD_API.equals(apiChoose)){
            cloudJson=cloudTencentApi.setLiveStatus(streamCode, liveStatus, null, null, null);
        }
        return cloudJson;
    }

    @Override
    public JSONObject getLiveStatusByApiChoose(String streamCode,String time){
        String apiChoose=CloudApiChooseUtil.API_CHOOSE;
        JSONObject cloudJson=new JSONObject();
        if(TipsMessage.LOCAL_SRS_API.equals(apiChoose)) {
            cloudJson=localSRSApi.getStatus(streamCode);
        }else if(TipsMessage.TENCENT_CLOUD_API.equals(apiChoose)){
            cloudJson=cloudTencentApi.getStatus(streamCode, time);
        }
        return cloudJson;
    }

    @Override
    public JSONObject getLiveStatatisByApiChoose(String streamCode,String type){
        String apiChoose=CloudApiChooseUtil.API_CHOOSE;
        JSONObject cloudJson=new JSONObject();
        if(TipsMessage.LOCAL_SRS_API.equals(apiChoose)) {
            cloudJson=localSRSApi.getLiveStatatis(type,streamCode);
        }else if(TipsMessage.TENCENT_CLOUD_API.equals(apiChoose)){
            cloudJson=cloudTencentApi.getLiveStatatis(type,streamCode,null,null,null);
        }
        return cloudJson;
    }

    private LiveSupervise getLiveSupervise(LiveTencentSupervise liveTencentSupervise){
        String streamCode=liveTencentSupervise.getStreamId();
        String img=liveTencentSupervise.getImg();
        String screenShotTime="";
        try {
            screenShotTime=DateUtil.convertTimeStampToDate(String.valueOf(liveTencentSupervise.getScreenshotTime())+"000");
        } catch (ParseException e) {
            screenShotTime="";
        }
        LiveSupervise liveSupervise=new LiveSupervise();
        liveSupervise.setStreamCode(streamCode);
        liveSupervise.setImg(img);
        try {
            liveSupervise.setScreenShotTime(DateUtil.parseDatetime(screenShotTime));
        } catch (ParseException e) {
            liveSupervise.setScreenShotTime(null);
        }
        try {
            String createTime=DateUtil.currentDatetime();
            liveSupervise.setCreateTime(DateUtil.parseDatetime(createTime));
        } catch (ParseException e) {
            liveSupervise.setCreateTime(null);
        }
        return liveSupervise;
    }
    private Result saveLiveData(Live live,Result re_value){
        try {
            int result = saveLive(live);
            //直播码
            String streamCode = live.getStreamCode();
            Map<String,Object> res=new HashMap<>();
            res.put("streamCode", streamCode);
            if (result > 0) {
                res.put("liveId", live.getLiveId());
                String endTime="";
                if(ParamUtil.isEmpty(live.getEndTime())){
                    Date now=new Date();
                    endTime= DateUtil.formatDate(now)+" 23:59:59";
                }else{
                    endTime=live.getEndTime();
                }
                //推流及播放地址
                getStreamUrl(res,streamCode,endTime);
            } else {
                res.put("success", false);
                res.put("liveId", "");
                res.put("streamUrl","");
            }
            re_value.setResult(res);
        }catch (JSONException e){
            logger.error("直播数据转换成json异常",e);
            return ConvertResult.getParamErrorResult("外部调用接口，直播json数据格式错误");
        }catch (Exception e){
            logger.error("保存直播数据异常",e);
            Result errorResult=new Result();
            errorResult.setMsg("外部调用接口，保存直播数据异常");
            return ConvertResult.getErrorResult(errorResult);
        }
        return re_value;
    }

    @Override
    public Map<String,Object> getStreamUrl(Map<String,Object> res,String streamCode,String endTime){
        String apiChoose=CloudApiChooseUtil.API_CHOOSE;
        if(!ParamUtil.isEmpty(apiChoose)){
            Map<String, String> streamUrl=new HashMap<>();
            if(TipsMessage.LOCAL_SRS_API.equals(apiChoose)){
                //推流及播放地址
                streamUrl=srsLiveUtil.getLiveStreamUrl(streamCode);
                //720p视频
                res.put("livePlayRtmpUrl_720", streamUrl.get("livePlayRtmpUrl_720") == null ?
                        "" : streamUrl.get("livePlayRtmpUrl_720"));
                res.put("livePlayHlsUrl_720", streamUrl.get("livePlayHlsUrl_720") == null ?
                        "" : streamUrl.get("livePlayHlsUrl_720"));
            }else if(TipsMessage.TENCENT_CLOUD_API.equals(apiChoose)){
                //推流及播放地址
                streamUrl = tencetLiveUtil.getLiveStreamUrl(streamCode, endTime);
                res.put("livePlayFlvUrl", streamUrl.get("livePlayFlvUrl") == null ? "" : streamUrl.get("livePlayFlvUrl"));
            }
            res.put("livePushUrl", streamUrl.get("livePushUrl") == null ? "" : streamUrl.get("livePushUrl"));
            res.put("livePlayRtmpUrl", streamUrl.get("livePlayRtmpUrl") == null ? "" : streamUrl.get("livePlayRtmpUrl"));
            res.put("livePlayHlsUrl", streamUrl.get("livePlayHlsUrl") == null ? "" : streamUrl.get("livePlayHlsUrl"));
        }
        return res;
    }
    /**
     * 处理推流历史记录
     * @param streamCode         直播码
     * @param actualBeginTime    实际开始时间
     * @param actualEndTime      实际结束时间
     * @return
     */
    private Map<String,Object> getLivePushHistory(String streamCode,String actualBeginTime,String actualEndTime,
                                                  Map<String,Object> livePushHistory){
        JSONObject pushLiveHistory=cloudTencentApi.
                getLivePushStatHistory(streamCode,actualBeginTime,actualEndTime);
        if(pushLiveHistory!=null && !pushLiveHistory.isEmpty() &&
                TipsMessage.SUCCESS_CODE==Integer.parseInt(String.valueOf(pushLiveHistory.get("code")))){
            String streamInfo=pushLiveHistory.get("streamInfo")==null?"":pushLiveHistory.get("streamInfo").toString();
            if(!ParamUtil.isEmpty(streamInfo)){
                JSONArray streamJsonArr=null;
                try {
                    streamJsonArr = JSONArray.parseArray(streamInfo);
                    if(streamJsonArr==null || streamJsonArr.size()==0){
                        return livePushHistory;
                    }
                }catch (JSONException e){
                    logger.error("推流历史记录转换成json异常",e);
                    return livePushHistory;
                }
                JSONObject streamInfoJson=getMaxStreamJson(streamJsonArr,"speed");
                livePushHistory.put("clientIp",streamInfoJson.get("client_ip")==null?
                        "":streamInfoJson.get("client_ip").toString());
                if(!ParamUtil.isEmpty(String.valueOf(streamInfoJson.get("fps")))){
                    if(!livePushHistory.containsKey("videoFrameRate")){
                        livePushHistory.put("videoFrameRate",0);
                    }
                    int sumFps=0;
                    int local_videoFrameRate=Integer.parseInt(String.valueOf(livePushHistory.get("videoFrameRate")));
                    int tencent_videoFrameRate=Integer.parseInt(String.valueOf(streamInfoJson.get("fps")));
                    //取两者峰值
                    if(local_videoFrameRate>=tencent_videoFrameRate){
                        sumFps=local_videoFrameRate;
                    }else{
                        sumFps=tencent_videoFrameRate;
                    }
                    livePushHistory.put("videoFrameRate",sumFps);
                }
                if(!ParamUtil.isEmpty(String.valueOf(streamInfoJson.get("speed")))){
                    if(!livePushHistory.containsKey("bitRate")){
                        livePushHistory.put("bitRate",0);
                    }
                    int sumSpeed=0;
                    int local_bitRate=Integer.parseInt(String.valueOf(livePushHistory.get("bitRate")));
                    int tencent_bitRate=Integer.parseInt(String.valueOf(streamInfoJson.get("speed")));
                    //取两者峰值
                    if(local_bitRate>=tencent_bitRate){
                        sumSpeed=local_bitRate;
                    }else{
                        sumSpeed=tencent_bitRate;
                    }
                    livePushHistory.put("bitRate",sumSpeed);
                }
            }
        }
        return livePushHistory;
    }

    /**
     * 处理播放历史记录
     * @param streamCode         直播码
     * @param actualBeginTime    实际开始时间
     * @param actualEndTime      实际结束时间
     * @return
     */
    private Map<String,Object> getLivePlayHistory(String streamCode,String actualBeginTime,String actualEndTime,
                                                  Map<String,Object> livePlayHistory){
        JSONObject playLiveHistory=cloudTencentApi.
                getLivePlayStatHistory(streamCode,actualBeginTime,actualEndTime);
        if(playLiveHistory!=null && !playLiveHistory.isEmpty() &&
                TipsMessage.SUCCESS_CODE==Integer.parseInt(String.valueOf(playLiveHistory.get("code")))){
            String statInfo=playLiveHistory.get("statInfo")==null?"":playLiveHistory.get("statInfo").toString();
            if(!ParamUtil.isEmpty(statInfo)){
                JSONArray streamJsonArr=null;
                try {
                    streamJsonArr=JSONArray.parseArray(statInfo);
                    if(streamJsonArr==null || streamJsonArr.size()==0){
                        return livePlayHistory;
                    }
                }catch (JSONException e){
                    logger.error("推流历史记录转换成json异常",e);
                }
                JSONObject statInfoJson = getMaxStreamJson(streamJsonArr,"flux");
                if(!ParamUtil.isEmpty(String.valueOf(statInfoJson.get("bandwidth")))){
                    if(!livePlayHistory.containsKey("bandWidth")){
                        livePlayHistory.put("bandWidth",0);
                    }
                    float sumBandWidth=0;
                    float local_bandWidth=Float.parseFloat(String.valueOf(livePlayHistory.get("bandWidth")));
                    float tencent_bandWidth=Float.parseFloat(String.valueOf(statInfoJson.get("bandwidth")));
                    //取两者峰值
                    if(local_bandWidth>=tencent_bandWidth){
                        sumBandWidth=local_bandWidth;
                    }else{
                        sumBandWidth=tencent_bandWidth;
                    }
                    livePlayHistory.put("bandWidth",sumBandWidth);
                }
                if(!ParamUtil.isEmpty(String.valueOf(statInfoJson.get("online")))){
                    if(!livePlayHistory.containsKey("viewersNumber")){
                        livePlayHistory.put("viewersNumber",0);
                    }
                    int sumNumber=0;
                    int local_viewersNumber= Integer.parseInt(String.valueOf(livePlayHistory.get("viewersNumber")));
                    int tencent_viewersNumber= Integer.parseInt(String.valueOf(statInfoJson.get("online")));
                    //取两者峰值
                    if(local_viewersNumber>=tencent_viewersNumber){
                        sumNumber=local_viewersNumber;
                    }else{
                        sumNumber=tencent_viewersNumber;
                    }
                    livePlayHistory.put("viewersNumber",sumNumber);
                }
                //流量取总和
                float sumTrafficValue=playLiveHistory.get("sumTrafficValue")==null?
                        0:Float.parseFloat(String.valueOf(playLiveHistory.get("sumTrafficValue")));
                if(!livePlayHistory.containsKey("trafficValue")){
                    livePlayHistory.put("trafficValue",0);
                }
                float sumTrafficValues=Float.parseFloat(String.valueOf(livePlayHistory.get("trafficValue")))+sumTrafficValue;
                livePlayHistory.put("trafficValue",sumTrafficValues);
            }
        }
        return livePlayHistory;
    }

    /**
     * 比较，获取最大值
     * @param streamJsonArr   直播流历史数据
     * @param compareKey      比较key值
     * @return
     */
    private JSONObject getMaxStreamJson(JSONArray streamJsonArr,String compareKey) {
        JSONObject maxResult=new JSONObject();
        try {
            if(streamJsonArr!=null && !streamJsonArr.isEmpty()){
                if(streamJsonArr.size()>1){
                    for (int i = 0; i < streamJsonArr.size()-1; i++) {
                        JSONObject json1 = JSONObject.parseObject(String.valueOf(streamJsonArr.get(i)));
                        JSONObject json2 = JSONObject.parseObject(String.valueOf(streamJsonArr.get(i+1)));
                        //大的数据赋值给maxResult
                        if(String.valueOf(json1.get(compareKey)).contains(".") || String.valueOf(json2.get(compareKey)).contains(".")){
                            float compareValue1=Float.parseFloat(String.valueOf(json1.get(compareKey)));
                            float compareValue2=Float.parseFloat(String.valueOf(json2.get(compareKey)));
                            if (compareValue1>= compareValue2) {
                                if(maxResult.isEmpty()){
                                    maxResult=json1;
                                }else if(Float.parseFloat(String.valueOf(maxResult.get(compareKey)))<compareValue1){
                                    maxResult=json1;
                                }
                            }else{
                                if(maxResult.isEmpty()){
                                    maxResult=json2;
                                }else if(Float.parseFloat(String.valueOf(maxResult.get(compareKey)))<compareValue2){
                                    maxResult=json2;
                                }
                            }
                        }else{
                            int compareValue1=Integer.parseInt(String.valueOf(json1.get(compareKey)));
                            int compareValue2=Integer.parseInt(String.valueOf(json2.get(compareKey)));
                            if (compareValue1>= compareValue2) {
                                if(maxResult.isEmpty()){
                                    maxResult=json1;
                                }else if(Integer.parseInt(String.valueOf(maxResult.get(compareKey)))<compareValue1){
                                    maxResult=json1;
                                }
                            }else{
                                if(maxResult.isEmpty()){
                                    maxResult=json2;
                                }else if(Integer.parseInt(String.valueOf(maxResult.get(compareKey)))<compareValue2){
                                    maxResult=json2;
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            maxResult=new JSONObject();
        }
        return maxResult;
    }
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean updateLivePicUrl(String picUrl, String streamCode){
        boolean re_value=false;
        try{
            if(!ParamUtil.isEmpty(picUrl) && !ParamUtil.isEmpty(streamCode)) {
                re_value = liveDao.updateLivePicUrl(picUrl, streamCode);
            }else{
                re_value=false;
            }
        }catch (Exception e){
            re_value=false;
            logger.error("保存直播封面异常,streamCode="+streamCode+",picUrl="+picUrl,e);
        }
        return re_value;
    }
    @Override
    public Result deleteLiveById(int liveId,int isEnable) {
        Result re_value=new Result(Result.Code.Success.value());
        try{
            Map<String,Object> live=getLiveById(liveId);
            if(live!=null && !live.isEmpty()){
                if(String.valueOf(Constants.LIVE_LIVING_STATUS).equals(live.get("status"))){
                    re_value.setMsg(Constants.LIVE_NO_DELETE);
                    re_value.setResult(false);
                }else{
                    //启用直播，判断直播数量是否达到阈值
                    if(Constants.LIVE_ENABLE==isEnable){
                        String beginTime=live.get("beginTime").toString();
                        String endTime=live.get("endTime").toString();
                        //本地直播中数量
                        int maxLiveCount=liveDao.getLiveCountByTime(beginTime,endTime);
                        //最大限制数量
                        int maxCount=maxLimitDao.getMaxCount();
                        if(maxLiveCount==maxCount){
                            re_value.setMsg(Constants.LIVE_ENABLE_DISABLE);
                            return re_value;
                        }
                    }
                    setLiveStatus(liveId,live.get("streamCode").toString(),isEnable,re_value);
                }
            }else{
                re_value.setMsg(Constants.LIVE_NO_EXITS);
                re_value.setResult(false);
            }
        }catch (Exception e){
            logger.error("根据直播id禁用/启用直播信息异常,id="+liveId,e);
            Result errorResult=new Result();
            errorResult.setMsg("禁用直播异常,直播id="+liveId);
            return ConvertResult.getErrorResult(errorResult);
        }
        return re_value;
    }

    /**
     * 修改直播状态
     * @param liveId        直播id
     * @param streamCode    直播码
     * @param isEnable      启用/禁用标识，0:启用；1:禁用
     * @param re_value      返回result
     * @return
     */
    private Result setLiveStatus(int liveId,String streamCode,int isEnable,Result re_value){
        JSONObject cloudJson=new JSONObject();
        //启用直播成功，推流
        if(Constants.LIVE_ENABLE==isEnable){
            cloudJson=setLiveStatusByApiChoose(streamCode,Constants.LIVE_PERMIT_PUSH);
        }else{
            //禁用直播
            cloudJson=setLiveStatusByApiChoose(streamCode,Constants.LIVE_BAN_PUSH);
        }
        if(cloudJson!=null && !cloudJson.isEmpty() && TipsMessage.SUCCESS_CODE==
                Integer.parseInt(String.valueOf(cloudJson.get("code")))){
            int result = liveDao.deleteLiveById(liveId, isEnable);
            if (result > 0) {
                re_value.setResult(true);
            } else {
                re_value.setResult(false);
                if ("0".equals(isEnable)) {
                    re_value.setMsg(Constants.LIVE_ENABLE_FAILED);
                } else {
                    re_value.setMsg(Constants.LIVE_DISENABLE_FAILED);
                }
            }
        }
        return re_value;
    }
    @Override
    public Result deleteLiveByLiveId(Live live) {
        Result re_value=new Result(Result.Code.Success.value());
        try{
            int liveId=live.getLiveId();
            Map<String,Object> liveData=getLiveById(liveId);
            if(liveData!=null && !liveData.isEmpty()){
                //禁用直播
                JSONObject cloudJson=setLiveStatusByApiChoose(liveData.get("streamCode").toString(),Constants.LIVE_BAN_PUSH);
                if(cloudJson!=null && !cloudJson.isEmpty() && TipsMessage.SUCCESS_CODE==
                        Integer.parseInt(String.valueOf(cloudJson.get("code")))){
                    int result=liveDao.deleteLiveById(liveId,Constants.LIVE_DISENABLE);
                    if(result>0){
                        re_value.setResult(true);
                    }else{
                        re_value.setResult(false);
                        re_value.setMsg(Constants.LIVE_DELETE_FAILED);
                    }
                }
            }else{
                re_value.setMsg(Constants.LIVE_NO_EXITS);
                re_value.setResult(false);
            }
        }catch (Exception e){
            logger.error("删除直播信息异常,id="+live.getLiveId(),e);
            Result errorResult=new Result();
            errorResult.setMsg("外部调用接口，删除直播异常,直播id="+live.getLiveId());
            return ConvertResult.getErrorResult(errorResult);
        }
        return re_value;
    }

    private Live convertLive(String apiChoose,Live live) {
        //默认状态为2，等待直播
        live.setStatus(Constants.LIVE_WAITTING_STATUS);
        String streamCode="";
        if(!ParamUtil.isEmpty(apiChoose)){
            if(TipsMessage.LOCAL_SRS_API.equals(apiChoose)){
                streamCode=srsLiveUtil.getStreamCode();
            }else if(TipsMessage.TENCENT_CLOUD_API.equals(apiChoose)){
                streamCode=tencetLiveUtil.getStreamCode();
            }
        }else{
            live.setStreamCode(null);
        }
        live.setStreamCode(streamCode);
        live.setIsDel(Constants.LIVE_IS_DEL);
        live.setType(live.getType()==null?"1":live.getType());
        try {
            live.setCreateTime(DateUtil.parseDatetime(DateUtil.currentDatetime()));
        } catch (ParseException e) {
            live.setCreateTime(null);
        }
        return live;
    }

    @Override
    public Map<String,String> revieveCallBackMsg(LiveTencentCloud liveTencentCloud){
        Map<String,String> revieveMsg=new HashMap<>();
        int eventType=liveTencentCloud.getEvent_type();
        switch (eventType){
            //断流
            case 0:
                revieveMsg=getLiveStreamMsg(liveTencentCloud);
                break;
            //推流
            case 1:
                revieveMsg=getLiveStreamMsg(liveTencentCloud);
                break;
            //截图
            case 200:
                revieveMsg=getLiveScreenPic(liveTencentCloud);
                break;
        }
        return revieveMsg;
    }

    @Override
    public Map<String,Object> getLivingStreamInfo(Map<String,Object> live,String streamCode) {
        JSONObject re_value= getLiveStatatisByApiChoose(streamCode,"all");
        if(re_value!=null && !re_value.isEmpty() &&
                TipsMessage.SUCCESS_CODE==Integer.parseInt(String.valueOf(re_value.get("code")))){
            String streamInfo=re_value.get("streamInfo").toString();
            try{
                if(!ParamUtil.isEmpty(streamInfo)){
                    JSONObject streamInfoJson=JSONObject.parseObject(JSONArray.parseArray(streamInfo).get(0).toString());
                    if(streamInfoJson!=null && !streamInfoJson.isEmpty()){
                        live.put("pushClientIp",streamInfoJson.get("client_ip")==null?"":streamInfoJson.get("client_ip").toString());
                        live.put("videoFrameRate",streamInfoJson.get("fps")==null? 0:
                                Integer.parseInt(String.valueOf(streamInfoJson.get("fps"))));
                        live.put("bitRate",streamInfoJson.get("speed")==null? 0:
                                Integer.parseInt(String.valueOf(streamInfoJson.get("speed")))/(8*1024));
                        live.put("bandWidth",streamInfoJson.get("bandwidth")==null? 0:
                                Float.parseFloat(String.valueOf(streamInfoJson.get("bandwidth"))));
                        live.put("viewersNumber",streamInfoJson.get("online")==null? 0:
                                Integer.parseInt(String.valueOf(streamInfoJson.get("online"))));
                    }
                }
            }catch (JSONException e){
                logger.error("直播信息转换成json格式异常,streamInfo="+streamInfo,e);
            }
        }else{
            live.put("pushClientIp","");
            live.put("videoFrameRate",0);
            live.put("bitRate",0);
            live.put("bandWidth",0);
            live.put("viewersNumber",0);
        }
        return live;
    }

    @Override
    public int isPermitOrderLiveByTime(String beginTime,String endTime,int maxCountLimit){
        int permit=0;
        if(!ParamUtil.isEmpty(beginTime) && !ParamUtil.isEmpty(endTime)){
            //如果预约开始时间小于当前时间，则不允许预约
            try {
                Date beginDate=DateUtil.parseDatetime(beginTime);
                Date endDate=DateUtil.parseDatetime(endTime);
                Date currentDate=new Date();
                //预约开始时间小于当前时间，返回-1
                if(beginDate.before(currentDate)){
                    permit=-1;
                    return permit;
                }if(beginDate.after(endDate)){
                    permit=-2;
                    return permit;
                }else{
                    synchronized (this) {
                        int result = liveDao.getLiveCountByTime(beginTime, endTime);
                        logger.debug("时间段 '" + beginTime + " ~ " + endTime + "' 内直播数量count=" + result +
                                ",最大限制直播数量maxCount=" + maxCountLimit);
                        //该时间段直播总数量小于最大直播限制数量，可以预约，否则不能预约直播
                        if (result < maxCountLimit) {
                            permit = 1;
                        } else {
                            permit = 2;
                        }
                    }
                }
            } catch (ParseException e) {
                permit=-3;
                logger.error("字符串转换成日期异常",e);
            }
        }
        return permit;
    }

    /**
     * 处理截图
     * @param liveTencentCloud  回调数据
     * @return
     */
    private Map<String,String> getLiveScreenPic(LiveTencentCloud liveTencentCloud) {
        Map<String,String> revieveMsg=new HashMap<>();
        String picUrl=liveTencentCloud.getPic_full_url();
        if(!ParamUtil.isEmpty(picUrl)){
            //错误码
            revieveMsg.put("code","0");
            //截图文件路径名称
            revieveMsg.put("picUrl",picUrl);
            //事件类型
            revieveMsg.put("eventType",String.valueOf(liveTencentCloud.getEvent_type()));
            //直播码
            revieveMsg.put("streamCode",liveTencentCloud.getStream_id());
        }
        return revieveMsg;
    }

    /**
     * 处理推流、断流回调数据
     * @param liveTencentCloud   回调数据
     * @return
     */
    private Map<String,String> getLiveStreamMsg(LiveTencentCloud liveTencentCloud) {
        Map<String,String> revieveMsg=new HashMap<>();
        //事件类型，0：断流 1：推流
        revieveMsg.put("eventType",String.valueOf(liveTencentCloud.getEvent_type()));
        //直播码
        revieveMsg.put("streamCode",liveTencentCloud.getStream_id());
        //用户推流ip
        revieveMsg.put("pushClientIp",liveTencentCloud.getUser_ip());
        //事件时间
        revieveMsg.put("eventTime",String.valueOf(liveTencentCloud.getEvent_time()));
        return revieveMsg;
    }

    /**
     * 获取推流历史记录的总和
     * @param livePushHistory
     * @param streamCode          直播码
     * @param beginDate           开始时间
     * @param endDate             结束时间
     * @return
     */
    private Map<String,Object> getSumLivePushHistory(Map<String,Object> livePushHistory,String streamCode,
                                                  String beginDate,String endDate){
        Calendar calendar=Calendar.getInstance();
        try {
            calendar.setTime(DateUtil.parseDatetime(beginDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.HOUR_OF_DAY,2);
        Date date=calendar.getTime();
        try {
            if(date.getTime()>=DateUtil.parseDatetime(endDate).getTime()) {
                getLivePushHistory(streamCode,beginDate,endDate,livePushHistory);
            }else{
                String endTime=DateUtil.formatDatetime(date);
                getLivePushHistory(streamCode,beginDate,endTime,livePushHistory);
                getSumLivePushHistory(livePushHistory,streamCode,endTime,endDate);
            }
        } catch (ParseException e) {
            logger.error("日期转换异常。",e);
        }
        return livePushHistory;
    }

    /**
     * 获取播放历史记录的总和
     * @param livePlayHistory
     * @param streamCode         直播码
     * @param beginDate          开始时间
     * @param endDate            结束时间
     * @return
     */
    private Map<String,Object> getSumLivePlayHistory(Map<String,Object> livePlayHistory,String streamCode,
                                                     String beginDate,String endDate){
        Calendar calendar=Calendar.getInstance();
        try {
            calendar.setTime(DateUtil.parseDatetime(beginDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.HOUR_OF_DAY,2);
        Date date=calendar.getTime();
        try {
            if(date.getTime()>=DateUtil.parseDatetime(endDate).getTime()) {
                getLivePlayHistory(streamCode,beginDate,endDate,livePlayHistory);
            }else{
                String endTime=DateUtil.formatDatetime(date);
                getLivePlayHistory(streamCode,beginDate,endTime,livePlayHistory);
                getSumLivePlayHistory(livePlayHistory,streamCode,endTime,endDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return livePlayHistory;
    }
    /**
     * 验证授权码是否合法
     * @param schoolName
     * @param result
     * @return
     */
    private Result checkSchoolNameIsValid(String schoolName,Result result){
        List<LiveLicense> licenseList = licenseDao.selectByName(schoolName);
        LiveSysLog liveSysLog=new LiveSysLog();
        liveSysLog.setLevel(LiveSysLog.Level.INFO.value());
        liveSysLog.setSource(LOG_SOURCE);
        liveSysLog.setCreateTime(new Date());
        if (null != licenseList && !licenseList.isEmpty()) {
            for(LiveLicense license:licenseList) {
                if (liveLicenseService.isValid(license) && Constants.ENABLE == license.getStatus()) {
                    result.setCode(Result.Code.Success.value());
                } else {
                    //授权已过期
                    result.setCode(TipsMessage.LICENSE_OUTDATE_CODE);
                    if (Constants.DISENABLE == license.getStatus()) {
                        result.setMsg(TipsMessage.APP_POWER_ERROR);
                    } else {
                        result.setMsg(TipsMessage.APP_LICENSE_OUTDATE);
                    }
                    logger.info("应用平台直播授权已过期，schoolName=" + schoolName);
                    liveSysLog.setDescription(TipsMessage.APP_LICENSE_OUTDATE + ",schoolName=" + schoolName);
                    liveSysLogService.addSysLog(liveSysLog);
                }
            }
        } else {
            //应用无权限
            result.setCode(TipsMessage.FAILED_CODE);
            result.setMsg(TipsMessage.APP_POWER_ERROR);
            logger.info("应用平台无直播授权，schoolName=" + schoolName);
            liveSysLog.setDescription(TipsMessage.APP_POWER_ERROR + ",schoolName=" + schoolName);
            liveSysLogService.addSysLog(liveSysLog);
        }
        return result;
    }
    /**
     * 验证授权码是否合法
     * @param licenseCode
     * @param result
     * @return
     */
    private Result checkLicenseCodeIsValid(String licenseCode,Result result){
        LiveLicense license = licenseDao.selectByLicenseCode(licenseCode);
        LiveSysLog liveSysLog=new LiveSysLog();
        if (null != license) {
            if (liveLicenseService.isValid(license) && license.getStatus()==1) {
                result.setCode(Result.Code.Success.value());
            } else {
                //授权已过期
                result.setCode(TipsMessage.LICENSE_OUTDATE_CODE);
                result.setMsg(TipsMessage.APP_LICENSE_OUTDATE);
                logger.info("应用平台直播授权已过期，licenseCode=" + licenseCode);
                liveSysLog.setDescription(TipsMessage.APP_LICENSE_OUTDATE + ",licenseCode=" + licenseCode);
            }
        } else {
            //应用无权限
            result.setCode(TipsMessage.FAILED_CODE);
            result.setMsg(TipsMessage.APP_POWER_ERROR);
            logger.info("应用平台无直播授权，licenseCode=" + licenseCode);
            liveSysLog.setDescription(TipsMessage.APP_POWER_ERROR + ",licenseCode=" + licenseCode);
        }
        liveSysLogService.addSysLog(liveSysLog);
        return result;
    }

    /**
     * TODO  验证方法待补充
     * 验证鸿合账户是否合法
     * @param hitevisonAccount
     * @param result
     * @return
     */
    private Result checkhitevisonAccountIsValid(String hitevisonAccount, Result result){
        LiveSysLog liveSysLog = new LiveSysLog();
        result.setCode(Result.Code.Success.value());
        return result;
    }

    /**
     * 获取监黄签名
     * @param reqBody       回调参数
     * @param secretKey     回调验证信息，防盗链key
     * @return
     */
    private String getTencentSignature(String reqBody,String secretKey){
        if(reqBody!=null && !"".equals(reqBody)) {
            byte[] b = HmacSha1Util.getHmacSHA1(reqBody, secretKey);
            String signature = new Base64Util().encode(b);
            return signature;
        }
        return "";
    }
    /**
     * 获取POST请求中Body参数
     * @param request
     * @return 字符串
     */
    private String getParms(HttpServletRequest request) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            ;
        }finally {
            br.close();
        }
        return sb.toString();
    }

    /**
     * 验证直播监黄合法性
     * @param request
     * @return
     */
    private String checkLiveSuperviseIsValid(LiveTencentSupervise liveTencentSupervise,HttpServletRequest request) {
        //回调请求头,防盗链id
        String tencentSecretId=request.getHeader("TPD-SecretID");
        //回调请求头,防盗链签名
        String tencentSignature=request.getHeader("TPD-CallBack-Auth");
        LiveConfig liveConfig=liveConfigDao.selectById(1);
        //生成防盗链签名
        String signature = getTencentSignature(convertSuperviseToJsonStr(liveTencentSupervise), liveConfig.getSecretKey());
        String result="";
        if (!tencentSecretId.equals(liveConfig.getSecretId()) || !tencentSignature.equals(signature)) {
            JSONObject errMsg = new JSONObject();
            //签名校验失败
            errMsg.put("code", 2);
            errMsg.put("errMsg","截图监黄签名校验失败！");
            result=errMsg.toString();
        }
        return result;
    }

    /**
     * 腾讯云回调实体转为json字符串
     * @param liveTencentSupervise
     * @return
     */
    private String convertSuperviseToJsonStr(LiveTencentSupervise liveTencentSupervise) {
        JSONObject superviseJson=new JSONObject();
        if(liveTencentSupervise!=null){
            superviseJson.put("tid",liveTencentSupervise.getTid());
            superviseJson.put("type",liveTencentSupervise.getType());
            superviseJson.put("confidence",liveTencentSupervise.getConfidence());
            superviseJson.put("normalScore",liveTencentSupervise.getNormalScore());
            superviseJson.put("hotScore",liveTencentSupervise.getHotScore());
            superviseJson.put("pornScore",liveTencentSupervise.getPornScore());
            superviseJson.put("screenshotTime",liveTencentSupervise.getScreenshotTime());
            superviseJson.put("level",liveTencentSupervise.getLevel());
            superviseJson.put("img",liveTencentSupervise.getImg());
            superviseJson.put("ocrMsg",liveTencentSupervise.getOcrMsg());
            superviseJson.put("streamId",liveTencentSupervise.getStreamId());
            if(liveTencentSupervise.getChannelId()!=null) {
                superviseJson.put("channelId", liveTencentSupervise.getChannelId());
            }
            superviseJson.put("abductionRisk",liveTencentSupervise.getAbductionRisk());
            superviseJson.put("sendTime",liveTencentSupervise.getSendTime());
        }
        return superviseJson.toString();
    }
}
