package com.tutego.date4u.service;


import com.tutego.date4u.core.repository.UnicornRepository;
import com.tutego.date4u.core.enities.Unicorn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
public class UnicornService {

    UnicornRepository unicornRepository;

    public UnicornService(@Autowired UnicornRepository unicornRepository) {
        this.unicornRepository = unicornRepository;
    }

    public Optional<Unicorn> getProfileByEmail(String email){
        return Optional.ofNullable(unicornRepository.findUnicornsByEmail(email));
    }
}
