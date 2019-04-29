package com.honghe.livemanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.LiveManagerServiceApplication;
import com.honghe.livemanager.common.util.UUIDUtil;
import com.honghe.livemanager.entity.LiveConfig;
import com.honghe.livemanager.entity.LiveMaxLimit;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiveManagerServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
//按字母名称顺序
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LiveConfigControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

    }


    @Test
    public void A_add() throws Exception {
        LiveConfig liveConfig  = new LiveConfig();
        liveConfig.setId(1);
        liveConfig.setApiAuthenticationKey(UUIDUtil.getUUID());
        liveConfig.setAppid("ASDFGHJKLOISETI7");
        liveConfig.setBizid("12548758");
        liveConfig.setName("腾讯云测试一");
        liveConfig.setSecretId("setSecretId");
        liveConfig.setSecretKey("setSecretKey");
        liveConfig.setPushSecretKey(UUIDUtil.getUUID());
        // ResultActions result
        mockMvc.perform(post("/config/add")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                .content(JSON.toJSONString(liveConfig)))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容

    }
    @Test
    public void B_selectById() throws Exception {

        // ResultActions result
        ResultActions result= mockMvc.perform(get("/config/selectById?id=1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("ASDFGHJKLOISETI7");
    }


    @Test
    public void C_update() throws Exception {
        LiveConfig liveConfig  = new LiveConfig();
        liveConfig.setId(1);
        liveConfig.setApiAuthenticationKey(UUIDUtil.getUUID());
        liveConfig.setAppid("ASDFGHJKLOISETI7");
        liveConfig.setBizid("12548758");
        liveConfig.setName("腾讯云测试一修改");
        liveConfig.setPushSecretKey(UUIDUtil.getUUID());

        // ResultActions result
        mockMvc.perform(post("/config/update")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                .content(JSON.toJSONString(liveConfig)))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        // ResultActions result
        ResultActions result= mockMvc.perform(get("/config/selectById?id=1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("腾讯云测试一修改");

    }





    @Test
    public void E_selectAll() throws Exception {
        // ResultActions result
        ResultActions result= mockMvc.perform(get("/config/selectAll")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        List list = JSONArray.parseArray(JSONObject.parseObject(result.andReturn().getResponse().getContentAsString()).get("result").toString());
        assert list.size()==1;
    }

    @Test
    public void F_delete() throws Exception {
        Map map = new HashMap<>();
        map.put("id",1);
        // ResultActions result
         mockMvc.perform(post("/config/delete")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(map)))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容

        ResultActions result= mockMvc.perform(get("/config/selectById?id=1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("null");

    }

    @Test
    public void H_selectMaxCount() throws Exception {
        ResultActions result= mockMvc.perform(get("/config/selectMaxCount")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("10");
    }

    @Test
    public void I_updateMaxCount() throws Exception {
        LiveMaxLimit liveMaxLimit = new LiveMaxLimit();
        liveMaxLimit.setMaxCount(20);
        mockMvc.perform(post("/config/updateMaxCount")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(liveMaxLimit)))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容

        ResultActions result= mockMvc.perform(get("/config/selectMaxCount")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("20");

    }


}