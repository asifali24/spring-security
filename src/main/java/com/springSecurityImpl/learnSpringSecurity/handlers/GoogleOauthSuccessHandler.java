package com.springSecurityImpl.learnSpringSecurity.handlers;

import com.springSecurityImpl.learnSpringSecurity.entities.User;
import com.springSecurityImpl.learnSpringSecurity.repository.UserRepository;
import com.springSecurityImpl.learnSpringSecurity.services.AuthServ;
import com.springSecurityImpl.learnSpringSecurity.services.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOauthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

//    private final AuthServ authServ;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) oAuth2AuthenticationToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        log.info("--------------------------- email: {}",email);


        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null){
            User newUser  = User.builder()
                    .email(email)
                    .build();

            user = userRepository.save(newUser);
        }
        String accessToken = jwtService.generateJwtToken(user,false);
        String refreshToken = jwtService.generateJwtToken(user,true);

        log.info("accessToken : {}",accessToken);
        log.info("refreshToken : {}",refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshCookies",refreshToken)
                .httpOnly(true)
                .sameSite("/auth")
                .maxAge(30)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        getRedirectStrategy().sendRedirect(request,response,"http://localhost:4200/login?token"+accessToken);

//        Map<String,String> tokens = authServ.oAuthSignInSignUp(email);

//        for(Map<String,String> val : tokens.entrySet()){
//            log.info("ket value {}",val);
//        }

//        tokens.forEach((k,v) -> {
//            log.info(" {} -> {}",k,v);
//        });


//        if(email != null){
//            // sign-in flow
//
//        }else {
//            //signup flow
//        }



    }

}
