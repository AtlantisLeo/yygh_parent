package com.whozm.yygh.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author HZM
 * @date 2023/1/24
 */
@SpringBootApplication
public class GateApplication {
    public static void main(String[] args) {
        SpringApplication.run(GateApplication.class,args);
    }
}
