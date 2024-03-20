package com.jiawa.train.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
//默认扫描当前包及其子孙包的所有类，扫描不到外面，加@ComponentScan
@ComponentScan("com.jiawa")
public class GatewayApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GatewayApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        //可以打印欢迎语句，口号等，或者项目常用地址，比如接口文档，项目文档，数据库文档
        LOG.info("网关地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }

}
