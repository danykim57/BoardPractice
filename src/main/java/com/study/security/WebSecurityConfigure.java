package com.study.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/swagger-resources", "/webjars/**", "/static/**", "/templates/**", "/h2/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        // CSRF protection configured for hybrid authentication approach
        // Disabled for REST API endpoints (*.json, /api/**) that use JWT
        // Enabled for session-based form endpoints (*.do) for CSRF protection
        .csrf()
            .ignoringAntMatchers("/api/**", "/**/*.json", "/members")
        .and()
        // Security headers enabled to protect against common vulnerabilities
        .headers()
            .frameOptions().deny()                    // Prevent clickjacking
            .contentTypeOptions().and()               // Prevent MIME-sniffing
            .xssProtection().and()                    // Enable XSS protection
            .httpStrictTransportSecurity()            // Enforce HTTPS in production
                .includeSubDomains(true)
                .maxAgeInSeconds(31536000)
        .and()
        .exceptionHandling()
        .and()
        .sessionManagement()
        // Redis 세션을 사용하는 엔드포인트를 위해 IF_REQUIRED로 변경
        // /api/session/** 경로는 세션 사용, 나머지는 JWT 사용
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        .and()
        // Authentication and authorization rules
        // Public access: static resources, list views, detail views, test endpoints
        // Protected: write, update, delete operations require authentication
        .authorizeRequests()
            // Public endpoints - no authentication required
            .antMatchers("/", "/post/list.do", "/post/view.do").permitAll()
            .antMatchers("/members", "/api/session/create").permitAll()
            .antMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()

            // Protected endpoints - authentication required
            .antMatchers("/post/write.do", "/post/save.do", "/post/save.json").authenticated()
            .antMatchers("/post/update.do", "/post/delete.do").authenticated()
            .antMatchers("/api/session/info", "/api/session/logout", "/api/session/extend").authenticated()

            // Default: allow all other requests (can be changed to authenticated() for stricter security)
            .anyRequest().permitAll()
        .and()
        // JWT 인증을 사용하므로 form 로긴은 비활성처리
        .formLogin()
        .disable();
  }
}
