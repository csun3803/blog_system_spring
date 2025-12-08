//package com.example.blog_system_spring.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // 禁用 CSRF（开发环境可以禁用）
//                .csrf(csrf -> csrf.disable())
//
//                // 配置请求授权
//                .authorizeHttpRequests(auth -> auth
//                        // 允许所有人访问以下路径（不需要登录）
//                        .requestMatchers(
//                                "/",
//                                "/login",
//                                "/register",
//                                "/css/**",
//                                "/js/**",
//                                "/images/**",
//                                "/h2-console/**",  // H2控制台
//                                "/api/**"          // API接口
//                        ).permitAll()
//
//                        // 其他所有请求都需要认证（登录）
//                        .anyRequest().authenticated()
//                )
//
//                // 配置表单登录
//                .formLogin(form -> form
//                        .loginPage("/login")                    // 登录页面URL
//                        .loginProcessingUrl("/login")           // 处理登录的URL
//                        .defaultSuccessUrl("/blog/list", true)  // 登录成功后的默认跳转
//                        .failureUrl("/login?error=true")        // 登录失败后的跳转
//                        .permitAll()                            // 允许所有人访问登录页面
//                )
//
//                // 配置注销
//                .logout(logout -> logout
//                        .logoutUrl("/logout")                   // 注销URL
//                        .logoutSuccessUrl("/login?logout=true") // 注销成功后的跳转
//                        .invalidateHttpSession(true)            // 使Session失效
//                        .deleteCookies("JSESSIONID")            // 删除Cookie
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//}