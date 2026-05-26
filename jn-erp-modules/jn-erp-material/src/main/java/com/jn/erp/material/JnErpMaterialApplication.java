package com.jn.erp.material;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class JnErpMaterialApplication {

    public static void main(String[] args) {
        SpringApplication.run(JnErpMaterialApplication.class, args);
        System.out.println("(c) 精恩风机ERP - 物料管理服务启动成功!");
    }
}
