package com.tutego.date4u.core.repository;

import com.tutego.date4u.core.enities.Profile;
import com.tutego.date4u.core.enities.Unicorn;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnicornRepository extends CrudRepository<Unicorn, Long> {

    Unicorn getById(long ID);
    Unicorn findUnicornsByEmail(String email);
    @Modifying
    @Query("UPDATE Unicorn u SET u.email = :email, u.password = :password, u.profile = :profile WHERE u.email= :email")
    int update(String email, String password, Profile profile);
    @Modifying
    @Query("UPDATE Unicorn u SET u.password = :password WHERE u.email = :email")
    int update(String email, String password);
}
