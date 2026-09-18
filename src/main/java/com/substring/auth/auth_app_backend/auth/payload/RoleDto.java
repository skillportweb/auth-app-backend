package com.substring.auth.auth_app_backend.auth.payload;

import java.util.UUID;

public class RoleDto {

    private UUID id;
    private String name;

    // No-argument constructor
    public RoleDto() {
    }

    // Parameterized constructor
    public RoleDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter
    public UUID getId() {
        return id;
    }

    // Setter
    public void setId(UUID id) {
        this.id = id;
    }

    // Getter
    public String getName() {
        return name;
    }

    // Setter
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RoleDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}