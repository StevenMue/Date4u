package com.tutego.date4u.service;

import com.tutego.date4u.core.repository.UnicornRepository;
import com.tutego.date4u.core.enities.Unicorn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

import java.lang.invoke.MethodHandles;
import java.util.Collection;


//TODO not implemented

public class UserDetailsService implements UserDetailsManager {
    private AuthenticationManager authenticationManager;
    private UserCache userCache = new NullUserCache();

    UnicornRepository unicornRepository;

    Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public UserDetailsService( @Autowired UnicornRepository unicornRepository) {
        this.unicornRepository=unicornRepository;
    }

    @Override
    public void createUser(UserDetails user) {
        validateUserDetails(user);
        Unicorn unicorn = new Unicorn();
        unicorn.setEmail(user.getUsername());
        unicorn.setPassword(user.getPassword());
        unicorn.setProfile(null);
        if(!userExists(user.getUsername())) {
            unicornRepository.save(unicorn);
        }else{
            updateUser(user);
        }

    }

    @Override
    public void updateUser(UserDetails user) {
        validateUserDetails(user);
        Unicorn unicorn = unicornRepository.findUnicornsByEmail(user.getUsername());
        if(unicorn!=null){
            if(user.getPassword().contains("{noop}")) {
                unicorn.setPassword(user.getPassword()+2);
            }else{
                unicorn.setPassword("{noop}"+user.getPassword()+2);
            }
            unicornRepository.update(unicorn.getEmail(), unicorn.getPassword());
        }
    }

    @Override
    public void deleteUser(String username) {
        //TODO
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        //TODO
    }

    @Override
    public boolean userExists(String email) {
        Unicorn uni = unicornRepository.findUnicornsByEmail(email);
        return uni != null;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Unicorn uni = unicornRepository.findUnicornsByEmail(email);
        if(uni==null){
            throw new UsernameNotFoundException("No unicorn with that email: "+email+" exits.");
        }
        log.error(uni.getEmail());
        return new User(uni.getEmail(),
                uni.getPassword(),
        true,
        true,
        true,
        true,
                AuthorityUtils.NO_AUTHORITIES); //TODO maybe change so that it can have roles
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public UserCache getUserCache() {
        return userCache;
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    private void validateUserDetails(UserDetails user) {
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        validateAuthorities(user.getAuthorities());
    }

    private void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Authorities list must not be null");
        for (GrantedAuthority authority : authorities) {
            Assert.notNull(authority, "Authorities list contains a null entry");
            Assert.hasText(authority.getAuthority(), "getAuthority() method must return a non-empty string");
        }
    }
}
