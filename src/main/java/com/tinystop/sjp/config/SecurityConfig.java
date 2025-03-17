package com.tinystop.sjp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    String[] urlsToBePermittedGET = { // GET methods list
        "/",
        "/signupPage",
        "/font.css",
        "/style.css",
        "/signinPage",
        "/debug/session",
        "/find-product"
    };
    String[] urlsToBePermittedPOST = { // POST methods list
        "/signin",
        "/signup",
        "/signout"
    };
    String[] urlsAfterAuthenticatedPOST = {
        "/cart",
        "/wishlist",
        "/orderHistory"
    };
    String[] urlsToBePermittedAdmin = {
        "/admin/**"
    };

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository(); // SecurityContext를 세션에 저장
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,  SecurityContextRepository securityContextRepository) throws Exception {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL); // SecurityContext가 현재 실행 중인 스레드에만 저장 (기본상태)

        http.csrf(csrf -> csrf.ignoringRequestMatchers("/signin", "/signup")) // CSRF 보호 signin signup 페이지 제외
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET,urlsToBePermittedGET).permitAll() // GET method 허용
                        .requestMatchers(HttpMethod.POST,urlsToBePermittedPOST).permitAll() // POST method 허용
                        .requestMatchers(urlsToBePermittedAdmin).hasRole("ADMIN") // admin 허용
                        .requestMatchers(urlsAfterAuthenticatedPOST).authenticated() // 로그인 했을 시 허용
                        .anyRequest().authenticated() // 나머진 권한 필요
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //필요한 경우 세션 생성 Always로 할 시 모든 요청마다 세션 생성
                    .maximumSessions(1) // 한 유저당 1 세션
                    .maxSessionsPreventsLogin(true) // 1 세션 넘으면 다른 로그인 차단
                    .expiredUrl("/login?expired") // 세션 만료되면 여기 페이지로 다이렉트함. 30분 후 만료
                )
                .securityContext(securityContext -> securityContext
                .securityContextRepository(securityContextRepository) // SecurityContext를 세션에 저장
                )
                .formLogin(form -> form // 로그인
                    .loginPage("/signinPage") 
                    .defaultSuccessUrl("/index.html", true)   
                    .failureUrl("/signinPage?error=true") 
                .permitAll()
                )
                .logout(logout -> logout // 로그아웃
                    .logoutUrl("/signout")
                    .logoutSuccessUrl("/index.html")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID") // 로그아웃 후 세션 삭제
                );
                //.securityContext(securityContext -> securityContext.requireExplicitSave(true));
        return http.build();
    }
}
