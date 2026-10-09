package com.springSecurityImpl.learnSpringSecurity.services;


import com.springSecurityImpl.learnSpringSecurity.controller.Auth;
import com.springSecurityImpl.learnSpringSecurity.dto.AuthReq;
import com.springSecurityImpl.learnSpringSecurity.dto.AuthRes;
import com.springSecurityImpl.learnSpringSecurity.entities.User;
import com.springSecurityImpl.learnSpringSecurity.exceptions.ResourceNotFoundException;
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

    public AuthRes Signup(AuthReq body) {

        if(isUserExist(body.getEmail()) != null ){
            throw new ResourcesAlreadyExist("User with Email: "+body.getEmail() +" already exist");
        }

        User newUser = modelMapper.map(body,User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        User savedUser = userRepository.save(newUser);
        System.out.println(savedUser);
        return modelMapper.map(savedUser,AuthRes.class);
    }


    public AuthRes signIn(AuthReq body) {

//        Manual impl
// -
// -
//        User user  = isUserExist(body.getEmail());
//        if( user == null ){
//            throw new ResourceNotFoundException("User with Email: "+body.getEmail() +" not exist");
//        }
//
//        assert user.getPassword() != null;
//        if(passwordEncoder.matches(body.getPassword(),user.getPassword() )){
//
//            return modelMapper.map(user,AuthRes.class);
//        }
//
//
//        throw new BadCredentialsException("Invalid userName / Password");


//        impl via AuthenticationManager


        Authentication authentication = authenticationManager.authenticate(
         new UsernamePasswordAuthenticationToken(body.getEmail(),body.getPassword())
        );

//        User user = (User) authentication.getPrincipal();

        return modelMapper.map(authentication.getPrincipal(),AuthRes.class);
    }






    private User isUserExist(String email){
     return  userRepository.findByEmail(email).orElse(null);
    }
}
