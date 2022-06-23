package com.tutego.date4u.service.formdata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ProfileFullFormData extends ProfileFormData{
    private String email;
    private List<String> photos;
    private List<String> likesNicknames;

    public ProfileFullFormData() {
    }

    public ProfileFullFormData(String email, List<String> photos, List<String> likesNicknames) {
        this.email = email;
        this.photos = photos;
        this.likesNicknames = likesNicknames;
    }

    public ProfileFullFormData(String nickname, LocalDate birthdate, int hornlength, int gender, Integer attractedToGender, String description, LocalDateTime lastseen, String email, List<String> photos, List<String> likesNicknames) {
        super(nickname, birthdate, hornlength, gender, attractedToGender, description, lastseen);
        this.email = email;
        this.photos = photos;
        this.likesNicknames = likesNicknames;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<String> getLikesNicknames() {
        return likesNicknames;
    }

    public void setLikesNicknames(List<String> likesNicknames) {
        this.likesNicknames = likesNicknames;
    }
}
