package com.springSecurityImpl.learnSpringSecurity.dto;

import lombok.Data;

import java.util.UUID;


@Data
public class AuthRes {

    private UUID id;
    private String email;
//    private String password;
}
