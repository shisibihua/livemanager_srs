package com.honghe.livemanager.controller;


import com.honghe.livemanager.common.pojo.model.PageSearch;
import com.honghe.livemanager.common.pojo.model.Result;
import com.honghe.livemanager.common.util.ParamUtil;
import com.honghe.livemanager.entity.LiveDistrict;
import com.honghe.livemanager.entity.LiveLicense;
import com.honghe.livemanager.service.LiveDistrictService;
import com.honghe.livemanager.service.LiveLicenseService;
import com.honghe.livemanager.util.ConvertResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author libing
 * @version 1.0
 * @created 08-9月-2018 13:16:45
 * @Description 行政区controller
 */
@CrossOrigin
@RestController("districtController")
@RequestMapping("district")
public class LiveDistrictController {
	@Autowired
	LiveDistrictService districtService;

	@GetMapping("getByParentId")
	public Result getByParentId(int parentId){
		if(0>parentId){
			return ConvertResult.getParamErrorResult();
		}
		return ConvertResult.getSuccessResult(districtService.selectByParentId(parentId));

	}


}