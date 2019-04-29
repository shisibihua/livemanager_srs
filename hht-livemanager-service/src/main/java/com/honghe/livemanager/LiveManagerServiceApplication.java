package com.honghe.livemanager;

import com.honghe.livemanager.cloud.srs.util.SRSParamsUtil;
import com.honghe.livemanager.common.util.ConfigUtil;
import com.honghe.livemanager.common.util.ProcessId;
import com.honghe.livemanager.common.util.SpringUtil;
import com.honghe.livemanager.entity.LiveSysLog;
import com.honghe.livemanager.service.LiveSysLogService;
import com.honghe.livemanager.util.CloudApiChooseUtil;
import com.honghe.livemanager.util.Constants;
import com.honghe.livemanager.util.TencentParamsUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;


@SpringBootApplication(scanBasePackages = "com.honghe.livemanager")
@MapperScan({"com.honghe.livemanager.dao","com.honghe.livemanager.cloud.srs.dao"})
@ServletComponentScan
@EnableScheduling
public class LiveManagerServiceApplication {

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(LiveManagerServiceApplication.class);
    @Value("${poolSize}")
    private int poolSize;

    //最大上传单文件大小
    @Value("${spring.http.multipart.maxFileSize}")
    private  String maxFileSize;
    //最大上传多文件大小
    @Value("${spring.http.multipart.maxRequestSize}")
    private  String maxRequestSize;
    @Autowired
    private LiveSysLogService liveSysLogService;
    private static LiveSysLogService sysLogService;
    @PostConstruct
    public void init(){
        sysLogService=liveSysLogService;
    }

    public static void main(String[] args) {

        //写入PId 用于shutDown
        ProcessId.setProcessID();
        ApplicationContext app = SpringApplication.run(LiveManagerServiceApplication.class, args);
        SpringUtil.setApplicationContext(app);
        SpringUtil.scannerBeans();
        initUtil();
        System.out.println(Constants.PLATFORM_START_UP);
        logger.error(Constants.PLATFORM_START_UP);
        saveSysLog();

        String interfaceIp= ConfigUtil.getInstance().getPropertyValue("resource_ip");
    }

    /**
     * 程序启动，保存日志
     */
    private static void saveSysLog(){
        LiveSysLog liveSysLog = new LiveSysLog(LiveSysLog.Level.DEBUG.value(),Constants.PLATFORM_START_UP,
                Constants.LIVE_PLATFORM_SERVICE);
        sysLogService.addSysLog(liveSysLog);
    }


    /*@Configuration
    public class WebAppConfig extends WebMvcConfigurerAdapter {
        @Override
        public void addInterceptors(InterceptorRegistry registry){
            registry.addInterceptor(new AuthInter()).addPathPatterns("/**");
        }
    }*/
    //设置定时器
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler=new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.setThreadNamePrefix("springboot-task");
        return taskScheduler;
    }
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    /**
     * 跨域过滤器
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大
        factory.setMaxFileSize(maxFileSize); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(maxRequestSize);
        return factory.createMultipartConfig();
    }

    //初始化操作
    private static void initUtil(){
        //初始化腾讯云配置参数
        new TencentParamsUtil().init();
        //初始化SRS配置参数
        new SRSParamsUtil().init();
        //初始化api接口选择配置
        new CloudApiChooseUtil().init();
    }
}