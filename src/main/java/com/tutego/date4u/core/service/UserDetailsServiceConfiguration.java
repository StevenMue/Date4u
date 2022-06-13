package com.tutego.date4u.core.service;

import com.tutego.date4u.repository.UnicornRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

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
