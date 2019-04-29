package com.honghe.livemanager.controller;

import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.common.util.ParamUtil;
import com.honghe.livemanager.entity.LiveConfig;
import com.honghe.livemanager.entity.LiveLicense;
import com.honghe.livemanager.entity.LiveMaxLimit;
import com.honghe.livemanager.service.LiveConfigService;
import com.honghe.livemanager.util.ConvertResult;
import com.honghe.livemanager.util.MyLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 直播配置管理
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:45
 */
@CrossOrigin
@RestController("configController")
@RequestMapping("config")
public class LiveConfigController {

	@Autowired
	private LiveConfigService configService;



    @MyLog("配置管理-添加接入配置")
	@PostMapping("add")
	public Result add(@RequestBody LiveConfig config){

		if(null==config||
				ParamUtil.isOneEmpty(config.getName(),config.getApiAuthenticationKey(),
						config.getAppid(),config.getPushSecretKey(),config.getBizid())){
			return ConvertResult.getParamErrorResult();
		}
		if (configService.add(config)) {
			return ConvertResult.getSuccessResult(true);
		} else {
			return ConvertResult.getSuccessResult(false);
		}
	}
    @MyLog("配置管理-更新接入配置")
	@PostMapping("update")
	public Result update(@RequestBody LiveConfig config){
		if(null==config||
				ParamUtil.isOneEmpty(config.getName(),config.getApiAuthenticationKey(),
						config.getAppid(),config.getPushSecretKey(),config.getBizid())){
			return ConvertResult.getParamErrorResult();
		}
		if (configService.update(config)) {
			return ConvertResult.getSuccessResult(true);
		} else {
			return ConvertResult.getSuccessResult(false);
		}
	}
    @MyLog("配置管理-删除接入配置")
	@PostMapping("delete")
	public Result delete(@RequestBody Map  map){
		Integer id = (Integer)map.get("id");
		if(null==id){
			return ConvertResult.getParamErrorResult();
		}
		if (configService.delete(id)) {
			return ConvertResult.getSuccessResult(true);
		} else {
			return ConvertResult.getSuccessResult(false);
		}
	}

	@GetMapping("selectById")
	public Result selectById(int id){
		if(0==id){
			return ConvertResult.getParamErrorResult();
		}
		return ConvertResult.getSuccessResult(configService.selectById(id));
	}

	/**
	 * 查询所有的
	 * @return
	 */
	@GetMapping("selectAll")
	public Result selectAll(){
		return ConvertResult.getSuccessResult(configService.selectAll());

	}


	@GetMapping("selectMaxCount")
	public Result selectMaxCount(){
		return ConvertResult.getSuccessResult(configService.getMaxCount());
	}



	/**
	 * 修改直播限制数量
	 * 
	 * @param maxlimit
	 */
    @MyLog("配置管理-修改直播数量阈值")
	@PostMapping("updateMaxCount")
	public Result updateMaxCount(@RequestBody LiveMaxLimit maxlimit){
		if(null==maxlimit||ParamUtil.isOneEmpty(maxlimit.getMaxCount())){
			return ConvertResult.getParamErrorResult();
		}
		return ConvertResult.getSuccessResult(configService.updateMaxCount(maxlimit));
	}

}