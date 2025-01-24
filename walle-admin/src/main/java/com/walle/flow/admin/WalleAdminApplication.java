package com.walle.flow.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.walle.flow.admin",
        "com.walle.test.springboot.operator"
})
public class WalleAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalleAdminApplication.class, args);
        System.out.println("服务启动成功...........");
    }
}