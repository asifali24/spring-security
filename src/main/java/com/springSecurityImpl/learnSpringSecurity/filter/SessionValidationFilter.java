package com.springSecurityImpl.learnSpringSecurity.filter;

import com.springSecurityImpl.learnSpringSecurity.services.JwtService;
import com.springSecurityImpl.learnSpringSecurity.services.UserSession;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserSession userSession;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }

        String token  = authHeader.split(" ")[1];
        UUID userId = jwtService.getUserIdFromToken(token);

        userSession.isSessionValid(userId,token);

        filterChain.doFilter(request,response);
    }
}
