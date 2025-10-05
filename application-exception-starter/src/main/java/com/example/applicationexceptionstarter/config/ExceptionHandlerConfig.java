package com.example.applicationexceptionstarter.config;

import com.example.applicationexceptionstarter.handler.ProjectExceptionHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;

@Slf4j
@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Import(ProjectExceptionHandler.class)
public class ExceptionHandlerConfig {

    @PostConstruct
    public void init() {
        log.info("Initializing ExceptionHandlerStarter Configuration");
    }
}
