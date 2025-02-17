package com.tinystop.sjp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    String[] urlsToBePermittedAll_GET = {
        "/signupPage",
        "/home",
        "/font.css",
        "/style.css",
        "/signinPage",
    };
    String[] urlsToBePermittedAll_POST = {
        "/signin",
        "/signup"
    };
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
        // .csrf(csrf -> csrf.enable()) => CSRF 보호 활성화
                .authorizeHttpRequests((authorize) -> 
                    authorize
                        .requestMatchers(HttpMethod.GET,urlsToBePermittedAll_GET).permitAll() // GET method 허용
                        .requestMatchers(HttpMethod.POST,urlsToBePermittedAll_POST).permitAll() // POST method 허용
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                    .loginPage("/signinPage") 
                    .defaultSuccessUrl("/")   
                    .failureUrl("/signinPage?error=true") 
                .permitAll()
                )
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                );
        return http.build();
    }
}
