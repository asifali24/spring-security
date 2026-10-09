package com.springSecurityImpl.learnSpringSecurity.filter;

import com.springSecurityImpl.learnSpringSecurity.entities.User;
import com.springSecurityImpl.learnSpringSecurity.services.JwtService;
import com.springSecurityImpl.learnSpringSecurity.services.UserSer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserSer userSer;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String headerToken = request.getHeader("Authorization");

        if(headerToken == null || !headerToken.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = headerToken.split(" ")[1];

        UUID userId = jwtService.getUserIdFromToken(token);

        if(userId != null || SecurityContextHolder.getContext().getAuthentication() == null){
            User retriveUser  = userSer.getUserById(userId);
            UsernamePasswordAuthenticationToken authUser = new UsernamePasswordAuthenticationToken(retriveUser,null,null);
//            authUser.setDetails(
//                    new WebAuthenticationDetailsSource().buildDetails(request) // for getting any details in request like Ip and all
//            );
            SecurityContextHolder.getContext().setAuthentication(authUser);
        }
        filterChain.doFilter(request,response);
        return;
    }
}
