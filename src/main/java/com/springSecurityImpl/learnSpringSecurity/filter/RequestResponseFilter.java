package com.springSecurityImpl.learnSpringSecurity.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


@Component
@Slf4j
@RequiredArgsConstructor
public class RequestResponseFilter extends OncePerRequestFilter {
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Request -> reqMethod: {} | Url : {} | remoteUser :{} | remoteAdd : {}",
                request.getMethod(), request.getRequestURL() ,
                request.getRemoteUser(), request.getRemoteAddr()
        );

        try{

        filterChain.doFilter(request,response);
        }catch (Exception e){
           log.error("Exception in Request response filter -> {}",e.getMessage());

           handlerExceptionResolver.resolveException(request,response,null,e);

        } finally {

        log.info("Response -> status : {}", response.getStatus());
        }
    }
}
