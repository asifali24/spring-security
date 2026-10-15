package com.springSecurityImpl.learnSpringSecurity.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {


    @Before("execution(* com.springSecurityImpl.learnSpringSecurity.services..*(..))")
    public void beforeEveryMethodExecution(JoinPoint joinPoint){
        log.info("logging from beforeEveryMethodExecution -> {}", joinPoint);
    }
}
