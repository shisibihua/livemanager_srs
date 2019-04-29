package com.honghe.livemanager.common.util;
/** *//**
 * List分页工具类
 *
 */

import java.util.ArrayList;
import java.util.List;

public class PageUtil {
    private int page = 1; // 当前页

    public int totalPages = 0; // 总页数

    private int pageSize;// 每页5条数据

    private int totalRows = 0; // 总数据数

    private int pageStartRow = 0;// 每页的起始数

    private int pageEndRow = 0; // 每页显示数据的终止数

    private boolean hasNextPage = false; // 是否有下一页

    private boolean hasPreviousPage = false; // 是否有前一页

    private List list;

    // private Iterator it;

    public PageUtil(List list, int pageSize) {
        init(list, pageSize);// 通过对象集，记录总数划分
    }

    /** *//**
     * 初始化list，并告之该list每页的记录数
     * @param list
     * @param pageSize
     */
    public void init(List list, int pageSize) {
        this.pageSize = pageSize;
        this.list = list;
        totalRows = list.size();
        // it = list.iterator();
        hasPreviousPage = false;
        if ((totalRows % this.pageSize) == 0) {
            totalPages = totalRows / this.pageSize;
        } else {
            totalPages = totalRows / this.pageSize + 1;
        }

        if (page >= totalPages) {
            hasNextPage = false;
        } else {
            hasNextPage = true;
        }

        if (totalRows < this.pageSize) {
            this.pageStartRow = 0;
            this.pageEndRow = totalRows;
        } else {
            this.pageStartRow = 0;
            this.pageEndRow = this.pageSize;
        }
    }


    // 判断要不要分页
    public boolean isNext() {
        return list.size() > 5;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public String toString(int temp) {
        String str = Integer.toString(temp);
        return str;
    }

    public void description() {

        String description = "共有数据数:" + this.getTotalRows() +

                "共有页数: " + this.getTotalPages() +

                "当前页数为:" + this.getPage() +

                " 是否有前一页: " + this.isHasPreviousPage() +

                " 是否有下一页:" + this.isHasNextPage() +

                " 开始行数:" + this.getPageStartRow() +

                " 终止行数:" + this.getPageEndRow();

        System.out.println(description);
    }

    public List getNextPage() {
        page = page + 1;

        disposePage();

//        System.out.println("用户调用的是第" + page + "页");
        this.description();
        return getObjects(page);
    }

    /** *//**
     * 处理分页
     */
    private void disposePage() {

        if (page == 0) {
            page = 1;
        }

        if ((page - 1) > 0) {
            hasPreviousPage = true;
        } else {
            hasPreviousPage = false;
        }

        if (page >= totalPages) {
            hasNextPage = false;
        } else {
            hasNextPage = true;
        }
    }

    public List getPreviousPage() {

        page = page - 1;

        if ((page - 1) > 0) {
            hasPreviousPage = true;
        } else {
            hasPreviousPage = false;
        }
        if (page >= totalPages) {
            hasNextPage = false;
        } else {
            hasNextPage = true;
        }
        this.description();
        return getObjects(page);
    }

    /** *//**
     * 获取第几页的内容
     *
     * @param page
     * @return
     */
    public List getObjects(int page) {
        if (page == 0) {
            this.setPage(1);
        }
        else {
            this.setPage(page);
        }
        this.disposePage();
        if (page * pageSize < totalRows) {// 判断是否为最后一页
            pageEndRow = page * pageSize;
            pageStartRow = pageEndRow - pageSize;
        } else {
            pageEndRow = totalRows;
            pageStartRow = pageSize * (totalPages - 1);
        }

        List objects = new ArrayList();
        if (!list.isEmpty()) {
            objects = list.subList(pageStartRow, pageEndRow);
        }
        //this.description();
        return objects;
    }
 //TODO:分页示例:
  /*
    Map<String,Object> obj = new HashMap<>();
    List<DMArea> areaList = dmAreaDao.getAreaListByKeyword(keyword);
    PageUtil areaPage = new PageUtil(areaList,pageSize);
    obj.put("currentPage",page);
    obj.put("dataList",areaPage.getObjects(page));
    obj.put("pageSize",pageSize);
    obj.put("totalCount",areaPage.getTotalRows());
    obj.put("totalPage",areaPage.getTotalPages());
  */

    public List getFistPage() {
        if (this.isNext()) {
            return list.subList(0, pageSize);
        } else {
            return list;
        }
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }


    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }


    public List getList() {
        return list;
    }


    public void setList(List list) {
        this.list = list;
    }


    public int getPage() {
        return page;
    }


    public void setPage(int page) {
        this.page = page;
    }


    public int getPageEndRow() {
        return pageEndRow;
    }


    public void setPageEndRow(int pageEndRow) {
        this.pageEndRow = pageEndRow;
    }


    public int getPageSize() {
        return pageSize;
    }


    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    public int getPageStartRow() {
        return pageStartRow;
    }


    public void setPageStartRow(int pageStartRow) {
        this.pageStartRow = pageStartRow;
    }


    public int getTotalPages() {
        return totalPages;
    }


    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }


    public int getTotalRows() {
        return totalRows;
    }


    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }


    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }


}