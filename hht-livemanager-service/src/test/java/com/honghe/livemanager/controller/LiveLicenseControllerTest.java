package com.honghe.livemanager.controller;

import com.alibaba.fastjson.JSON;
import com.honghe.livemanager.LiveManagerServiceApplication;
import com.honghe.livemanager.common.util.SpringUtil;
import com.honghe.livemanager.dao.LiveLicenseDao;
import com.honghe.livemanager.dao.LiveSysLogDao;
import com.honghe.livemanager.entity.LiveLicense;
import com.honghe.livemanager.entity.LiveLicenseATT;
import com.honghe.livemanager.entity.LiveSysLog;
import com.honghe.livemanager.service.LiveLicenseService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiveManagerServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
//按字母名称顺序
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LiveLicenseControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void A_add() throws Exception {
        LiveLicense license = new LiveLicense();
        license.setName("河北省第一中学");
        license.setBeginTime("2018-09-11 19:10:00");
        license.setEndTime("2019-01-11 19:10:00");
        license.setContact("张三");
        license.setContactNumber("13600005555");
        license.setId(1);
        license.setProvinceId(110000);
        license.setCityId(110000);
        license.setCountyId(110105);
        List<LiveLicenseATT> list = new ArrayList<>();
        LiveLicenseATT licenseATT  = new LiveLicenseATT();
        licenseATT.setPath("http://192.168.17.33:8013/licenseATT/download/0c74fb45-6541-4350-a33e-491fba2bb19b.sql");
        licenseATT.setName("测试.sql");
        list.add(licenseATT);
        //license.setLiveLicenseATTs(list);
        System.out.println(">>>>>>>>>>>>"+JSON.toJSONString(license));
        // ResultActions result
        mockMvc.perform(post("/license/add")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                .content(JSON.toJSONString(license)))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容

    }

    @Test
    public void B_selectById() throws Exception {
        // ResultActions result
        ResultActions result= mockMvc.perform(get("/license/selectById?id=1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("河北省第一中学");
        assert result.andReturn().getResponse().getContentAsString().contains("测试.sql");
        assert result.andReturn().getResponse().getContentAsString().contains("朝阳区");

    }
    @Test
    public void C_toggleStatus() throws Exception {
        LiveLicense license = new LiveLicense();
        license.setName("河北省第一中学修改");
        license.setBeginTime("2018-09-11 19:10:00");
        license.setEndTime("2019-01-11 19:10:00");
        license.setContact("张三");
        license.setId(1);
        license.setContactNumber("13600005555");
        license.setStatus(0);
        license.setProvinceId(110000);
        license.setCityId(110000);
        license.setCountyId(110102);
        System.out.println(JSON.toJSONString(license));
        // ResultActions result
        mockMvc.perform(post("/license/toggleStatus")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                .content(JSON.toJSONString(license)))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        // ResultActions result
        ResultActions result= mockMvc.perform(get("/license/selectById?id=1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("河北省第一中学修改");
        assert result.andReturn().getResponse().getContentAsString().contains("西城区");


    }

    @Test
    public void C_checkName() throws Exception {
        String params = "id=&name=河北省第一中学修改";

        mockMvc.perform(get("/license/checkName?"+params)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("result").exists())
                .andExpect(jsonPath("result").value(false));////使用jsonPath解析返回值，判断具体的内容


    }
    @Test
    public void C_update() throws Exception {
        LiveLicense license = new LiveLicense();
        license.setName("河北省第一中学修改");
        license.setBeginTime("2018-09-11 19:10:00");
        license.setEndTime("2019-01-11 19:10:00");
        license.setContact("张三");
        license.setId(1);
        license.setContactNumber("13600005555");
        license.setStatus(1);
        license.setProvinceId(110000);
        license.setCityId(110000);
        license.setCountyId(110102);
        System.out.println(JSON.toJSONString(license));
        // ResultActions result
        mockMvc.perform(post("/license/update")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                .content(JSON.toJSONString(license)))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        // ResultActions result
        ResultActions result= mockMvc.perform(get("/license/selectById?id=1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("河北省第一中学修改");
        assert result.andReturn().getResponse().getContentAsString().contains("西城区");


    }
    @Test
    public void D_selectByPage() throws Exception {

        String params = "page=1&pageSize=10&key=&beginTime=&endTime=2019-01-11 20:31:00";


        // ResultActions result
        ResultActions result= mockMvc.perform(get("/license/selectByPage?"+params)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("河北省第一中学");

    }

    @Test
    public void E_delete() throws Exception {
        Map map = new HashMap<>();
        map.put("id",2);
        // ResultActions result
        mockMvc.perform(post("/license/delete")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(map)))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容

        ResultActions result= mockMvc.perform(get("/license/selectById?id=1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("null");

    }




    @Test
    public void F_deleteLicenseAttById() throws Exception {
        Map map = new HashMap<>();
        map.put("id",3);
        map.put("path","http://192.168.17.33:8013/licenseATT/download/0c74fb45-6541-4350-a33e-491fba2bb19b.sql");
        // ResultActions result
        mockMvc.perform(post("/license/deleteLicenseAttById")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(map)))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容

        ResultActions result= mockMvc.perform(get("/license/selectById?id=1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("\"liveLicenseATTs\":[]");

    }

    @Autowired
    private LiveLicenseDao licenseDao;
    @Autowired
    private LiveLicenseService licenseService;
    @Autowired
    private LiveSysLogDao liveSysLogDao;
    /**
     * 查詢授權信息
     * @throws Exception
     */
    @Test
    public void F_selectByEndDate() throws Exception {
        //处理过期授权
        List<LiveLicense> list =licenseDao.selectByEndDate();
        LiveSysLog sysLog ;
        for(LiveLicense license:list ) {
            license.setStatus(0);
            //失败的存入数据库
            if(!licenseService.toggleStatus(license)){
                sysLog = new LiveSysLog();
                sysLog.setCreateTime(new Date());
                sysLog.setLevel("ERROR");
                sysLog.setDescription("授权到期，禁用授权失败，请手动禁用！学校名称为："+license.getName());
                sysLog.setSource("直播服务");
                liveSysLogDao.addSysLog(sysLog);
            }
        }

    }




}