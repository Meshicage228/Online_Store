package com.example.coursework.configuration;

import com.example.coursework.exceptions.IdNotFountException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
@RequiredArgsConstructor
@Configuration
public class AdminFeignConfig {
    private final ObjectMapper mapper;
    @Bean
    public ErrorDecoder decoder() {
        return ((methodKey, response) -> {
            int status = response.status();
            if (status >= 400 && status < 500) {
                Response.Body body = response.body();
                try {
                    String s = new String(response.body().asInputStream().readAllBytes());
                    return mapper.readValue(body.asInputStream(), IdNotFountException.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        });
    }
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    @Bean
    public JsonFormWriter jsonFormWriter() {
        return new JsonFormWriter();
    }
}
