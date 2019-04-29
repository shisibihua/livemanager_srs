package com.honghe.livemanager.service;

import com.honghe.livemanager.entity.LiveUser;

import java.util.List;

public interface LiveUserService{
    /**
     * 保存用户
     * @param user
     * @return
     */
    int addUser(LiveUser user);

    /**
     * 批量添加用户
     * @param userList
     * @return
     */
    int addBatchUser(List<LiveUser> userList);

    /**
     * 验证用户登录
     * @param loginName   用户名
     * @param loginPwd    密码
     * @return
     */
    LiveUser checkUserByName(String loginName, String loginPwd);

    /**
     * 根据用户id查询用户
     * @param userId   用户id
     * @return
     */
    LiveUser getUserById(String userId);

    /**
     * 修改用户密码
     * @param userId      用户id
     * @param loginPwd    用户密码
     * @param oldPwd      旧密码
     * @return
     */
    int updateLoginPwd(String userId, String loginPwd,String oldPwd);

    /**
     * 根据用户id启用/禁用用户
     * @param userId   用户id
     * @param status   用户状态
     * @return
     */
    int deleteUserById(String userId,String status);
}
