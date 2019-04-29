package com.honghe.livemanager.service;

import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.entity.Live;
import com.honghe.livemanager.model.LiveTencentCloud;
import com.honghe.livemanager.model.LiveTencentSupervise;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 直播逻辑处理
 * @author caoqian
 * @date 20180822
 */
public interface LiveService {
    /**
     * 保存直播
     * @param live         实体
     * @return
     */
    int saveLive(Live live);


    /**
     * 获取正在直播的数量
     */
    int getLivingCount();


    /**
     * 根据直播id获取直播数据
     * @param liveId  直播id
     * @return
     */
    Map<String,Object> getLiveById(int liveId);

    /**
     * 修改直播信息
     * @param live
     * @return
     */
    int updateLive(Live live);

    /**
     * 根据直播码修改直播实际开始、结束时间及直播状态
     * 根据推流回调服务及关闭直播流调用，修改直播实际开始/结束时间及状态
     * @param eventType       0:直播关闭 1：开始直播
     * @param streamCode      直播码
     * @param pushClientIp    推流ip
     * @param eventTime       事件通知时间
     * @return
     */
    boolean updateLiveByStreamCode(String eventType,String streamCode,String pushClientIp,String eventTime,int picCount);

    /**
     * 保存直播封面
     * @param picUrl       截图下载路径
     * @param streamCode     直播码
     * @return
     */
    boolean updateLivePicUrl(String picUrl, String streamCode);

    /**
     * 内部调用
     * 禁用/启用直播（逻辑删除，只更新删除状态）
     * @param liveId    直播id
     * @param isEnable  是否禁用直播 0:启用；1:禁用
     * @return
     */
    Result deleteLiveById(int liveId,int isEnable);

    /**
     * 外部调用
     * 禁用直播（逻辑删除，只更新删除状态）
     * @param live    直播
     * @return
     */
    Result deleteLiveByLiveId(Live live);

    /**
     * 处理接收的腾讯云回调信息
     * @param liveTencentCloud   回调实体
     * @return
     */
     Map<String,String> revieveCallBackMsg(LiveTencentCloud liveTencentCloud);

    /**
     * 处理腾讯云正在直播的数据
     * @param live        腾讯云正在直播统计数据
     * @param streamCode    直播id
     * @return
     */
    Map<String,Object> getLivingStreamInfo(Map<String,Object> live,String streamCode);

    /**
     * 判断当前时间段是否可以预约直播，true:允许预约；false:不允许预约
     * @param beginTime      直播开始时间
     * @param endTime        直播结束时间
     * @param maxCountLimit  最大直播限制
     * @return
     */
    int isPermitOrderLiveByTime(String beginTime,String endTime,int maxCountLimit);

    /**
     * 获取直播历史数据总和
     * @param liveEntity          直播实体
     * @param streamCode          直播码
     * @param actualBeginTime     直播实际开始时间
     * @param actualEndTime       直播实际结束时间
     * @return
     */
    Map<String,Object> getSumLiveHistory(Map<String,Object> liveEntity,String streamCode,String actualBeginTime,String actualEndTime);

    /**
     * 根据开始日期、结束日期查询直播数量
     * @param beginTime   开始日期，格式:yyyy-MM-dd HH:mm:ss
     * @param endTime     结束日期，格式:yyyy-MM-dd HH:mm:ss
     * @return
     */
    Map<String,String> getLiveNumByDate(String beginTime, String endTime);

    /**
     * 根据当前日期获取直播数量（折线图）
     * @param currentDate  当前日期
     * @return
     */
    Map<String,Object> getLineChartLiveNum(String currentDate);

    /**
     * 分页获取直播列表
     * @param params
     * @return
     */
    List<Map<String,Object>> getLiveListByPage(Map<String, Object> params);

    /**
     * 获取直播列表
     * @param params
     * @return
     */
    List<Map<String,Object>> getLiveList(Map<String, Object> params);

    /**
     * 根据直播码查询直播
     * @param streamCode   直播码
     * @return
     */
    Map<String,Object> getLiveByStreamCode(String streamCode);

    /**
     * 返回添加直播结果
     * @param live
     * @return
     */
    Result getAddLiveResult(Live live);

    /**
     * 获取直播瞬时数据
     * @param live
     * @return
     */
    Result getLiveDescribe(Live live);

    /**
     * 获取直播历史记录
     * @param live
     * @return
     */
    Result getLiveDescribeHistory(Live live);

    /**
     * 接收腾讯云断流、推流、截图回调
     * @param liveTencentCloud
     * @return
     */
    String getCallBack(LiveTencentCloud liveTencentCloud);
    /**
     * 直播监黄
     * @param liveTencentSupervise
     * @param request
     * @return
     */
    String getSuperviseResult(LiveTencentSupervise liveTencentSupervise, HttpServletRequest request);

    /**
     * 根据直播id获取直播详情
     * @param liveId   直播id
     * @return
     */
    Result getLiveDataById(int liveId);

    /**
     * 验证对外接口，授权码或鸿合账号是否合法
     * @param live
     * @return
     */
    Result checkLiveServiceIsValid(Live live);

    /**
     * 查询直播状态
     * @param live
     * @return
     */
    Result getLiveStatus(Live live);

    /**
     * 根据api类型推流、禁用直播
     * @param streamCode   直播码
     * @param liveStatus   设置直播状态类型，0：禁用；1：推流
     * @return
     */
    JSONObject setLiveStatusByApiChoose(String streamCode, String liveStatus);

    /**
     * 根据api类型获取直播状态
     * @param streamCode  直播码
     * @param time        过期时间
     * @return
     */
    JSONObject getLiveStatusByApiChoose(String streamCode,String time);

    /**
     * 根据api类型获取直播统计数据(瞬时数据)
     * @param streamCode  直播码
     * @param type        接口查询类型，all查询所有；push查询推流；play查询播放
     * @return
     */
    JSONObject getLiveStatatisByApiChoose(String streamCode,String type);

    /**
     * 获取推流地址、播放地址
     * @param res
     * @param streamCode
     * @param endTime
     * @return
     */
    Map<String,Object> getStreamUrl(Map<String,Object> res,String streamCode,String endTime);
}
