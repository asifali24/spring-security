package com.springSecurityImpl.learnSpringSecurity.filter;

import com.springSecurityImpl.learnSpringSecurity.exceptions.InvalidSessionException;
import com.springSecurityImpl.learnSpringSecurity.services.JwtService;
import com.springSecurityImpl.learnSpringSecurity.services.UserSession;
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
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserSession userSession;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String authHeader = request.getHeader("Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer")){
                filterChain.doFilter(request,response);
                return;
            }

            String token  = authHeader.split(" ")[1];
            UUID userId = jwtService.getUserIdFromToken(token);

            Boolean isSessionValid = userSession.isSessionValid(userId,token);

            log.info("isSessionValid -> {}" ,isSessionValid);

            if(!isSessionValid){
                throw  new InvalidSessionException("old/invalid session");
            }

            filterChain.doFilter(request,response);
        }catch (Exception e){
            handlerExceptionResolver.resolveException(request,response,null, e);
        }
    }
}
