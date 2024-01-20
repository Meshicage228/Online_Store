package com.example.coursework.configuration;

import feign.okhttp.OkHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@RequiredArgsConstructor
@Configuration
public class ProjectConfig {
    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }
    @Bean
    public PageJacksonModule pageJacksonModule() {
        return new PageJacksonModule();
    }
}
