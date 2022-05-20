package com.master.userservice.model;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public class User {
    @Id
    private UUID id;
    private String firstName;
}
