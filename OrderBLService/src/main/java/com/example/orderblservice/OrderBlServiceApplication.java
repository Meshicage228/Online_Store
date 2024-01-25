package com.example.orderblservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrderBlServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderBlServiceApplication.class, args);
    }

}
