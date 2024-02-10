package com.example.coursework.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final TokenValidationFilter validationFilter;

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(registry -> {
            registry.requestMatchers("/store/{page}/{size}").permitAll();
            registry.requestMatchers("/admin").hasAuthority("USER");
            registry.requestMatchers("/store/login").permitAll();
            registry.requestMatchers("/store/authorize").permitAll();
        });
        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);

     /*   http.formLogin(cust -> {
            cust.loginPage("/auth");
            cust.loginProcessingUrl("/auth");
            cust.usernameParameter("login");
            cust.passwordParameter("cred");
            cust.successHandler((request, response, authentication) -> {
                response.sendRedirect("/home");
            });
            cust.failureHandler((request, response, exception) -> {
                response.sendRedirect("/auth");
            });
        });*/
        http.addFilterBefore(validationFilter, SecurityContextHolderAwareRequestFilter.class);
        return http.build();
    }
}
