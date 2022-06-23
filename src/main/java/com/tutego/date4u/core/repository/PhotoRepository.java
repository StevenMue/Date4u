package com.tutego.date4u.core.repository;

import com.tutego.date4u.core.enities.Photo;
import com.tutego.date4u.core.enities.Unicorn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoRepository extends CrudRepository<Photo, Long>, RevisionRepository<Photo, Long, Long> {

    @Query("SELECT photo FROM Photo photo WHERE photo.profile.nickname=:nickname")
    Optional<Photo> findPhotosByNickname(String nickname);

    @Query("SELECT photo FROM Photo photo WHERE photo.profile.nickname=:nickname AND photo.name=:name")
    Optional<Photo> findPhotoByNicknameAndName(String nickname, String name);

    @Query("SELECT photo FROM Photo photo WHERE photo.profile.nickname=:nickname AND photo.isProfilePhoto=true")
    Optional<Photo> findProfilePhotoByNickname(String nickname);
}
