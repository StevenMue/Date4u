package com.tutego.date4u.service;

import com.tutego.date4u.core.repository.UnicornRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.transaction.Transactional;


//TODO use own DetailManager
@Configuration
public class UserDetailsServiceConfiguration {

    @Autowired
    UnicornRepository unicornRepository;

    @Bean
    @Transactional
    public UserDetailsService userDetailsService() {
       return new MyUserDetailsManager(unicornRepository);
    }
}
