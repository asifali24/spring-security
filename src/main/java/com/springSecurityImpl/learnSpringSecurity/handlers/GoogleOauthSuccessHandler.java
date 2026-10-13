package com.springSecurityImpl.learnSpringSecurity.handlers;

import com.springSecurityImpl.learnSpringSecurity.entities.User;
import com.springSecurityImpl.learnSpringSecurity.repository.UserRepository;
import com.springSecurityImpl.learnSpringSecurity.services.JwtService;
import com.springSecurityImpl.learnSpringSecurity.services.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class GoogleOauthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserSession userSession;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) oAuth2AuthenticationToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Google account did not provide an email");
        }
        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null){
            User newUser  = User.builder()
                    .email(email)
                    .build();

            user = userRepository.save(newUser);
        }
        String accessToken = jwtService.generateJwtToken(user, false);
        String refreshToken = jwtService.generateJwtToken(user, true);
        userSession.generateNewSessionAndInvalidatePrevOnce(user.getId(), accessToken, refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshCookies", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        getRedirectStrategy().sendRedirect(request, response,
                frontendUrl + "/oauth/callback#access_token=" + accessToken);
    }

}
