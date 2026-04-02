package com.restai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableAsync // Enables asynchronous method execution
@EnableScheduling
public class SpringAiWithDynamicApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiWithDynamicApplication.class, args);
    }
    
    
}
