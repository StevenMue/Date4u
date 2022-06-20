package com.tutego.date4u.repository.enities;




import javax.persistence.*;


@Entity
@Access( AccessType.FIELD )
public class Unicorn {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    private String email;

    private String password;

    @OneToOne
    @JoinColumn( name = "profile_fk" )
    private Profile profile;

    public Unicorn() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
