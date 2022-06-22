package com.tutego.date4u.service.formdata;

import com.tutego.date4u.service.validation.ValidEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UnicornFormData {

    @Email
    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String password;


    public UnicornFormData() {
    }

    public UnicornFormData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UnicornFormData{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
