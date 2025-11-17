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
        .csrf()
        .disable()
        .headers()
        .disable()
        .exceptionHandling()
        .and()
        .sessionManagement()
        // Redis 세션을 사용하는 엔드포인트를 위해 IF_REQUIRED로 변경
        // /api/session/** 경로는 세션 사용, 나머지는 JWT 사용
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        .and()
        .authorizeRequests()
        .anyRequest().permitAll()
        .and()
        // JWT 인증을 사용하므로 form 로긴은 비활성처리
        .formLogin()
        .disable();
  }
}
