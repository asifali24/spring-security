package com.springSecurityImpl.learnSpringSecurity.config;


import com.springSecurityImpl.learnSpringSecurity.filter.JwtAuthFilter;
import com.springSecurityImpl.learnSpringSecurity.filter.RequestResponseFilter;
import com.springSecurityImpl.learnSpringSecurity.filter.SessionValidationFilter;
import com.springSecurityImpl.learnSpringSecurity.handlers.GoogleOauthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

import static com.springSecurityImpl.learnSpringSecurity.enums.Permission.POST_CREATE;
import static com.springSecurityImpl.learnSpringSecurity.enums.Roles.ADMIN;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final RequestResponseFilter requestResponseFilter;
    private final SessionValidationFilter sessionValidationFilter;
    private final GoogleOauthSuccessHandler googleOauthSuccessHandler;


    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

//    private final publicUrl = {
//
//    }

     String[] publicUrl =  {
        "/auth/**", "/oauth2/**", "/login/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .csrf(c -> c.disable())
                    .cors(Customizer.withDefaults())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(publicUrl).permitAll()
                            .requestMatchers(HttpMethod.GET, "/posts").permitAll()
                            .requestMatchers(HttpMethod.POST, "/posts").hasAllAuthorities(POST_CREATE.name())
                            .anyRequest().authenticated()
                    )
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint((request, response, authException) ->
                                    handlerExceptionResolver.resolveException(
                                            request, response, null, authException
                                    )
                            )
                            .accessDeniedHandler((request, response, accessDeniedException) ->
                                    handlerExceptionResolver.resolveException(
                                            request, response, null, accessDeniedException
                                    )
                            )
                    )
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(requestResponseFilter, JwtAuthFilter.class)
                    .addFilterAfter(sessionValidationFilter, JwtAuthFilter.class)
                    .oauth2Login(oauth -> oauth
                            .failureUrl("/login?error=true")
                            .successHandler(googleOauthSuccessHandler)
                    )
                    .httpBasic(Customizer.withDefaults());

            return httpSecurity.build();


    }


    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
