package com.example.coursework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.coursework.clients")
public class CourseWorkApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseWorkApplication.class, args);
    }

}
