package com.whozm.yygh.hosp;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.whozm.yygh.hosp.mapper")
@ComponentScan("com.whozm.yygh")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.whozm.yygh" )
public class ServiceHospMainStarter {

    public static void main(String[] args) {
        SpringApplication.run(ServiceHospMainStarter.class,args);
    }
}
