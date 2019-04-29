package com.honghe.livemanager.controller;

import com.honghe.livemanager.LiveManagerServiceApplication;
import com.honghe.livemanager.common.util.SpringUtil;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiveManagerServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
//按字母名称顺序
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LiveDistrictControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    @Test
    public void getByParentId() throws Exception {
        // ResultActions result
        ResultActions result= mockMvc.perform(get("/district/getByParentId?parentId=0")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) //打印内容
                .andExpect(status().isOk())   //判断接收到的状态是否是200
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("code").value(0));////使用jsonPath解析返回值，判断具体的内容
        assert result.andReturn().getResponse().getContentAsString().contains("北京");

    }

}