package com.master.userservice.controller;

import com.master.userservice.model.Roles;
import com.master.userservice.model.User;
import com.master.userservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {this.repository = repository;}


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<User> getUser(Authentication authentication, @PathVariable String id) {
        final boolean isAdmin = authentication.getAuthorities().contains(Roles.ADMIN);
        final Optional<User> user = repository.findById(id);
        user.ifPresent( foundUser -> {
            if (!(foundUser.getUsername().equals(authentication.getName()) || isAdmin))
                throw new AccessDeniedException("You can't retrieve other user");
        } );
        return ResponseEntity.of(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<User> updateUser(Authentication authentication, @RequestBody User user, @PathVariable String id) {
        final boolean isAdmin = authentication.getAuthorities().contains(Roles.ADMIN);
        if ((!user.getUsername().equals(authentication.getName()) && !isAdmin)) {
            throw new AccessDeniedException("You can't change other users info");
        }
        user.setId(id);
        final User updatedUser = repository.save(user);
        return ResponseEntity.of(Optional.of(updatedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.of(Optional.of(repository.findAll()));
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(Authentication authentication, @PathVariable String id) {
        final Optional<User> byId = repository.findById(id);
        byId.ifPresent(user -> {
            if (!(user.getUsername().equals(authentication.getName()) || authentication.getAuthorities().contains(Roles.ADMIN))) {
                throw new AccessDeniedException("You can't delete other user");
            }
        });
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
