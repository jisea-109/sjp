package com.tinystop.sjp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    String[] urlsToBePermittedGET = { // GET methods list
        "/signupPage",
        "/home",
        "/font.css",
        "/style.css",
        "/signinPage",
        "/debug/session"
    };
    String[] urlsToBePermittedPOST = { // POST methods list
        "/signin",
        "/signup"
    };
    String[] urlsToBeAuthenticatedAllPOST = {
        "/cart",
        "/wishlist"
    };
    String[] urlsToBePermittedAdmin = {
        "/admin/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/signin", "/signup")) // CSRF 보호 활성화
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET,urlsToBePermittedGET).permitAll() // GET method 허용
                        .requestMatchers(HttpMethod.POST,urlsToBePermittedPOST).permitAll() // POST method 허용
                        .requestMatchers(urlsToBePermittedAdmin).hasRole("ADMIN") // admin 허용
                        .requestMatchers(urlsToBeAuthenticatedAllPOST).authenticated() // 로그인 했을 시 허용
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(true)
                )
                .formLogin(form -> form
                    .loginPage("/signinPage") 
                    .defaultSuccessUrl("/index.html", true)   
                    .failureUrl("/signinPage?error=true") 
                .permitAll()
                )
                .logout(logout -> logout
                    .logoutUrl("/signout")
                    .logoutSuccessUrl("/index.html")
                    .invalidateHttpSession(false)
                    //.deleteCookies("JSESSIONID") // 세션 삭제
                );
                //.securityContext(securityContext -> securityContext.requireExplicitSave(true));
        return http.build();
    }
}
