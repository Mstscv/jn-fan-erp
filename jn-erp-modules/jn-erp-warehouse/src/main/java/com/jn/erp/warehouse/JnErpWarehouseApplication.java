package com.jn.erp.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class JnErpWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(JnErpWarehouseApplication.class, args);
        System.out.println("(c) 精恩风机ERP - 仓库管理服务启动成功!");
    }
}
