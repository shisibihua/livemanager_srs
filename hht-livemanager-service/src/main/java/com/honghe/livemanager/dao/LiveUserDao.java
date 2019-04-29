package com.honghe.livemanager.dao;

import com.honghe.livemanager.entity.LiveUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据库操作
 * @author caoqian
 * @date 2018-09-10
 */
public interface LiveUserDao {

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
     * 根据用户名及密码查询用户
     * @param loginName   用户名
     * @return
     */
    LiveUser getUserByName(@Param("loginName") String loginName);

    /**
     * 根据用户id查询用户
     * @param userId   用户id
     * @return
     */
    LiveUser getUserById(@Param("userId") String userId);

    /**
     * 修改用户密码
     * @param userId      用户id
     * @param loginPwd    用户密码
     * @return
     */
    int updateLoginPwd(@Param("userId")String userId,@Param("loginPwd") String loginPwd);

    /**
     * 根据用户id启用/禁用用户
     * @param userId   用户id
     * @param status   用户启用状态,0-用户注册未激活 1-用户正常使用 2-用户被禁用 3-用户未激活被禁用
     * @return
     */
    int deleteUserById(@Param("userId") String userId,@Param("status") String status);


}
