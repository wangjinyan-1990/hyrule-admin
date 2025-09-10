package com.king;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.king")
public class HyruleAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(HyruleAdminApplication.class, args);
    }
}