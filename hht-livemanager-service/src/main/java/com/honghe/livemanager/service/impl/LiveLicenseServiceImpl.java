package com.honghe.livemanager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.Resumeable.FileUpload;
import com.honghe.livemanager.cloud.tencent.api.CloudTencentApi;
import com.honghe.livemanager.common.pojo.model.Page;
import com.honghe.livemanager.common.util.*;
import com.honghe.livemanager.dao.*;
import com.honghe.livemanager.entity.Live;
import com.honghe.livemanager.entity.LiveLicense;
import com.honghe.livemanager.entity.LiveLicenseATT;
import com.honghe.livemanager.entity.LiveSysLog;
import com.honghe.livemanager.service.LiveLicenseService;
import com.honghe.livemanager.service.LiveService;
import com.honghe.livemanager.service.LiveSysLogService;
import com.honghe.livemanager.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:45
 */
@Service("licenseService")
public class LiveLicenseServiceImpl implements LiveLicenseService {

    private static Logger logger = LoggerFactory.getLogger(LiveLicenseServiceImpl.class);


    @Autowired
    LiveLicenseDao licenseDao;
    @Autowired
    LiveLicenseATTDao licenseATTDao;
    @Autowired
    LiveDistrictDao districtDao;
    @Autowired
    LiveDao liveDao;
    @Autowired
    private LiveMaxLimitDao maxLimitDao;
    @Autowired
    private LiveSysLogDao liveSysLogDao;
    @Autowired
    LiveService liveService;
    @Autowired
    private CloudTencentApi cloudTencentApi;
    @Autowired
    private LiveSysLogService liveSysLogService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(LiveLicense license) {

        if(addCheckName(license)){
            license.setStatus(1);
            license.setLicenseCode(UUIDUtil.getUUID());
            licenseDao.add(license);
            insertATTBatch(license);
            return true;
        }
        return false;
    }

