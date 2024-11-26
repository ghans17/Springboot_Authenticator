package com.argusoft.authmodule.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "properties")
public class PropertyManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // Name of the property, e.g., "otp_validation", "otp_inResponse"

    @Column(nullable = false)
    private Boolean value;  // Value of the property, e.g., "true", "false"

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}


