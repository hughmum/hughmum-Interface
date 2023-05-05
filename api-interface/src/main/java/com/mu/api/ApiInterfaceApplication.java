package com.mu.api;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.mu.api.mapper")
public class ApiInterfaceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiInterfaceApplication.class,args);
    }
}
