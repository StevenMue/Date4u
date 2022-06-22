package com.tutego.date4u.service;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;

@Validated
public class SecurityService {

    public String getUserName(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
