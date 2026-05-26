package com.ruoyi.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class RuoYiSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuoYiSystemApplication.class, args);
        System.out.println("(c) 精恩风机ERP - 系统服务启动成功! http://localhost:9201");
    }
}
