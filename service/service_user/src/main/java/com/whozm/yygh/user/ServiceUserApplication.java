package com.whozm.yygh.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author HZM
 * @date 2023/1/26
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.whozm.yygh")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.whozm.yygh")
@MapperScan("com.whozm.yygh.user.mapper")
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}
