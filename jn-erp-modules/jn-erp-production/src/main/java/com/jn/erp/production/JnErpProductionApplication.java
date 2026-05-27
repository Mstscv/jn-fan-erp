package com.jn.erp.production;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.jn.erp.**.mapper")
@SpringBootApplication
public class JnErpProductionApplication {

    public static void main(String[] args) {
        SpringApplication.run(JnErpProductionApplication.class, args);
        System.out.println("(c) 精恩风机ERP - 生产管理服务启动成功!");
    }
}
