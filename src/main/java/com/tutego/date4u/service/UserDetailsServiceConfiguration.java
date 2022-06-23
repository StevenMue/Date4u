package com.tutego.date4u.service;

import com.tutego.date4u.core.repository.UnicornRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.transaction.Transactional;


//TODO use own DetailManager
@Configuration
public class UserDetailsServiceConfiguration {


    @Bean
    @Transactional
    public UserDetailsService userDetailsService(@Autowired UnicornRepository unicornRepository) {
       return new UserDetailsService(unicornRepository);
    }
}
