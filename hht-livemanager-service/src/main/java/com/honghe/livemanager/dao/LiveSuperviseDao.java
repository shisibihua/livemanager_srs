package com.honghe.livemanager.dao;

import com.honghe.livemanager.entity.LiveSupervise;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LiveSuperviseDao {
    /**
     * 保存监黄信息
     * @param liveSupervise
     * @return
     */
    int add(LiveSupervise liveSupervise);

    /**
     * 分页查询监黄信息
     * @param start     起始页
     * @param pageSize  每页大小
     * @param pageFlag  true:分页查询;false:不分页查询
     * @return
     */
    List<LiveSupervise> getLiveSuperviseListByPage(@Param("start") int start, @Param("pageSize") int pageSize,
                                                   @Param("pageFlag") boolean pageFlag);

    /**
     * 查询数据总条数
     * @return
     */
    int getCount();
}
