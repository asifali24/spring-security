package com.springSecurityImpl.learnSpringSecurity.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@Slf4j
public class RequestResponseFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Request -> reqMethod: {} | Url : {} | remoteUser :{} | remoteAdd : {}",
                request.getMethod(), request.getRequestURL() ,
                request.getRemoteUser(), request.getRemoteAddr()
        );

        try{

        filterChain.doFilter(request,response);
        }finally {

        log.info("Response -> status : {}", response.getStatus());
        }
    }
}
