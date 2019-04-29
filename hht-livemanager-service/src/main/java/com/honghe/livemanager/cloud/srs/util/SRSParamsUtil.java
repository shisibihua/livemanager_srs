package com.honghe.livemanager.cloud.srs.util;

import com.honghe.livemanager.cloud.srs.dao.LiveSRSConfigDao;
import com.honghe.livemanager.cloud.srs.model.SRSConfigModel;
import com.honghe.livemanager.common.util.ConfigUtil;
import com.honghe.livemanager.common.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * SRS参数更新
 *
 * @Author caoqian
 * @Date: 2018-10-19
 * @Mender:
 */
public class SRSParamsUtil {
    private Logger logger=LoggerFactory.getLogger(SRSParamsUtil.class);
    //SRS配置读取配置文件
    private String SRS_CONFIG_APPLICATION="0";
    //SRS配置读取数据库
    private String SRS_CONFIG_DB="1";
    private String LOCALHOST_IP="localhost";

    //初始化SRS服务配置,根据配置文件设置选择SRS的ip、端口配置的读取方式
    public void init(){
        String SRS_CONFIG_CHOOSE=ConfigUtil.getInstance().getPropertyValue("srs_config_choose");
        //配置文件
        if(SRS_CONFIG_APPLICATION.equals(SRS_CONFIG_CHOOSE)){
            updateParams(ConfigUtil.getInstance());
        }else if(SRS_CONFIG_DB.equals(SRS_CONFIG_CHOOSE)){
            LiveSRSConfigDao liveSRSConfigDao = (LiveSRSConfigDao)SpringUtil.getBean("liveSRSConfigDao");
            updateParams(liveSRSConfigDao.getSrsConfig(1));
        }
    }
    public void updateParams(Object entity){
        if(entity instanceof ConfigUtil){
            ConfigUtil configUtil=(ConfigUtil) entity;
            String srs_serverIp=configUtil.getPropertyValue("srs_server_ip");
            //配置文件srs的ip是localhost时,获取本机ip，否则使用配置文件配置的ip
            if(srs_serverIp!=null && LOCALHOST_IP.equals(srs_serverIp)) {
                try {
                    SRSLiveUtil.SRS_SERVER_IP = getHostIp();
                } catch (Exception e) {
                    logger.error("初始化SRS服务ip地址异常，获取本机ip失败",e);
                    SRSLiveUtil.SRS_SERVER_IP="127.0.0.1";
                }
            }else{
                SRSLiveUtil.SRS_SERVER_IP = srs_serverIp;
            }
            SRSLiveUtil.SRS_SERVER_API_PORT=Integer.parseInt(configUtil.getPropertyValue("srs_server_api_port"));
            SRSLiveUtil.SRS_SERVER_PUSH_PORT=Integer.parseInt(configUtil.getPropertyValue("srs_server_push_port"));
            SRSLiveUtil.SRS_SERVER_PLAY_PORT=Integer.parseInt(configUtil.getPropertyValue("srs_server_play_port"));
        }else if(entity instanceof SRSConfigModel){
            SRSConfigModel srsConfigModel=(SRSConfigModel)entity;
            SRSLiveUtil.SRS_SERVER_IP=srsConfigModel.getSrsIp();
            SRSLiveUtil.SRS_SERVER_API_PORT=srsConfigModel.getApiPort();
            SRSLiveUtil.SRS_SERVER_PUSH_PORT=srsConfigModel.getPushPort();
            SRSLiveUtil.SRS_SERVER_PLAY_PORT=srsConfigModel.getPlayPort();
        }
    }

    /**
     * 获取本机真实
     * ip，windows与linux环境通用
     * Inet4Address.getLocalHost().getAddress()获取的ip在linux环境下为127.0.0.1
     * @return
     */
    private String getHostIp(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
