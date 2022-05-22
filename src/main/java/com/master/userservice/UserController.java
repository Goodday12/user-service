package com.master.userservice;

import com.master.userservice.model.User;
import com.master.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {this.repository = repository;}


    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<User> userById(Principal principal, @PathVariable String id) {
        final Optional<User> user = repository.findById(id);
        return ResponseEntity.of(user);
    }

}
