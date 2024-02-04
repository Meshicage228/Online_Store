/*
package com.example.userblservice.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@RequiredArgsConstructor
public class UsersConfig {
    private final TokenValidationFilter validationFilter;
    private final TokenGenerationFilter generationFilter;
    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(registry -> {
            registry.requestMatchers("/v1/users/save").permitAll();
            registry.requestMatchers("/v1/users/login").permitAll();
            registry.requestMatchers("/v1/users/{user_id}").hasAuthority("USER");
            registry.requestMatchers("/v1/users/{page}/{size}").hasAuthority("USER");
        });
        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);

        http.addFilterAfter(generationFilter, LogoutFilter.class);
        http.addFilterBefore(validationFilter, SecurityContextHolderAwareRequestFilter.class);
        return http.build();
    }
}
*/
