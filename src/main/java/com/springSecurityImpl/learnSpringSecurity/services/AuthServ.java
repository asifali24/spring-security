package com.springSecurityImpl.learnSpringSecurity.services;


import com.springSecurityImpl.learnSpringSecurity.dto.AuthReq;
import com.springSecurityImpl.learnSpringSecurity.dto.AuthRes;
import com.springSecurityImpl.learnSpringSecurity.entities.User;
import com.springSecurityImpl.learnSpringSecurity.exceptions.InvalidSessionException;
import com.springSecurityImpl.learnSpringSecurity.exceptions.ResourcesAlreadyExist;
import com.springSecurityImpl.learnSpringSecurity.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServ {
    private static final String REFRESH_COOKIE_NAME = "refreshCookies";
    private static final List<String> COOKIE_PATHS = List.of("/", "/auth");

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserSession userSession;
    private final UserSer userSer;

    public AuthRes Signup(AuthReq body,HttpServletResponse response) {

        if(isUserExist(body.getEmail()) != null ){
            throw new ResourcesAlreadyExist("User with Email: "+body.getEmail() +" already exist");
        }

        User newUser = modelMapper.map(body,User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        User savedUser = userRepository.save(newUser);
        System.out.println(savedUser);
        AuthRes ret =  modelMapper.map(savedUser,AuthRes.class);
        ret.setToken(issueTokenPair(savedUser,response));
        return  ret;
    }


    public AuthRes signIn(AuthReq body, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
         new UsernamePasswordAuthenticationToken(body.getEmail(),body.getPassword())
        );

        System.out.println("authentication "+ authentication);
        User user = (User) authentication.getPrincipal();
//        assert  user != null;

        if (user == null) {
            throw new BadCredentialsException("Invalid credentials");
        }
        AuthRes ret =  modelMapper.map(authentication.getPrincipal(),AuthRes.class);

        ret.setToken(issueTokenPair(user,response));

//        userSession.generateNewSessionAndInvalidatePrevOnce(user.getId(), accessToken);
        return  ret;
    }



    public AuthRes refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] token = request.getCookies();

        if (token == null) {
            throw new InvalidSessionException("refresh token not found");
        }

        String refreshToken = Arrays.stream(token)
                .filter(ele -> ele.getName().equals("refreshCookies"))
                .map(ele -> ele.getValue())
                .findFirst()
                .orElseThrow(() -> new InvalidSessionException("refresh token not found"));

        UUID userId = jwtService.getUserIdFromToken(refreshToken);

        if (!userSession.isSessionValid(userId, refreshToken)) {
            throw new InvalidSessionException("refresh token has been revoked");
        }

        User user = userSer.getUserById(userId);

        AuthRes res = modelMapper.map(user,AuthRes.class);
        res.setToken(issueTokenPair(user, response));
        return res;
    }

    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        revokeToken(extractBearerToken(request.getHeader("Authorization")));

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies)
                    .filter(cookie -> REFRESH_COOKIE_NAME.equals(cookie.getName()))
                    .forEach(cookie -> revokeToken(cookie.getValue()));
        }

        clearCookies(request, response);
    }

    private void revokeToken(String token) {
        if (token == null) return;

        try {
            UUID userId = jwtService.getUserIdFromToken(token);
            userSession.revokeSession(userId, token);
        } catch (RuntimeException ignored) {
            // Cookie cleanup must still complete for invalid or expired tokens.
        }
    }

    private void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        LinkedHashSet<String> cookieNames = new LinkedHashSet<>();
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            Arrays.stream(cookies)
                    .map(Cookie::getName)
                    .forEach(cookieNames::add);
        }

        // Always send this deletion header, even when the browser did not send the cookie.
        cookieNames.add(REFRESH_COOKIE_NAME);

        cookieNames.forEach(cookieName -> COOKIE_PATHS.forEach(cookiePath -> response.addHeader(
                HttpHeaders.SET_COOKIE,
                ResponseCookie.from(cookieName, "")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Lax")
                        .path(cookiePath)
                        .maxAge(0)
                        .build()
                        .toString()
        )));
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        return token.isEmpty() ? null : token;
    }



    private User isUserExist(String email){
     return  userRepository.findByEmail(email).orElse(null);
    }

    public String issueTokenPair(User user,HttpServletResponse response){
        String accessToken = jwtService.generateJwtToken(user,false);
        String refreshToken = jwtService.generateJwtToken(user,true);

        userSession.generateNewSessionAndInvalidatePrevOnce(user.getId(), accessToken, refreshToken);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return  accessToken;
    }

}
