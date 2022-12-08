package com.study.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Slf4j
public class LogAll {
  @Around("@annotation(* com.study.domain..*Controller.*(..))")
  public Object logAccess(ProceedingJoinPoint joinPoint) throws Throwable {
    // Get the request URI
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = requestAttributes.getRequest();
    String requestUri = request.getRequestURI();

    // Log the request URI
    log.info("Accessing URL: {}", requestUri);

    // Proceed with the original method call
    return joinPoint.proceed();
  }
}
