package com.springSecurityImpl.learnSpringSecurity.constant;

public final class JwtConstant {

    private JwtConstant(){}


    public static final long ACCESS_TOKEN_DURATION =
            1000L * 60 * 15; // 15 minutes

    public static final long REFRESH_TOKEN_DURATION =
            1000L * 60 * 60 * 24 * 30; // 30 days
}
