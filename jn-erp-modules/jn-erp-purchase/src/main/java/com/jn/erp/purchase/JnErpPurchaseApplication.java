package com.jn.erp.purchase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class JnErpPurchaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(JnErpPurchaseApplication.class, args);
        System.out.println("(c) 精恩风机ERP - 采购管理服务启动成功!");
    }
}
