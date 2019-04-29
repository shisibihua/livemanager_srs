package com.honghe.livemanager.entity;

public class LiveUserType {
    private int id;
    //用户类型名称
    private String typeName;

    public LiveUserType() {
    }

    public LiveUserType(int id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
