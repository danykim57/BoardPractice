package com.study.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
public class TestFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    // 전처리
    String url = httpServletRequest.getRequestURI();

    BufferedReader br = httpServletRequest.getReader();

    br.lines().forEach(line -> {
      log.info("url : {}, line : {}",url, line);
    });


    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }
}
