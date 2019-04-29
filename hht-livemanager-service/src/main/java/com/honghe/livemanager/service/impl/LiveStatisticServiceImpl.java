package com.honghe.livemanager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.common.pojo.model.Page;
import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.common.util.DateUtil;
import com.honghe.livemanager.common.util.ParamUtil;
import com.honghe.livemanager.common.util.TipsMessage;
import com.honghe.livemanager.dao.LiveDao;
import com.honghe.livemanager.dao.LiveHistoryDao;
import com.honghe.livemanager.entity.LiveHistory;
import com.honghe.livemanager.service.LiveStatisticService;
import com.honghe.livemanager.util.ConvertResult;
import com.honghe.livemanager.util.ExcelTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 数据统计
 *
 * @Author libing
 * @Date: 2018-09-26 14:34
 * @Mender:
 */
@Service("statisticService")
public class LiveStatisticServiceImpl implements LiveStatisticService{
    private static Logger logger = LoggerFactory.getLogger(LiveStatisticServiceImpl.class);
    @Autowired
    private LiveHistoryDao liveHistoryDao;
    @Autowired
    private LiveDao liveDao;
    @Override
    public Result getLiveLineCharts(String beginDate, String endDate, int dataType) {

        Result result = new Result();
        result.setCode(TipsMessage.FAILED_CODE);
        if(dataType<0||dataType>3){
            result.setCode(TipsMessage.PARAM_ERROR_CODE);
            result.setMsg(TipsMessage.PARAM_ERROR);
            return result;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("beginDate",beginDate);
        map.put("endDate",endDate);
        map.put("dataType",dataType);

        List<Date> dateList;
        try {
            dateList = DateUtil.getBetweenDates(DateUtil.parseDate(DateUtil.getDateAfter(beginDate,-1)), DateUtil.parseDate(DateUtil.getDateAfter(endDate,+1)));
        }catch (Exception e){
            logger.error("date format error",e);
            result.setCode(TipsMessage.INNER_ERROR_CODE);
            result.setMsg(TipsMessage.INNER_ERROR_MSG);
            return result;
        }
        List<LiveHistory> list = liveHistoryDao.selectCountByDate(map);
        String[] xData = new String[dateList.size()];
        Float[] yData = new Float[dateList.size()];
        for(int i=0 ;i<dateList.size();i++){
            xData[i] = DateUtil.formatDate(dateList.get(i));
            int flag = 0;
            for(int j=0;j<list.size();j++) {
                if (xData[i].equalsIgnoreCase(DateUtil.formatDate(list.get(j).getCreateDate()))) {
                    if (dataType == DateType.LIVECOUNT.ordinal()) {
                        yData[i] = (float) list.get(j).getLiveCount();
                    }
                    if (dataType == DateType.TRAFFICVALUE.ordinal()) {
                        yData[i] = list.get(j).getTrafficValue();
                    }
                    if (dataType == DateType.VIEWERSNUMBER.ordinal()) {
                        yData[i] = (float) list.get(j).getViewersNumber();
                    }
                    if (dataType == DateType.PICCOUNT.ordinal()) {
                        yData[i] = (float) list.get(j).getPicCount();
                    }
                    flag = 1;
                }

            }
            if(flag == 0){
                yData[i] = 0f;
            }
        }
        result.setCode(TipsMessage.SUCCESS_CODE);
        Map<String,Object> resultMap  =new HashMap<>();
        resultMap.put("xData",xData);
        resultMap.put("yData",yData);
        result.setResult(resultMap);
        return result;
    }

    @Override
    public Result getLiveStatisticList(LiveHistory liveHistory) {
        if(liveHistory==null || ParamUtil.isEmpty(liveHistory.getBeginTime()) ||
                ParamUtil.isEmpty(liveHistory.getEndTime())){
            return ConvertResult.getParamErrorResult();
        }
        String beginTime=liveHistory.getBeginTime();
        String endTime=liveHistory.getEndTime();
        JSONObject result=new JSONObject();
        try {
            List<Map<String,Object>> trafficValueList=liveDao.getTrafficValueStatisticList(beginTime,endTime);
            List<Map<String,Object>> viewersNumberList=liveDao.getViewersNumberStatisticList(beginTime,endTime);
            List<Map<String,Object>> picCountList=liveDao.getPicCountStatisticList(beginTime,endTime);
            result.put("trafficValueOrder",trafficValueList);
            result.put("viewersNumberOrder",viewersNumberList);
            result.put("screenShotPicOrder",picCountList);
            Map<String,Integer> liveStatisticCount=liveDao.getLiveStatisticCount(beginTime,endTime);
            if(liveStatisticCount!=null && !liveStatisticCount.isEmpty()){
                result.put("trafficValueCount",liveStatisticCount.get("trafficValue"));
                result.put("viewersNumberCount",liveStatisticCount.get("viewersNumber"));
                result.put("screenShotPicCount",liveStatisticCount.get("screenShotPic"));
            }else{
                result.put("trafficValueCount",0);
                result.put("viewersNumberCount",0);
                result.put("screenShotPicCount",0);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取直播统计排行异常",e);
            return ConvertResult.getErrorResult();
        }
        return ConvertResult.getSuccessResult(result);
    }

    @Override
    public Result getLiveStatisticDetails(LiveHistory liveHistory) {
        if(liveHistory==null || ParamUtil.isEmpty(liveHistory.getBeginTime()) ||
                ParamUtil.isEmpty(liveHistory.getEndTime()) || liveHistory.getCurrentPage()==0 ||
                liveHistory.getPageSize()==0 ) {
            return ConvertResult.getParamErrorResult();
        }
        String beginTime=liveHistory.getBeginTime();
        String endTime=liveHistory.getEndTime();
        String schoolName=liveHistory.getSchoolName();
        String orderType=liveHistory.getOrderType();
        String sort=liveHistory.getSort();
        int currentPage=liveHistory.getCurrentPage();
        int pageSize=liveHistory.getPageSize();
        int start=(currentPage-1) * pageSize;
        List<Map<String, Object>> liveStatisticList=new ArrayList<>();
        int totalCount=0;
        try {
            liveStatisticList = liveDao.getLiveStatisticListByPage(beginTime, endTime, schoolName,
                    orderType, sort, start, pageSize,true);
            List<Map<String, Object>> totalCountList = liveDao.getLiveStatisticListByPage(beginTime, endTime, schoolName,
                    orderType, sort, start, pageSize,false);
            if(totalCountList!=null && totalCountList.size()>0){
                totalCount=Integer.parseInt(String.valueOf(totalCountList.get(0).get("totalCount")));
            }
        }catch (Exception e){
            logger.error("获取直播统计列表异常",e);
            return ConvertResult.getErrorResult();
        }
        Page page=new Page(liveStatisticList,currentPage,pageSize,totalCount);
        return ConvertResult.getSuccessResult(page);
    }

    @Override
    public Result exportStatisticList(LiveHistory liveHistory) {
        if(liveHistory==null || ParamUtil.isEmpty(liveHistory.getBeginTime()) ||
                ParamUtil.isEmpty(liveHistory.getEndTime())){
            return ConvertResult.getParamErrorResult();
        }
        String beginTime=liveHistory.getBeginTime();
        String endTime=liveHistory.getEndTime();
        String schoolName=liveHistory.getSchoolName();
        List<Map<String, Object>> liveStatisticList=new ArrayList<>();
        try {
            liveStatisticList = liveDao.getLiveStatisticList(beginTime, endTime, schoolName,
                    null, null, 0, 0,false);
        }catch (Exception e){
            logger.error("获取直播统计列表异常",e);
            return ConvertResult.getErrorResult();
        }
        String fileName="";
        if(liveStatisticList!=null && !liveStatisticList.isEmpty()) {
            String[] headers = {"学校名称", "直播数量/次", "直播流量/G", "观看人数/人","直播截图/张"};
            Date date=new Date();
            String name="statisticDownExcel-"+date.getTime();
            Map<String,Object> params=new HashMap<>();
            params.put("beginTime",beginTime);
            params.put("endTime",endTime);
            fileName= ExcelTools.getInstance().exportExcel(name,headers,liveStatisticList,params,"liveStatistic");
        }else{
            Result result=new Result();
            result.setCode(Result.Code.Success.value());
            result.setMsg("直播统计数据为空");
            return result;
        }
        return ConvertResult.getSuccessResult(fileName);
    }


    /**
     * 类型枚举
     */
     private enum DateType{
        //直播数量
        LIVECOUNT,
        //直播流量
        TRAFFICVALUE,
        //观看人数
        VIEWERSNUMBER,
        //截图数量
        PICCOUNT
    }

}
