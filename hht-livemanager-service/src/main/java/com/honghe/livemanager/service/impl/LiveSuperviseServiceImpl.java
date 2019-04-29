package com.honghe.livemanager.service.impl;

import com.honghe.livemanager.common.pojo.model.Page;
import com.honghe.livemanager.dao.LiveSuperviseDao;
import com.honghe.livemanager.entity.LiveSupervise;
import com.honghe.livemanager.service.LiveSuperviseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiveSuperviseServiceImpl implements LiveSuperviseService {
    @Autowired
    private LiveSuperviseDao liveSuperviseDao;
    @Override
    public int add(LiveSupervise liveSupervise) {
        return liveSuperviseDao.add(liveSupervise);
    }

    @Override
    public Page getLiveSuperviseListByPage(int currentPage, int pageSize) {
        int start = (currentPage - 1) * pageSize;
        int totalCount = liveSuperviseDao.getCount();
        List<LiveSupervise> liveSuperviseList = liveSuperviseDao.getLiveSuperviseListByPage(start, pageSize, true);
        if (liveSuperviseList == null || liveSuperviseList.isEmpty()) {
            liveSuperviseList = new ArrayList<>();
            LiveSupervise liveSupervise = new LiveSupervise();
            liveSuperviseList.add(liveSupervise);
        }
        return new Page(liveSuperviseList, currentPage, pageSize, totalCount);
    }

    @Override
    public List<LiveSupervise> getLiveSuperviseList() {
        List<LiveSupervise> liveSuperviseList=liveSuperviseDao.getLiveSuperviseListByPage(0,0,false);
        if(liveSuperviseList==null || liveSuperviseList.isEmpty()){
            liveSuperviseList=new ArrayList<>();
            LiveSupervise liveSupervise=new LiveSupervise();
            liveSuperviseList.add(liveSupervise);
        }
        return liveSuperviseList;
    }
    @Override
    public int getPicCount(){
        return liveSuperviseDao.getCount();
    }
}
