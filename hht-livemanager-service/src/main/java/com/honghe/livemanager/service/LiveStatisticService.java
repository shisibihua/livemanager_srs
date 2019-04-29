package com.honghe.livemanager.service;


import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.entity.LiveHistory;

/**
 * 数据统计
 *
 * @Author libing
 * @Date: 2018-09-26 14:35
 * @Mender:
 */
public interface LiveStatisticService {
    /**
     * 查询直播统计折线数据
     * @param beginTime
     * @param endTime
     * @param dateType
     * @return
     */
    public Result getLiveLineCharts(String beginTime, String endTime, int dateType);

    /**
     * 获取直播统计排行榜
     * @param liveHistory
     * @return
     */
    public Result getLiveStatisticList(LiveHistory liveHistory);

    /**
     * 获取直播统计详情
     * @param liveHistory
     * @return
     */
    Result getLiveStatisticDetails(LiveHistory liveHistory);

    /**
     * 导出直播统计列表
     * @param liveHistory
     * @return
     */
    Result exportStatisticList(LiveHistory liveHistory);
}
