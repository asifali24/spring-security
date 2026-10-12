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

import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServ {
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
        ret.setToken(accessAndRefreshTokenGeneration(savedUser,response));
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

        ret.setToken(accessAndRefreshTokenGeneration(user,response));

//        userSession.generateNewSessionAndInvalidatePrevOnce(user.getId(), accessToken);
        return  ret;
    }



    public AuthRes refreshToken(HttpServletRequest request) {
        Cookie[] token = request.getCookies();

        String refreshToken = Arrays.stream(token)
                .filter(ele -> ele.getName().equals("refreshCookies"))
                .map(ele -> ele.getValue())
                .findFirst()
                .orElseThrow(() -> new InvalidSessionException("refresh token not found"));

        UUID userId = jwtService.getUserIdFromToken(refreshToken);

        User user = userSer.getUserById(userId);

        String accessToken = jwtService.generateJwtToken(user,false);

        AuthRes res = modelMapper.map(user,AuthRes.class);
        res.setToken(accessToken);
        return res;
    }



    private User isUserExist(String email){
     return  userRepository.findByEmail(email).orElse(null);
    }

    private String accessAndRefreshTokenGeneration(User user,HttpServletResponse response){
        String accessToken = jwtService.generateJwtToken(user,false);
        String refreshToken = jwtService.generateJwtToken(user,true);

        ResponseCookie cookie = ResponseCookie.from("refreshCookies",refreshToken)
                .httpOnly(true)
                .sameSite("/auth/refresh")
                .maxAge(30)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return  accessToken;
    }

}
