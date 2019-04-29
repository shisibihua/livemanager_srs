package com.honghe.livemanager.common.pojo.model;

/**
 * @Author: libing
 * @Date: 2018/8/24 16:21
 * @Description: 分页查询参数
 */
public class PageSearch {
    private int start;
    private int size;
    private String key;
    private int currentPage;
    private String orderBy;
    private String orderType;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public  enum OrderType{
        //降序
        DESC,
        //升序
        ASC;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = OrderType.ASC.name();
    }


}
