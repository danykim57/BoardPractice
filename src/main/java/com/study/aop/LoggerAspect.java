package com.study.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

    @Around("execution(* com.study.domain..*Controller.*(..)) || execution(* com.study.domain..*Service.*(..)) || execution(* com.study.domain..*Mapper.*(..))")
    public Object printLog(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Throwable {

        String name = joinPoint.getSignature().getDeclaringTypeName();
        String type = "";

        if (name.contains("Controller")) {
            type = "Controller ===> ";
            String uuid = UUID.randomUUID().toString();
            request.setAttribute("uuid", uuid);

        } else if (name.contains("Service")) {
            type = "ServiceImpl ===> ";

        } else if (name.contains("Mapper")) {
            type = "Mapper ===> ";
        }

        log.debug(type + name + "." + joinPoint.getSignature().getName() + "()");
        return joinPoint.proceed();
    }

}
