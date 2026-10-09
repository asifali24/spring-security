package com.springSecurityImpl.learnSpringSecurity.exceptions;

public class ResourcesAlreadyExist extends RuntimeException {

    public ResourcesAlreadyExist(String message){
        super(message);
    }
}
