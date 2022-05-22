package com.master.userservice.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Locale;

public enum Roles implements GrantedAuthority {
    USER, ADMIN;

    @Override
    public String getAuthority() {
        return this.toString().toUpperCase(Locale.ROOT);
    }

}
