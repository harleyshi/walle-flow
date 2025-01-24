package com.walle.test.springboot;

import com.walle.operator.OperatorsRegister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class WalleFlowDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalleFlowDemoApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeFlowEngine(ApplicationReadyEvent event) {
        OperatorsRegister operatorsRegister = OperatorsRegister.getInstance();
        System.out.println("服务启动成功...........");
    }
}