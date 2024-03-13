package com.jiawa.train.member.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//默认扫描当前包及其子孙包的所有类，扫描不到外面，加@ComponentScan
@ComponentScan("com.jiawa")
public class MemberApplication {


    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }

}
