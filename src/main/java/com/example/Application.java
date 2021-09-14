package com.example;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@MapperScan("com.example.mapper")
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.warn("关注微信公众号：武哥聊编程 / Java开发宝典，获取更多Java干货教程");
        log.warn("项目启动后请访问：http://localhost:8888/page/front/index.html");
        log.warn("账号：admin   密码：admin");
    }
}
