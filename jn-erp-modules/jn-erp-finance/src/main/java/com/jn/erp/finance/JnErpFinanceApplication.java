package com.jn.erp.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class JnErpFinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JnErpFinanceApplication.class, args);
        System.out.println("(c) 精恩风机ERP - 财务管理服务启动成功!");
    }
}
