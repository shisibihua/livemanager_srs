package com.honghe.livemanager.dao;

import com.honghe.livemanager.entity.Live;
import com.honghe.livemanager.entity.LiveHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 直播数据库操作
 * @author caoqian
 * @date 20180822
 */
public interface LiveDao {

    /**
     * 添加直播
     * @param live
     * @return
     */
    int addLive(Live live);

    /**
     * 获取正在直播的数量
     */
    int getLivingCount();
    /**
     * 获取直播
     */
    Live getEntityLiveById(@Param("liveId") int liveId);


    /**
     * 根据直播id查询直播信息
     * @param liveId  直播id
     * @return
     */
    Map<String,Object> getLiveById(@Param("liveId") int liveId);

    /**
     * 根据直播码查询直播信息
     * @param streamCode  直播码
     * @return
     */
    Map<String,Object> getLiveByStreamCode(@Param("streamCode") String streamCode);
    Live getLiveEntityByStreamCode(@Param("streamCode") String streamCode);

    /**
     * 修改直播信息
     * @param live
     * @return
     */
    int updateLive(Live live);

    /**
     * 保存直播历史记录，包括宽带、在线人数、码率、流量等
     * @param live
     * @return
     */
    boolean updateLiveHistory(@Param("live") Map<String,Object> live);


    /**
     * 根据直播码修改直播实际开始、结束时间
     * @return
     */
    int updateLiveByStreamCode(Live live);

    /**
     * 保存直播封面
     * @param picUrl       截图下载路径
     * @param streamCode     直播码
     * @return
     */
    boolean updateLivePicUrl(@Param("picUrl") String picUrl,@Param("streamCode") String streamCode);


    /**
     * 根据直播id禁用/启用直播(逻辑删除，修改直播的删除状态isDel)
     * @param liveId    直播id
     * @param isEnable  是否禁用直播,0:启用；1:禁用
     * @return
     */
    int deleteLiveById(@Param("liveId") int liveId,@Param("isEnable") int isEnable);

    /**
     * 分页获取直播列表
     * @param params
     */
    List<Map<String,Object>> getLiveListByPage(@Param("params")Map<String, Object> params);

    /**
     * 获取直播列表
     * @param params
     */
    List<Map<String,Object>> getLiveList(@Param("params")Map<String, Object> params);

    /**
     * 根据直播开始时间、结束时间获取当前时间段直播数量
     * @param beginTime   开始时间
     * @param endTime     结束时间
     * @return
     */
    int getLiveCountByTime(@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    /**
     * 根据开始日期、结束日期查询直播数量
     * @param beginTime   开始日期，格式:yyyy-MM-dd HH:mm:ss
     * @param endTime     结束日期，格式:yyyy-MM-dd HH:mm:ss
     * @return
     */
    List<Map<String,Object>> getLiveNumByDate(@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    /**
     * 获取当天全天直播列表（正在直播与待直播）
     * @param currentDate   当前日志，格式:yyyy-MM-dd
     * @return
     */
    List<Map<String,Object>> getAllLiveNumByDate(@Param("currentDate") String currentDate);

    /**
     * 查询当前日期已结束，但状态status=1或2的直播列表
     * @return
     */
    List<Live> getOverLiveList();

    /**
     * 修改直播状态
     * @param idsArr
     */
    int updateLiveStatus(@Param("idsArr")String[] idsArr);

    /**
     * 根据时间获取直播列表
     * @param beginTime
     * @param endTime
     * @return
     */
    List<Live> selectByDate(@Param("beginTime") String beginTime,@Param("endTime") String endTime);
    /**
     * 根据时间查询直播统计排行
     * 根据时间查询流量统计排行
     * @param beginTime  查询开始时间，格式:yyyy-MM-dd
     * @param endTime    查询结束时间，格式:yyyy-MM-dd
     * @return
     */
    List<Map<String,Object>> getTrafficValueStatisticList(@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    /**
     * 根据时间查询观看人数统计排行
     * @param beginTime  查询开始时间，格式:yyyy-MM-dd
     * @param endTime    查询结束时间，格式:yyyy-MM-dd
     * @return
     */
    List<Map<String,Object>> getViewersNumberStatisticList(@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    /**
     * 根据时间查询截图统计排行
     * @param beginTime  查询开始时间，格式:yyyy-MM-dd
     * @param endTime    查询结束时间，格式:yyyy-MM-dd
     * @return
     */
    List<Map<String,Object>> getPicCountStatisticList(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     * 根据时间查询直播统计数量
     * @param beginTime  查询开始时间，格式:yyyy-MM-dd
     * @param endTime    查询结束时间，格式:yyyy-MM-dd
     * @return
     */
    Map<String,Integer> getLiveStatisticCount(@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    /**
     * 分页查询直播统计列表
     * @param beginTime     开始日期:yyyy-MM-dd
     * @param endTime       结束日期:yyyy-MM-dd
     * @param schoolName    学校名称
     * @param orderType     排序类型
     * @param sort          升序asc,降序desc
     * @param start         起始页
     * @param pageSize      每页大小
     * @param pageFlag      是否分页，true:分页;false:不分页
     * @return
     */
    List<Map<String,Object>> getLiveStatisticListByPage(@Param("beginTime") String beginTime, @Param("endTime") String endTime,
                                                        @Param("schoolName") String schoolName,@Param("orderType")String orderType,
                                                        @Param("sort") String sort,@Param("start") int start,
                                                        @Param("pageSize") int pageSize,@Param("pageFlag") boolean pageFlag);
    /**
     * 查询直播统计列表
     * @param beginTime     开始日期:yyyy-MM-dd
     * @param endTime       结束日期:yyyy-MM-dd
     * @param schoolName    学校名称
     * @param orderType     排序类型
     * @param sort          升序asc,降序desc
     * @param start         起始页
     * @param pageSize      每页大小
     * @param pageFlag      是否分页，true:分页;false:不分页
     * @return
     */
    List<Map<String,Object>> getLiveStatisticList(@Param("beginTime") String beginTime, @Param("endTime") String endTime,
                                                        @Param("schoolName") String schoolName,@Param("orderType")String orderType,
                                                        @Param("sort") String sort,@Param("start") int start,
                                                        @Param("pageSize") int pageSize,@Param("pageFlag") boolean pageFlag);

    /**
     * 通过学校名称获取直播列表（仅获取未直播和直播中状态的直播）
     * @param schoolName
     * @return
     */
    List<Live> selectBySchoolName (String schoolName);

    /**
     * 批量更新直播状态
     * @param list 直播list
     * @return
     */
    int updateStatusBatch (List<Live> list);


    //测试使用 批量添加
    int insertBatch(List list);

    /**
     * 获取当天所有等待直播
     * @return
     */
    List<Live> getCurrentAllWaitingLives(String currentDate);

    /**
     * 获取当天所有已结束直播
     * @return
     */
    List<Live> getCurrentAllOverLives(String currentDate);

    /**
     * 修改录播主机推流、断流状态
     * @param controllerStatus    00：推流失败；01：推流成功;10：断流失败;11:断流成功;offline:设备离线
     * @param codeList            直播码集合
     * @return
     */
    boolean updateControllerStatus(@Param("controllerStatus") String controllerStatus,@Param("list") List<String> codeList);
}
