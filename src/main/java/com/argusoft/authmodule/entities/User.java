package com.argusoft.authmodule.entities;

import com.argusoft.authmodule.custom.EncryptionConverter;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //encrypted before being stored in the database and decrypted when retrieved
    @Convert(converter = EncryptionConverter.class)
    private String firstName;

    @Convert(converter = EncryptionConverter.class)
    private String lastName;

    //unique and cannot be null
    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column()
    private String password;

    @Column(name = "password_setup_token")
    private String passwordSetupToken;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_appid",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "app_id")
    )
    private Set<AppID> appIds = new HashSet<>();
    // Getters and setters
    public Set<AppID> getAppIds() {
        return appIds;
    }

    public void setAppIds(Set<AppID> appIds) {
        this.appIds = appIds;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSetupToken() {
        return passwordSetupToken;
    }

    public void setPasswordSetupToken(String passwordSetupToken) {
        this.passwordSetupToken = passwordSetupToken;
    }
}