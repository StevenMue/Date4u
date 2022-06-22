package com.tutego.date4u.core.enities;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Access( AccessType.FIELD )
@EntityListeners( AuditingEntityListener.class )
public class Photo {


    @Id @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;
    @ManyToOne
    @JoinColumn( name = "profile_fk" )
    private Profile profile;

    public String name;
    @Column( name = "is_profile_photo" )
    public boolean isProfilePhoto;
    @Column( nullable = false, updatable = false )
    @CreatedDate
    public LocalDateTime created;


    public Photo(Long id, String name, boolean isProfilePhoto, LocalDateTime created) {
        this.id = id;
        this.name = name;
        this.isProfilePhoto = isProfilePhoto;
        this.created = created;
    }

    public Photo() {
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override public String toString() {
        return "Photo[" + id + "]";
    }
}