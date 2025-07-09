package com.example.domain.user;

public enum UserRole {
    USER, ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