    @Override
    public String uploadLicenseATT(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String fileAutoName = new FileUpload().doPost(request, response, PathUtil.PathType.UPLOAD);
        if (null == fileAutoName)
            return null;
//		Map map = new HashMap();
//		map.put("path",request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/licenseATT/download/"+fileAutoName);
//		map.put("FileAutoName",fileAutoName);
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/licenseATT/download/" + fileAutoName;
//		if(null==fileAutoName)
//			return false;
//		LiveLicenseATT licenseATT = new LiveLicenseATT();
//		if(null==request.getParameter("licenseId")){
//			System.out.println("licenseId is null");
//			licenseATT.setLicenseId(1);
//			//TODO 正式使用时打开
//			//return  false;
//		}else{
//			licenseATT.setLicenseId(Integer.parseInt(request.getParameter("licenseId")));
//		}
//		licenseATT.setName(String.valueOf(request.getParameter("resumableFilename")));
//		licenseATT.setPath(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/licenseATT/download/"+fileAutoName);
//		try {
//			return licenseATTDao.add(licenseATT) > 0;
//		}catch (Exception e){
//			logger.error("licenseATTDao.add false.");
//			//上传失败，删除文件
//			File file = new File(PathUtil.getPath(PathUtil.PathType.UPLOAD)+"/"+fileAutoName);
//			file.delete();
//			return  false;
//		}
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(int id) {
        boolean flag =false;
        try {
            LiveLicense license = licenseDao.selectById(id);
            license.setStatus(0);
            //禁用直播
            if(toggleStatus(license)){
                flag = licenseDao.deleteById(id) > 0;
                List<LiveLicenseATT> list = licenseATTDao.selectByLicenseId(id);
                for(LiveLicenseATT att:list){

                    deleteLicenseATTById(att.getId(),
                            att.getPath().split("/")[att.getPath().split("/").length-1]);
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            flag = false;
            logger.error("license delete error.",e);
        }
        return flag;
    }

    /**
     * 名称校验
     * @param license
     * @return
     */
    @Override
    public boolean checkName(LiveLicense license) {
        //添加校验
        if(license.getId()==null){
            return addCheckName(license);
            //修改校验
        }else{
            return updateCheckName(license);

        }
    }

    /**
     * 禁用/启用 学校授权
     * 0 禁用, 1 启用。 默认启用
     * @param license
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleStatus(LiveLicense license) {
        /**
         * 1.根据学校名称查询所有直播未开始或已开始直播
         * 2.发送启用/禁用直播请求。
         * 3.更新数据库
         * 4.保存系统日志
         */
        LiveSysLog liveSysLog=new LiveSysLog();
        liveSysLog.setLevel(LiveSysLog.Level.INFO.value());
        liveSysLog.setCreateTime(new Date());
        liveSysLog.setSource(Constants.LIVE_LICENSE);
        Map<String,Object> resultMap=new HashMap<>();
        if (license!=null){
            String licenseName=license.getName();
            int status=license.getStatus();
            List<Live> liveAllList=liveDao.selectBySchoolName(licenseName);
            if(liveAllList!=null && !liveAllList.isEmpty()) {
                //禁用学校授权，禁用直播
                if (Constants.DISENABLE == status) {
                    resultMap=returnBanLive(license.getName(),liveAllList);
                }
                //开启学校授权，启用直播
                else if(Constants.ENABLE==status){
                    //最大限制数量
                    int maxCount = maxLimitDao.getMaxCount();
                    LiveLicense liveLicense=licenseDao.selectById(license.getId());
                    //判断授权是否在有效期
                    if(isValid(liveLicense)){
                        resultMap=returnPermitLive(maxCount,license.getName(),liveAllList);
                    }else{
                        //授权已过期
                        liveSysLog.setDescription(TipsMessage.APP_LICENSE_OUTDATE +",licenseName=" + license.getName());
                        liveSysLogService.addSysLog(liveSysLog);
                        return false;
                    }
                }
            }else{
                //修改授权状态
                return licenseDao.update(license)>0;
            }
        }else{
            liveSysLog.setDescription(TipsMessage.APP_POWER_ERROR + ",licenseName=" +  license.getName());
            liveSysLogService.addSysLog(liveSysLog);
            return false;
        }
        //保存系统日志
        List<LiveSysLog> liveSysLogList=(List<LiveSysLog>)resultMap.get("liveSysLogList");
        if(liveSysLogList!=null && !liveSysLogList.isEmpty()) {
            liveSysLogDao.addSysLogBatch(liveSysLogList);
        }
        //修改禁用的直播
        if(Constants.DISENABLE==license.getStatus()) {
            List<Live> liveList=(List<Live>)resultMap.get("liveList");
            if (liveList!=null && !liveList.isEmpty()) {
                try {
                    int result = liveDao.updateStatusBatch(liveList);
                    if (result > 0) {
                        //修改授权状态
                        return licenseDao.update(license) > 0;
                    } else {
                        return false;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        //启用失败的直播列表
        if(Constants.ENABLE==license.getStatus()){
            List<Live> updateFailedLiveList=(List<Live>)resultMap.get("updateFailedLiveList");
            if(updateFailedLiveList!=null && updateFailedLiveList.isEmpty()){
                //修改授权状态
                return licenseDao.update(license)>0;
            }else{
                return false;
            }
        }
        return false;
    }

    @Override
    public LiveLicense selectById(int id) {
        LiveLicense liveLicense = licenseDao.selectById(id);
        List<LiveLicenseATT> list;
        if (null == liveLicense) {
            return null;
        } else {
            liveLicense.setAddress(getAddress(liveLicense));
            list = licenseATTDao.selectByLicenseId(liveLicense.getId());
            liveLicense.setLiveLicenseATTs(list);
        }
        return liveLicense;
    }

    @Override
    public Page selectByPage(int page,int pageSize,String key,String beginTime,String endTime) {
        page = page<1 ? 1 : page;
        pageSize =  pageSize<10 ? 10 : pageSize;
        Map map  =new HashMap();
        map.put("pageSize",pageSize);
        map.put("start", (page - 1) * pageSize);
        map.put("key", null==key||"".equals(key)?null:key);
        map.put("beginTime",null==beginTime||"".equals(beginTime)?null:beginTime);
        map.put("endTime",null==endTime||"".equals(endTime)?null:endTime);

        List<LiveLicense> list  =licenseDao.selectByPage(map);
        if(null!=list&&list.size()>0){
            for(int i = 0;i<list.size();i++){
                list.get(i).setAddress(getAddress(list.get(i)));
                list.get(i).setLiveLicenseATTs(licenseATTDao.selectByLicenseId(list.get(i).getId()));
            }
        }
        return new Page(list, page,
                pageSize, licenseDao.countSelectByPage(map));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(LiveLicense license) {
        if(updateCheckName(license)){
            license.setLicenseCode(UUIDUtil.getUUID());
            licenseATTDao.deleteByLicenseId(license.getId());
            insertATTBatch(license);
            return licenseDao.update(license) > 0;
        }
        return false;

    }

    @Override
    public boolean deleteLicenseATTById(int attId, String fileAutoName) {

        File file = new File(PathUtil.getPath(PathUtil.PathType.UPLOAD) + "/" + fileAutoName);
        if (!file.exists()) {
//            System.out.println("file is not exists.fileAutoName:"+fileAutoName);
            return false;
        }
        file.delete();
        //如果该附件没有被整体保存时，只传文件名称即可进行删除。
        if(attId!=0){
            return licenseATTDao.deleteById(attId) > 0;
        }else{
            return true;
        }
    }

    @Override
    public boolean isValid(LiveLicense license){
        //验证授权是否在期限内
        String beginTime=license.getBeginTime();
        String endTime=license.getEndTime();
        Date currentDate=new Date();
        try {
            Date beginDate= DateUtil.parseDatetime(beginTime);
            Date endDate=DateUtil.parseDatetime(endTime);
            if(currentDate.after(beginDate) && currentDate.before(endDate)){
                return true;
            }else{
                return false;
            }
        } catch (ParseException e) {
            logger.error("时间转换异常",e);
        }
        return false;
    }

    @Override
    public LiveLicense getLiveLicenseById(int licenseId) {
        return licenseDao.selectById(licenseId);
    }

    /****************************************私有方法***************************************/

    /**
     * 拼地址
     * @param liveLicense
     * @return
     */
    private String getAddress(LiveLicense liveLicense){
        StringBuffer address = new StringBuffer();

        String districtName = districtDao.selectById(liveLicense.getProvinceId()).getName();

        address.append(districtName).append(",");
        districtName = districtDao.selectById(liveLicense.getCityId()).getName();
        address.append(districtName).append(",");

        districtName = districtDao.selectById(liveLicense.getCountyId()).getName();
        address.append(districtName);
        liveLicense.setAddress(address.toString());
        return address.toString();

    }

    /**
     * 批量插入附件
     * @param license
     */
    private void insertATTBatch(LiveLicense license){
        int licenseId = license.getId();
        List<LiveLicenseATT> list = license.getLiveLicenseATTs();

        if (null != list&&list.size()!=0) {
            for (LiveLicenseATT aList : list) {
                aList.setLicenseId(licenseId);
            }
            licenseATTDao.addBatch(list);
        }

    }

    /**
     * 添加校验重名
     * @param liveLicense
     * @return
     */
    private boolean addCheckName(LiveLicense liveLicense){
        List<LiveLicense> list = licenseDao.selectByName(liveLicense.getName());
        return null==list||list.size()==0;
    }

    /**
     * 修改校验重名
     * @param liveLicense
     * @return
     */
    private boolean updateCheckName(LiveLicense liveLicense) {
        //重名校验
        List<LiveLicense> list = licenseDao.selectByName(liveLicense.getName());
        //空 或者只有一个数据，且这条数据是修改的这条数据。
        return null == list || (list.size() == 1 && list.get(0).getId() == (int) liveLicense.getId());
    }

    /**
     * 获取可禁用直播
     * @param licenseName     学校名称
     * @param liveAllList     学校预约的直播
     * @return
     */
    private Map<String,Object> returnBanLive(String licenseName,List<Live> liveAllList){
        List<LiveSysLog> liveSysLogList=new ArrayList<>();
        List<Live> liveList=new ArrayList<>();
        for (Live live : liveAllList){
            JSONObject tencentCloudJson=cloudTencentApi.setLiveStatus(live.getStreamCode(),
                    Constants.LIVE_BAN_PUSH, null, null, null);
            if (tencentCloudJson != null && !tencentCloudJson.isEmpty() &&
                    TipsMessage.SUCCESS_CODE == Integer.parseInt(String.valueOf(tencentCloudJson.get("code")))) {
                String description="禁用学校授权,licenseName=" +  licenseName+",streamCode="+live.getStreamCode();
                LiveSysLog log=new LiveSysLog(LiveSysLog.Level.INFO.value(),description,Constants.LIVE_LICENSE);
                liveSysLogList.add(log);
                liveList.add(live);
            }
        }
        Map<String,Object> map=new HashMap<>();
        map.put("liveList",liveList);
        map.put("liveSysLogList",liveSysLogList);
        return map;
    }

    /**
     * 获取可开启的直播
     * @param maxCount                直播数量限制阈值
     * @param licenseName             学校名称
     * @param liveAllList             学校预约的直播
     * @return
     */
    private Map<String,Object> returnPermitLive(int maxCount,String licenseName,List<Live> liveAllList){
        List<LiveSysLog> liveSysLogList=new ArrayList<>();
        List<Live> updateFailedLiveList=new ArrayList<>();
        //判断直播数量是否超过直播限制阈值
        for(Live live:liveAllList) {
            String beginTime = live.getBeginTime();
            String endTime = live.getEndTime();
            synchronized (this) {
                //本地直播中数量
                int maxLiveCount = liveDao.getLiveCountByTime(beginTime, endTime);
                /**
                 * 学校能允许开启的直播，每次只能修改了数据库才能准确获取当前直播数量，此处不适合批量处理
                 * 学校开启授权，因为直播数量限制，所以有可能不能开启所有直播
                 */
                if (maxLiveCount < maxCount) {
                    JSONObject tencentCloudJson = cloudTencentApi.setLiveStatus(live.getStreamCode(),
                            Constants.LIVE_PERMIT_PUSH, null, null, null);
                    if (tencentCloudJson != null && !tencentCloudJson.isEmpty() &&
                            TipsMessage.SUCCESS_CODE == Integer.parseInt(String.valueOf(tencentCloudJson.get("code")))) {
                        String description = "开启学校授权,licenseName=" + licenseName + ",streamCode=" + live.getStreamCode();
                        LiveSysLog log = new LiveSysLog(LiveSysLog.Level.INFO.value(), description, Constants.LIVE_LICENSE);
                        liveSysLogList.add(log);
                        //直播与当前状态相反，所以放相反的值。
                        int result = liveDao.deleteLiveById(live.getLiveId(), Constants.DISENABLE);
                        //修改失败的直播id集合
                        if (result == 0) {
                            updateFailedLiveList.add(live);
                        }
                    }
                }
            }
        }
        Map<String,Object> map=new HashMap<>();
        map.put("liveSysLogList",liveSysLogList);
        map.put("updateFailedLiveList",updateFailedLiveList);
        return map;
    }
}