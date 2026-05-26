package com.jn.erp.production;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class JnErpProductionApplication {

    public static void main(String[] args) {
        SpringApplication.run(JnErpProductionApplication.class, args);
        System.out.println("(c) 精恩风机ERP - 生产管理服务启动成功!");
    }
}
