package com.tinystop.sjp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    String[] urlsToBePermittedAll_GET = { // GET methods list
        "/signupPage",
        "/home",
        "/font.css",
        "/style.css",
        "/signinPage",
    };
    String[] urlsToBePermittedAll_POST = { // POST methods list
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
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .formLogin(form -> form
                    .loginPage("/signinPage") 
                    .defaultSuccessUrl("/home")   
                    .failureUrl("/signinPage?error=true") 
                .permitAll()
                )
                .logout(logout -> logout
                    .logoutUrl("/signout")
                    .logoutSuccessUrl("/home")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                );
        return http.build();
    }
}
