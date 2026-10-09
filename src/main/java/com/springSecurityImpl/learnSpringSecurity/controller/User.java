package com.springSecurityImpl.learnSpringSecurity.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/users")
public class User {

    @GetMapping
    public ResponseEntity<Map<String,String>> test(){
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("test","test"));
    }
}
