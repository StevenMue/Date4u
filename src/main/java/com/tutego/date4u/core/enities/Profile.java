package com.tutego.date4u.core.enities;



import org.hibernate.envers.Audited;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Access( AccessType.FIELD )
@Audited
public class Profile {

    public static final int FEE = 1;
    public static final int MAA = 2;

    @Id @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    private String nickname;
    private LocalDate birthdate;
    private short hornlength;
    private byte gender;

    @Column( name = "attracted_to_gender" )
    private Byte attractedToGender;

    private String description;
    private LocalDateTime lastseen;

    @OneToOne( mappedBy = "profile" )
    private Unicorn unicorn;
    @OneToMany(
            mappedBy = "profile",
            fetch = FetchType.EAGER )
    private Set<Photo> photos;


    @ManyToMany
    @JoinTable(
            name = "likes",
            joinColumns = @JoinColumn(name = "liker_fk"),
            inverseJoinColumns = @JoinColumn(name = "likee_fk")
    )
    Set<Profile> profilesILike;
    @ManyToMany
    @JoinTable(name = "likes",
            joinColumns = @JoinColumn(name = "likee_fk"),
            inverseJoinColumns = @JoinColumn(name = "liker_fk")
    )
    Set<Profile> profilesThatLikeMe;
    public Profile add( Photo photo ) {
        photos.add( photo );
        return this;
    }
    public Set<Photo> getPhotos() {
        return photos;
    }

    public Photo getProfilePhoto() {
        for (Photo photo: photos) {
            if(photo.isProfilePhoto){
                return photo;
            }
        }
        return null;
    }
    public Photo getPhoto(String name){
        for (Photo photo: photos) {
            if(photo.name.equals(name)){
                return photo;
            }
        }
        return null;
    }
    public Unicorn getUnicorn() {
        return unicorn;
    }

    public void setUnicorn(Unicorn unicorn) {
        this.unicorn = unicorn;
    }

    protected Profile() {
    }

    public Profile( String nickname, LocalDate birthdate, int hornlength,
                    int gender, Integer attractedToGender, String description,
                    LocalDateTime lastseen ) {
        setNickname( nickname );
        setBirthdate( birthdate );
        setHornlength( hornlength );
        setGender( gender );
        setAttractedToGender( attractedToGender );
        setDescription( description );
        setLastseen( lastseen );
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname( String nickname ) {
        this.nickname = nickname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate( LocalDate birthdate ) {
        this.birthdate = birthdate;
    }

    public int getHornlength() {
        return hornlength;
    }

    public void setHornlength( int hornlength ) {
        this.hornlength = (short) hornlength;
    }

    public int getGender() {
        return gender;
    }

    public void setGender( int gender ) {
        this.gender = (byte) gender;
    }

    public @Nullable Integer getAttractedToGender() {
        return attractedToGender == null ? null : attractedToGender.intValue();
    }

    public void setAttractedToGender( @Nullable Integer attractedToGender ) {
        this.attractedToGender = attractedToGender == null ? null : attractedToGender.byteValue();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public LocalDateTime getLastseen() {
        return lastseen;
    }

    public void setLastseen( LocalDateTime lastseen ) {
        this.lastseen = lastseen;
    }

    public Set<Profile> getProfilesILike() {
        return profilesILike;
    }

    public void setProfilesILike(Set<Profile> profilesILike) {
        this.profilesILike = profilesILike;
    }

    public Set<Profile> getProfilesThatLikeMe() {
        return profilesThatLikeMe;
    }

    public void setProfilesThatLikeMe(Set<Profile> profilesThatLikeMe) {
        this.profilesThatLikeMe = profilesThatLikeMe;
    }

    @Override public boolean equals(Object o ) {
        return o instanceof Profile profile
                && nickname.equals( profile.nickname );
    }

    @Override public int hashCode() {
        return nickname.hashCode();
    }

    @Override public String toString() {
        return "Profile[id=%d]".formatted( id );
    }
}