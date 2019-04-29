package com.honghe.livemanager.controller;

import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.common.util.ParamUtil;
import com.honghe.livemanager.common.util.TipsMessage;
import com.honghe.livemanager.entity.LiveHistory;
import com.honghe.livemanager.service.LiveStatisticService;
import com.honghe.livemanager.util.ConvertResult;
import com.honghe.livemanager.util.ExcelTools;
import com.honghe.livemanager.util.MyLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计controller
 *
 * @Author libing
 * @Date: 2018-09-26 11:24
 * @Mender:
 */
@CrossOrigin
@RestController("statisticController")
@RequestMapping("statistic")
public class LiveStatisticController {

    @Autowired
    private LiveStatisticService liveStatisticService;
    @GetMapping("getLiveLineCharts")
    public Result getLiveLineCharts(String beginDate,String endDate,int dataType){
        if(ParamUtil.isOneEmpty(beginDate,endDate,dataType)){
            Result result  = new Result();
            result.setCode(TipsMessage.PARAM_ERROR_CODE);
            result.setMsg(TipsMessage.PARAM_ERROR);
         }
        return liveStatisticService.getLiveLineCharts( beginDate, endDate, dataType);
    }

    /**
     * 获取直播统计排行
     * @param liveHistory
     * @return
     */
    @RequestMapping(value="getLiveStatistic",method = RequestMethod.POST)
    public Result getLiveStatistic(@RequestBody LiveHistory liveHistory){
        return liveStatisticService.getLiveStatisticList(liveHistory);
    }

    /**
     * 获取直播排行详情
     * @param liveHistory
     * @return
     */
    @RequestMapping(value="getLiveStatisticDetails",method = RequestMethod.POST)
    public Result getLiveStatisticDetails(@RequestBody LiveHistory liveHistory){
        return liveStatisticService.getLiveStatisticDetails(liveHistory);
    }

    /**
     * 导出统计列表
     * @return
     */
    @MyLog("数据统计-导出统计列表")
    @RequestMapping(value = "exportStatisticList",method = RequestMethod.POST)
    public Result exportStatisticList(@RequestBody LiveHistory liveHistory){
        return liveStatisticService.exportStatisticList(liveHistory);
    }

    /**
     * 下载导出的统计文件
     * @param fileName   文件名
     * @param res
     * @return
     */
    @RequestMapping(value = "downLoadStatisticFile",method = RequestMethod.GET)
    public Result downLoadStatisticFile(String fileName,HttpServletResponse res){
        if(ParamUtil.isEmpty(fileName)){
            return ConvertResult.getParamErrorResult();
        }
        return ConvertResult.getSuccessResult(ExcelTools.getInstance().downLoadFile(fileName,res));
    }
}
