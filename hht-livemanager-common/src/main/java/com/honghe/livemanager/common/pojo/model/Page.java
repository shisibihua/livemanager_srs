package com.honghe.livemanager.common.pojo.model;

import java.util.List;

/**
 * 分页对象
 *
 * @auther Libing
 * @Time 2017/9/8 10:54
 */
public class Page<E> {
    private List<E>     dataList ;     // 存放实体类集合
    private int        currentPage ;   // 当前页
    private int        pageSize ;      // 每页显示的条数
    private int        totalPage ;     // 总页数
    private int        totalCount ;    // 总条数

    public Page(List<E> dataList,int currentPage,int pageSize,int totalCount){
        this.dataList = dataList;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        int totalPage = totalCount / pageSize;
        if(totalCount % pageSize > 0){
            totalPage +=1;
        }
        this.totalPage = totalPage;
        this.totalCount = totalCount;
    }

    public List<E> getDataList() {
        return dataList;
    }

    public void setDataList(List<E> dataList) {
        this.dataList = dataList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
