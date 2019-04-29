package com.honghe.livemanager.entity;

import java.util.Date;

public class LiveUser {
    private int userId;
    //用户名
    private String userName;
    //用户密码
    private String userPwd;
    //真实姓名
    private String realName;
    //性别，1-男 2-女 0-未知；
    private int gender;
    //邮箱
    private String email;
    //手机号码
    private String mobile;
    //用户激活状态：0-用户注册未激活 1-用户正常使用 2-用户被禁用 3-用户未激活被禁用
    private int status;
    //用户类型id
    private int userType;
    //创建时间
    private Date createTime;

    public LiveUser() {
    }

    public LiveUser(int userId, String userName, String userPwd,String realName,int gender,
                    String email, String mobile, int status, int userType, Date createTime) {
        this.userId = userId;
        this.userName = userName;
        this.userPwd = userPwd;
        this.realName = realName;
        this.gender = gender;
        this.email = email;
        this.mobile = mobile;
        this.status = status;
        this.userType = userType;
        this.createTime = createTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
