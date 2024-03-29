package com.prolog.prologbackend.Security.Jwt;

import lombok.Getter;

public enum JwtType {
    ACCESS_TOKEN("Access Token",600000),
    REFRESH_TOKEN("Refresh Token",1800000),
    EMAIL_VERIFICATION("Email Verification",1800000);

    @Getter
    private String tokenType;
    @Getter
    private int expirationTime;

    JwtType(String tokenType, int expirationTime){
        this.tokenType = tokenType;
        this.expirationTime = expirationTime;
    }
}
