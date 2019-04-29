package com.honghe.livemanager.dao;

import com.honghe.livemanager.entity.LiveHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LiveHistoryDao {
    int insert(LiveHistory record);

    int insertSelective(LiveHistory record);

    int insertBatch(List<LiveHistory> list);

    /**
     * 按日期查询
     * @param map
     * @return
     */
    List<LiveHistory> selectCountByDate(Map map);
}