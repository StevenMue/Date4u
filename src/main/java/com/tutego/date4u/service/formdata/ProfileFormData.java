package com.tutego.date4u.service.formdata;

import com.tutego.date4u.service.validation.ValidBirthdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProfileFormData {

    //TODO make valid classes for each

    @NotBlank(message = "Nickname cant be blank or null")
    private String nickname;

    @NotNull(message = "Birthdate cant be empty or null")
    @DateTimeFormat(pattern="dd/MM/yyyy")
    @ValidBirthdate
    private LocalDate birthdate;

    @NotNull(message = "Hornlength cant be empty or null")
    @Min(0)
    private int hornlength;

    @NotNull(message = "Gender cant be empty or null")
    @Min(0)
    private int gender;
    private Integer attractedToGender;
    @NotNull(message = "Discription cant be null")
    private String description;

    @NotNull(message = "Lastseen cant be empty or null")
    private LocalDateTime lastseen;

    public ProfileFormData() {
    }

    public ProfileFormData(String nickname, LocalDate birthdate, int hornlength, int gender, Integer attractedToGender, String description, LocalDateTime lastseen) {
        this.nickname = nickname;
        this.birthdate = birthdate;
        this.hornlength = hornlength;
        this.gender = gender;
        this.attractedToGender = attractedToGender;
        this.description = description;
        this.lastseen = lastseen;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public int getHornlength() {
        return hornlength;
    }

    public void setHornlength(int hornlength) {
        this.hornlength = hornlength;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Integer getAttractedToGender() {
        return attractedToGender;
    }

    public void setAttractedToGender(Integer attractedToGender) {
        this.attractedToGender = attractedToGender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastseen() {
        return lastseen;
    }

    public void setLastseen(LocalDateTime lastseen) {
        this.lastseen = lastseen;
    }

    @Override
    public String toString() {
        return "ProfileFormData{" +
                "nickname='" + nickname + '\'' +
                ", birthdate=" + birthdate +
                ", hornlength=" + hornlength +
                ", gender=" + gender +
                ", attractedToGender=" + attractedToGender +
                ", description='" + description + '\'' +
                ", lastseen=" + lastseen +
                '}';
    }
}
