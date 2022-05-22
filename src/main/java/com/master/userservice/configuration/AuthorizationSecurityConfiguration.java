package com.master.userservice.configuration;

import com.google.common.primitives.Bytes;
import com.master.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class AuthorizationSecurityConfiguration {

    @Value("#{environment.JWT_SECRET}")
    private String secret;

    @Bean
    GithubUsersAuthenticationConverter githubUsersAuthenticationConverter(UserRepository repository){
        return new GithubUsersAuthenticationConverter(repository);
    }
    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity httpSecurity, GithubUsersAuthenticationConverter githubUsersAuthenticationConverter) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2Config -> oauth2Config
                        .jwt(jwtConfigurer ->
                                jwtConfigurer
                                        .jwtAuthenticationConverter(githubUsersAuthenticationConverter)
                                        .decoder(
                                                NimbusJwtDecoder.withSecretKey(
                                                                new SecretKeySpec(Bytes.ensureCapacity(secret.getBytes(), 64,0), "HMAC256")
                                                        )
                                                        .build())
                        )
                )
                .build();
    }

}
