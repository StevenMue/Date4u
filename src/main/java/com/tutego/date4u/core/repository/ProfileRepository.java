package com.tutego.date4u.core.repository;


import com.tutego.date4u.core.enities.Profile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository  extends CrudRepository<Profile, Long> {
    @Query( "SELECT p FROM Profile p WHERE p.nickname = :name" )
    Optional<Profile> findProfileByNickname(String name );

    @Query( "SELECT p FROM Profile p WHERE p.nickname LIKE %:name%" )
    List<Profile> findProfilesByContainingName( String name );

    @Query( "SELECT p FROM Profile p WHERE p.hornlength BETWEEN :min AND :max" )
    List<Profile> findProfilesByHornlengthBetween(short min, short max );

    @Modifying
    @Query( "UPDATE Profile p SET p.lastseen =:lastseen WHERE p.id = :id" )
    int updateLastSeen( long id, LocalDateTime lastseen );

    @Query("SELECT p FROM Profile p WHERE p.unicorn.email =:email")
    Optional<Profile> findProfileByEmail(String email);

    @Modifying
    @Query("UPDATE Profile p SET " +
            "p.nickname=:nickname, " +
            "p.birthdate=:birthdate, " +
            "p.hornlength=:hornlength, " +
            "p.gender=:gender, " +
            "p.attractedToGender=:attractedTo, " +
            "p.description=:description, " +
            "p.lastseen=:lastSeen " +
            "WHERE p.id=:id"
    )
    int updateProfile(long id, String nickname, LocalDate birthdate, short hornlength, byte gender, Byte attractedTo, String description, LocalDateTime lastSeen);

    @Modifying
    @Query( "UPDATE Profile p SET p.lastseen =:lastseen WHERE p.id = :id" )
    int updateProfile( long id, LocalDateTime lastseen );

    @Query( """
        SELECT p FROM Profile p
        WHERE p.lastseen > :#{#lastseen.atDay(1).atStartOfDay()}""" )
    List<Profile> findProfilesLastSeenAfter( Year lastseen );

    @Query( """
    SELECT p
    FROM   Profile p
    WHERE  (p.attractedToGender=:myGender OR p.attractedToGender IS NULL)
       AND (p.gender = :attractedToGender OR :attractedToGender IS NULL)
       AND (p.hornlength BETWEEN :minHornlength AND :maxHornlength)
       AND (p.birthdate  BETWEEN :minBirthdate  AND :maxBirthdate)""" )
    List<Profile> search(byte myGender, Byte attractedToGender,
                         LocalDate minBirthdate, LocalDate maxBirthdate,
                         short minHornlength, short maxHornlength );
    Optional<Profile> findFirstByOrderByHornlengthDesc();

    List<Profile>     findByOrderByHornlengthDesc();

    List<Profile>     findByHornlengthGreaterThan( short min );

    List<Profile>     findFirst10ByOrderByLastseenDesc();
    int deleteProfileById( long id );

}
