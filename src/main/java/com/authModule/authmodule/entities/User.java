package com.authModule.authmodule.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

import com.authModule.authmodule.custom.EncryptionConverter;

@Entity
@Data
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
    @Convert(converter = EncryptionConverter.class)
    @Column(unique = true, nullable = false)
    private String email;

    @Convert(converter = EncryptionConverter.class)
    @Column(unique = true, nullable = false)
    private String username;

    @Column()
    private String password;

    private String phone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_appid",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "app_id")
    )
    private Set<AppID> appIds = new HashSet<>();
}
