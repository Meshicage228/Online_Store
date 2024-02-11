package com.example.coursework.configuration;

import com.example.coursework.configuration.token.TokenGenerationFilter;
import com.example.coursework.configuration.token.TokenValidationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
    private final TokenGenerationFilter generationFilter;

    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(registry -> {
            registry.requestMatchers("/store/login").permitAll();
            registry.requestMatchers("/admin/**").hasAuthority("ADMIN");
            registry.requestMatchers("/store/authorize").permitAll();
            registry.requestMatchers("/orders/**").hasAnyAuthority("USER", "ADMIN");
            registry.requestMatchers("/store/{page}/{size}/**").permitAll();
            registry.requestMatchers("/store/products/{id}").permitAll();
            registry.requestMatchers("/store/users/**").hasAnyAuthority("USER", "ADMIN");
        });
        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);

        http.httpBasic(Customizer.withDefaults());

        http.formLogin(cust -> {
            cust.loginPage("/store/login");
            cust.usernameParameter("loginAuth");
            cust.passwordParameter("password");
            cust.successHandler((request, response, authentication) -> {
                response.sendRedirect("/store/0/10");
            });
            cust.failureHandler((request, response, exception) -> {
                response.sendRedirect("/store/login");
            });
        });
        http.logout(cust -> {
            cust.logoutUrl("/user/logout");
            cust.invalidateHttpSession(true);
        });
        http.addFilterAfter(generationFilter, LogoutFilter.class);
        http.addFilterBefore(validationFilter, SecurityContextHolderAwareRequestFilter.class);
        return http.build();
    }
}
