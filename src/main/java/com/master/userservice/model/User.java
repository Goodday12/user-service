package com.master.userservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
@Builder
public class User {
    @Id
    private String id;
    private String username;
    private List<GrantedAuthority> roles;

}
