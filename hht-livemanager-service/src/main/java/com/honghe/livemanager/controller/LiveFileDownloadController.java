package com.honghe.livemanager.controller;

import com.honghe.livemanager.common.util.PathUtil;
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
 * @Description:下载接入配置中的附件
 */
@CrossOrigin
@WebServlet(name = "downLoadServlet", urlPatterns = {"/licenseATT/download/*"})
public class LiveFileDownloadController extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                doPost(request,response);
        }
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                String pathInfo = request.getPathInfo();
                String fileName=request.getParameter("fileName");

//                System.out.println(">>>>>文件下载  pathInfo："+pathInfo);
                OutputStream outputStream;
                InputStream inputStream;

                try {
                        if(null!=pathInfo&&!"".equals(pathInfo)){
                                pathInfo =pathInfo.substring(1);
                        }
                        response.setContentType("application/force-download");
                        // 设置强制下载不打开 response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
                        response.addHeader("Content-Disposition", "inline;fileName=" + new String(fileName.getBytes("GBK"), "ISO8859_1"));// 设置文件名
                        //response.addHeader("Content-Disposition", "attachment;fileName=" + fileName));// 设置文件名

                        File file = new File( getFilePath(pathInfo));
                        outputStream = response.getOutputStream();
                        inputStream = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int i = -1;
                        while ((i = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, i);
                        }
                        outputStream.flush();
                        if (inputStream != null) {
                                inputStream.close();
                        }
                        if (outputStream != null) {
                                outputStream.close();
                        }
                } catch (FileNotFoundException e) {
                        System.out.println("download FileNotFoundException!"+e);
                } catch (IOException e) {
                        System.out.println("download IOException!"+e);
                }

        }

        private String getFilePath(String pathInfo){
                return  PathUtil.getPath(PathUtil.PathType.UPLOAD)+pathInfo;
        }

}

