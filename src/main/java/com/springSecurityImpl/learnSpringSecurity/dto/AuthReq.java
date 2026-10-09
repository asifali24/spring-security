package com.springSecurityImpl.learnSpringSecurity.dto;


import lombok.Data;

@Data
public class AuthReq {

    private String email;
    private String password;
}
