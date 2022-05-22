package com.master.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@Builder
public class User {

    @Id
    private String id;
    private String username;
    private List<String> roles;
    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Code> codes;
    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Submission> submissions;

}
