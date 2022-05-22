package com.master.userservice.controller;

import com.master.userservice.model.Code;
import com.master.userservice.model.Roles;
import com.master.userservice.model.User;
import com.master.userservice.repository.CodeRepository;
import com.master.userservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static com.master.userservice.model.Roles.ADMIN;

@RestController
@RequestMapping(value = "/users/{userId}")
public class CodeController {

    private final CodeRepository codeRepository;
    private final UserRepository userRepository;

    public CodeController(CodeRepository codeRepository, UserRepository userRepository) {
        this.codeRepository = codeRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/code/{codeId}", method = RequestMethod.GET)
    public ResponseEntity<Code> getUserCode(Authentication authentication, @PathVariable String userId, @PathVariable String codeId) {
        final User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        if (!(authentication.getAuthorities().contains(ADMIN) || user.getCodes().stream().anyMatch(code -> code.getId().equals(codeId)))){
            throw new AccessDeniedException("You can't access not your code");
        }
        return ResponseEntity.of(codeRepository.findById(codeId));
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public ResponseEntity<List<Code>> getAllUserCode(Authentication authentication, @PathVariable String userId) {
        final User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        if (!(authentication.getName().equals(user.getUsername()) || authentication.getAuthorities().contains(ADMIN))){
            throw new AccessDeniedException("You can't access not your codes");
        }
        return ResponseEntity.of(Optional.of(user.getCodes()));
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/code", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Code> saveUserCode(Authentication authentication, @PathVariable String userId, @RequestBody Code code) {
        final User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        if (!(authentication.getName().equals(user.getUsername()) || authentication.getAuthorities().contains(ADMIN))){
            throw new AccessDeniedException("You can't add code not for you");
        }
        codeRepository.save(code);
        user.getCodes().add(code);
        userRepository.save(user);
        return ResponseEntity.of(Optional.of(code));
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/code/{codeId}")
    public ResponseEntity<Void> deleteUserCode(Authentication authentication, @PathVariable String codeId, @PathVariable String userId) {
        final User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        if (!(authentication.getName().equals(user.getUsername()) || authentication.getAuthorities().contains(ADMIN))){
            throw new AccessDeniedException("You can't delete not your code");
        }
        final Optional<Code> optionalCode = user.getCodes().stream().filter(code -> codeId.equals(code.getId())).findFirst();
        optionalCode.ifPresent(code -> {
            codeRepository.delete(code);
            user.getCodes().remove(code);
            userRepository.save(user);
        });
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}
