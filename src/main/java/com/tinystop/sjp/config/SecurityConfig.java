package com.tinystop.sjp.Config;

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
    String[] PermittedGET = { // GET methods list
        "/",
        "/signupPage",
        "/font.css",
        "/style.css",
        "/js/signup.js",
        "/signinPage",
        "/debug/session",
        "/find-product",
        "/check-username",
        "/check-email",
        "/change-info",
        "deleteAccountPage",
        "/review/productReview",
        "/product/detail"
    };
    String[] PermittedPOST = { // POST methods list
        "/signin",
        "/signup",
        "/signout",
        "/email/**"
    };
    String[] AfterAuthenticatedGET = {
        "/cart/list",
        "/wishlist",
        "/order/**",
        "/review/**"
    };
    String[] AfterAuthenticatedPOST = {
        "/cart/add",
        "/cart/remove",
        "/wishlist",
        "/order/**",
        "/change-password",
        "/deleteAccount",
        "/review/**"
    };
    
    String[] BePermittedAdmin = {
        "/admin/**"
    };

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository(); // SecurityContext를 세션에 저장
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,  SecurityContextRepository securityContextRepository) throws Exception {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL); // SecurityContext가 현재 실행 중인 스레드에만 저장 (기본상태)

        http.csrf(csrf -> csrf.ignoringRequestMatchers("/signin", "/signup", "/cart/add", "/cart/remove", "/order/**", "/admin/remove-product", "/email/send-code", "/email/verify-code", "/change-password", "/deleteAccount", "/review/**")) // CSRF 보호 제외 목록
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET,PermittedGET).permitAll() // GET method 허용
                        .requestMatchers(HttpMethod.POST,PermittedPOST).permitAll() // POST method 허용
                        .requestMatchers(AfterAuthenticatedGET).authenticated()
                        .requestMatchers(AfterAuthenticatedPOST).authenticated() // 로그인 했을 시 허용
                        .requestMatchers(BePermittedAdmin).hasRole("ADMIN") // admin 허용
                        .anyRequest().authenticated() // 나머진 권한 필요
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //필요한 경우 세션 생성 Always로 할 시 모든 요청마다 세션 생성, STATELESS는 REST API용
                    .sessionFixation(sessionFixation -> sessionFixation.migrateSession()) // 로그인 할 때마다 새로운 세션 ID 발급 (세션 고정 공격 방지)
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
        return http.build();
    }
}
