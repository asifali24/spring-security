package com.springSecurityImpl.learnSpringSecurity.controller;


import com.springSecurityImpl.learnSpringSecurity.dto.AuthReq;
import com.springSecurityImpl.learnSpringSecurity.dto.AuthRes;
import com.springSecurityImpl.learnSpringSecurity.services.AuthServ;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class Auth {

    private final AuthServ authServ;

    @PostMapping("/signup")
    public ResponseEntity<AuthRes> signup(@RequestBody AuthReq body){
        return ResponseEntity.status(HttpStatus.CREATED).body(authServ.Signup(body));
    }


    @GetMapping("/signin")
    public ResponseEntity<AuthRes> signIn(@RequestBody AuthReq body){
        return ResponseEntity.status(HttpStatus.OK).body(authServ.signIn(body));
    }
}
