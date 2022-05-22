package com.master.userservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class User {
    @Id
    private String id;
    private String username;
    private List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("USER"));
}
