package com.springSecurityImpl.learnSpringSecurity.exceptions;

public class InvalidSessionException extends RuntimeException {

    public InvalidSessionException(String message){
        super(message);
    }
}
