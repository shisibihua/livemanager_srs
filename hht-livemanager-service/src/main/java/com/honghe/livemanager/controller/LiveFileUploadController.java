package com.honghe.livemanager.controller;

import com.honghe.livemanager.Resumeable.FileUpload;
import com.honghe.livemanager.service.LiveLicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 文件controller
 *
 * @Author libing
 * @Date: 2018-09-11 13:51
 * @Mender:
 * @Description:上传接入配置中的附件
 */
@CrossOrigin
@WebServlet(name = "uploadServlet", urlPatterns = {"/licenseATT/upload"})
public class LiveFileUploadController extends HttpServlet {

        @Autowired
        private LiveLicenseService licenseService;
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                new FileUpload().doGet(request,response);
        }
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
               String result = licenseService.uploadLicenseATT(request,response);
                if(null==result){
                        response.getWriter().print(false);
                }else{
                        //response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                        response.getWriter().print(result);
                }
        }
}

