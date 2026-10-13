package com.springSecurityImpl.learnSpringSecurity.dto;


import com.springSecurityImpl.learnSpringSecurity.enums.Roles;
import lombok.Data;

import java.util.Set;

@Data
public class AuthReq {

    private String email;
    private String password;
    private Set<Roles> roles;
}
