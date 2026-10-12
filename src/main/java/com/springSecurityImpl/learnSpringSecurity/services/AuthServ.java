package com.springSecurityImpl.learnSpringSecurity.services;


import com.springSecurityImpl.learnSpringSecurity.dto.AuthReq;
import com.springSecurityImpl.learnSpringSecurity.dto.AuthRes;
import com.springSecurityImpl.learnSpringSecurity.entities.User;
import com.springSecurityImpl.learnSpringSecurity.exceptions.ResourcesAlreadyExist;
import com.springSecurityImpl.learnSpringSecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServ {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserSession userSession;

    public AuthRes Signup(AuthReq body) {

        if(isUserExist(body.getEmail()) != null ){
            throw new ResourcesAlreadyExist("User with Email: "+body.getEmail() +" already exist");
        }

        User newUser = modelMapper.map(body,User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        User savedUser = userRepository.save(newUser);
        System.out.println(savedUser);
        AuthRes ret =  modelMapper.map(savedUser,AuthRes.class);
        ret.setToken(jwtService.generateJwtToken(savedUser));
        return  ret;
    }


    public AuthRes signIn(AuthReq body) {

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
        String token = jwtService.generateJwtToken(user);
        ret.setToken(token);
        userSession.generateNewSessionAndInvalidatePrevOnce(user.getId(), token);
        return  ret;
    }






    private User isUserExist(String email){
     return  userRepository.findByEmail(email).orElse(null);
    }
}
