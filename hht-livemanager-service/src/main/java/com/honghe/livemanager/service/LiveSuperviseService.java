package com.honghe.livemanager.service;

import com.honghe.livemanager.common.pojo.model.Page;
import com.honghe.livemanager.entity.LiveSupervise;

import java.util.List;

/**
 * 直播监黄
 * @author caoqian
 */
public interface LiveSuperviseService {
    int add(LiveSupervise liveSupervise);
    Page getLiveSuperviseListByPage(int currentPage,int pageSize);
    List<LiveSupervise> getLiveSuperviseList();
    int getPicCount();
}
