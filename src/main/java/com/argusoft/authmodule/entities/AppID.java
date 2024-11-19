package com.argusoft.authmodule.entities;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class AppID {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appId;  // Unique identifier for the app/subproject

    @ManyToMany(mappedBy = "appIds", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    // Getters and setters
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
