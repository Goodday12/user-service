package com.master.userservice.configuration;

import com.master.userservice.model.Roles;
import com.master.userservice.model.User;
import com.master.userservice.repository.UserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collections;

public class GithubUsersAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;

    public GithubUsersAuthenticationConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public final AbstractAuthenticationToken convert(Jwt jwt) {
        String userClaim = "user";
        String principalClaimValue = jwt.getClaimAsString(userClaim);
        final User user = userRepository.findUserByUsername(principalClaimValue).orElseGet(() -> userRepository.save(
                User.builder().username(principalClaimValue).roles(Collections.singletonList(Roles.USER)).build()
        ));
        return new JwtAuthenticationToken(jwt, user.getRoles(), principalClaimValue);
    }

}
