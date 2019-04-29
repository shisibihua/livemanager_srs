package com.honghe.livemanager.service.impl;

import com.honghe.livemanager.common.util.ParamUtil;
import com.honghe.livemanager.dao.LiveUserDao;
import com.honghe.livemanager.entity.LiveUser;
import com.honghe.livemanager.service.LiveUserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiveUserServiceImpl implements LiveUserService {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(LiveUserServiceImpl.class);
    @Autowired
    private LiveUserDao liveUserDao;
    @Override
    public int addUser(LiveUser user) {
        try{
            if(user!=null){
                return liveUserDao.addUser(user);
            }
        }catch (Exception e){
            logger.error("添加用户时异常。",e);
        }
        return 0;
    }

    @Override
    public int addBatchUser(List<LiveUser> userList) {
        try{
            if(userList!=null && !userList.isEmpty()){
                return liveUserDao.addBatchUser(userList);
            }
        }catch (Exception e){
            logger.error("批量添加用户时异常。",e);
        }
        return 0;
    }

    @Override
    public LiveUser checkUserByName(String loginName, String loginPwd) {
        try{
            if(!ParamUtil.isEmpty(loginName) && !ParamUtil.isEmpty(loginPwd)) {
                LiveUser user=liveUserDao.getUserByName(loginName);
                if(user!=null){
                    //用户密码
                    String pwd=user.getUserPwd();
                    //验证密码
                    if(pwd.equals(loginPwd)){
                        return user;
                    }
                }
            }
        }catch (Exception e){
            logger.error("根据用户名及密码查询用户异常,loginName="+loginName+",loginPwd="+loginPwd);
        }
        return null;
    }

    @Override
    public LiveUser getUserById(String userId) {
        try{
            if(!ParamUtil.isEmpty(userId)){
                return liveUserDao.getUserById(userId);
            }
        }catch (Exception e){
            logger.error("根据用户id查询用户异常，userId="+userId);
        }
        return null;
    }

    @Override
    public int updateLoginPwd(String userId, String loginPwd,String oldPwd) {
        try{
            if(!ParamUtil.isEmpty(userId) && !ParamUtil.isEmpty(loginPwd) && !ParamUtil.isEmpty(oldPwd)){
                LiveUser user=liveUserDao.getUserById(userId);
                if(user!=null && user.getUserPwd().equals(oldPwd)){
                    return liveUserDao.updateLoginPwd(userId,loginPwd);
                }else{
                    //输入的用户密码与旧密码不一致
                    return -1;
                }
            }
        }catch (Exception e){
            logger.error("修改用户密码异常,userId="+userId+",loginPwd="+loginPwd+",oldPwd="+oldPwd);
        }
        return 0;
    }

    @Override
    public int deleteUserById(String userId, String status) {
        try{
            if(!ParamUtil.isEmpty(userId) && !ParamUtil.isEmpty(status)){
                return liveUserDao.deleteUserById(userId,status);
            }
        }catch (Exception e){
            logger.error("禁用用户异常，userId="+userId+",status="+status,e);
        }
        return 0;
    }
}
