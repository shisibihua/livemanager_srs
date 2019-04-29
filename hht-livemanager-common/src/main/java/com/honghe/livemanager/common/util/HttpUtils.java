package com.honghe.livemanager.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.honghe.livemanager.common.pojo.model.Result;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;

/**
 * 发送http请求工具类
 *
 * @auther yuk
 * @create 2017-10-11 13:55
 */
public class HttpUtils {

    static org.slf4j.Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private final static int CONNET_TIME=6000;
    private final static int REQUEST_TIME=6000;

    public static JSONObject httpPost(String url, JSONObject jsonParam) {
        //post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        try {
            List<NameValuePair> paramList = new ArrayList<>();
            for (JSONObject.Entry<String, Object> entry : jsonParam.entrySet()) {
                paramList.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }
            // 模拟表单
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/x-www-form-urlencoded");
            method.setEntity(entity);
            method.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    str = EntityUtils.toString(result.getEntity());
                    /**把json字符串转换成json对象**/

                    jsonResult = JSONObject.parseObject(str);
                    if (jsonResult == null) {
                        jsonResult = new JSONObject();
                        jsonResult.put("result", str);
                    }
                } catch (JSONException e){
                    jsonResult = new JSONObject();
                    jsonResult.put("result", str);
                }catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return jsonResult;
    }

    public static JSONObject httpPostJson(String url, JSONObject jsonParam) {
        //post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        try {
            StringEntity entity = new StringEntity(jsonParam.toJSONString(),"UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            method.setEntity(entity);
            HttpResponse response = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    str = EntityUtils.toString(response.getEntity());
                    /**把json字符串转换成json对象**/
                    jsonResult = JSON.parseObject(str);
                } catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return jsonResult;
    }

    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
    public static JSONObject httpGet(String url) {
        //get请求返回结果
        JSONObject jsonResult =new JSONObject();
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            //发送get请求
            HttpGet request = new HttpGet(url);
            request.addHeader("Accept", "text/json");
            request.addHeader("charset", "UTF-8");
            //连接超时和请求超时时间设置
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000)
                    .setConnectionRequestTimeout(60000)
                    .setSocketTimeout(60000).build();
            request.setConfig(requestConfig);
            HttpResponse response = client.execute(request);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
//                String strResult = EntityUtils.toString(response.getEntity(),"GBK");
                String strResult = EntityUtils.toString(response.getEntity(),"UTF-8");
                /**把json字符串转换成json对象**/
                if(strResult!=null && !"".equals(strResult)){
                    try {
                        jsonResult = JSONObject.parseObject(strResult);
                    }catch (JSONException e){
                        logger.error("字符串转换json异常,jsonStr="+strResult);
                        jsonResult=new JSONObject();
                    }
                }
            } else {
                url = URLDecoder.decode(url, "UTF-8");
                logger.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
            jsonResult=new JSONObject();
        }
        return jsonResult;
    }

    /**
     * delete请求
     * @param url 请求地址
     * @param parameter 参数
     * @return
     * @throws Exception
     */
    public static String httpDelete (String url, String parameter) throws Exception{
        StringBuffer buffer = new StringBuffer();
        URLConnection http_url_connection = null;
        BufferedOutputStream output_stream=null;
        InputStreamReader input_stream_reader=null;
        BufferedReader buffered_reader=null;
        try {
            http_url_connection = (new URL(url)).openConnection();
            //将urlconnection类强转为httpurlconnection类
            HttpURLConnection HttpURLConnection = (HttpURLConnection)http_url_connection;

            HttpURLConnection.setDoInput(true);
            HttpURLConnection.setDoOutput(true);

            //设置请求方式。可以是delete put post get
            HttpURLConnection.setRequestMethod("DELETE");
            //设置内容的长度
            HttpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameter.length()));
            //设置编码格式
            HttpURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            //设置接收返回参数格式
            HttpURLConnection.setRequestProperty("accept", "application/json");
            //设置读取超时时间
            HttpURLConnection.setReadTimeout(10000);
            //设置连接超时时间
            HttpURLConnection.setConnectTimeout(10000);

            HttpURLConnection.setUseCaches(false);

            output_stream = new BufferedOutputStream(HttpURLConnection.getOutputStream());
            output_stream.write(parameter.getBytes());
            output_stream.flush();

            input_stream_reader = new InputStreamReader(HttpURLConnection.getInputStream(), "utf-8");
            buffered_reader = new BufferedReader(input_stream_reader);
            buffer = new StringBuffer();
            String line;
            while ((line = buffered_reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(output_stream!=null){
                output_stream.close();
            }
            if(input_stream_reader!=null) {
                input_stream_reader.close();
            }
            if(buffered_reader!=null) {
                buffered_reader.close();
            }
        }

        return buffer.toString();
    }
    public static JSONObject httpPostOutTimeAndRetry(String url, String param,int retryCount) {
        Result result = new Result();
        //post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = new JSONObject();
        HttpPost method = new HttpPost(url);
        try {
            // 请求重试处理
            HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
                @Override
                public boolean retryRequest(IOException arg0, int retryTimes, HttpContext arg2) {
                    if (retryTimes > retryCount) {
                        return false;
                    }
                    HttpClientContext clientContext = HttpClientContext.adapt(arg2);
                    HttpRequest request = clientContext.getRequest();
                    boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                    if (idempotent) {
                        // 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
                        return true;
                    }
                    return false;
                }
            };
            StringEntity entity = new StringEntity(param,"UTF-8");
            entity.setContentEncoding("UTF-8");
            method.setEntity(entity);
            httpClient = HttpClients.custom().setRetryHandler(handler).build();
            HttpResponse response = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    str = EntityUtils.toString(response.getEntity());
                    /**把json字符串转换成json对象**/
                    jsonResult = JSON.parseObject(str);

                } catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                    result.setCode(Result.Code.UnKnowError.value());
                    result.setMsg("UnKnowError");
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
            result.setCode(Result.Code.UnKnowError.value());
            result.setMsg("UnKnowError");
        }
        return jsonResult;
    }
}