package com.receiptvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReceiptvaultApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReceiptvaultApplication.class, args);
    }
}
