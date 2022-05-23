package com.master.userservice.controller;

import com.master.userservice.model.Code;
import com.master.userservice.model.Submission;
import com.master.userservice.model.User;
import com.master.userservice.repository.SubmissionsRepository;
import com.master.userservice.repository.UserRepository;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.master.userservice.model.Roles.ADMIN;

@RestController
@RequestMapping(value = "/user/{userId}")
public class SubmissionsController {

    private static final String PLAGIARISM_SERVICE_NAME = "plagiarism";
    private final UserRepository userRepository;
    private final SubmissionsRepository submissionsRepository;
    private final EurekaClient discoveryClient;

    public SubmissionsController(UserRepository userRepository,
                                 SubmissionsRepository submissionsRepository, EurekaClient discoveryClient) {
        this.userRepository = userRepository;
        this.submissionsRepository = submissionsRepository;
        this.discoveryClient = discoveryClient;
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/submissions/{submissionId}", method = RequestMethod.GET)
    public ResponseEntity<Submission> getUserSubmission(Authentication authentication,
                                                        @PathVariable String submissionId,
                                                        @PathVariable String userId) {
        final User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        if (!(authentication.getName().equals(user.getUsername()) || authentication.getAuthorities().contains(ADMIN))) {
            throw new AccessDeniedException("You can't see submission you don't own");
        }
        return ResponseEntity.of(user.getSubmissions().stream().filter(submission -> submission.getId().equals(submissionId)).findFirst());
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/submissions", method = RequestMethod.GET)
    public ResponseEntity<List<Submission>> getUserSubmissions(Authentication authentication,
                                                               @PathVariable String userId) {
        final User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        if (!(authentication.getName().equals(user.getUsername()) || authentication.getAuthorities().contains(ADMIN))) {
            throw new AccessDeniedException("You can't see submission you don't own");
        }
        return ResponseEntity.of(Optional.of(user.getSubmissions()));
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/submissions", method = RequestMethod.POST)
    public ResponseEntity<Flux<Submission>> sendSubmissionForCheck(Authentication authentication,
                                                                   @PathVariable String userId,
                                                                   @RequestBody List<String> codes) {
        final User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        if (!(authentication.getName().equals(user.getUsername()) || authentication.getAuthorities().contains(ADMIN))) {
            throw new AccessDeniedException("You can't check not your code");
        }
        final List<Code> codeList =
                user.getCodes().stream().filter(userCode -> codes.contains(userCode.getId())).toList();

        final InstanceInfo plagiarismService = discoveryClient.getNextServerFromEureka(PLAGIARISM_SERVICE_NAME,
                false);
        final WebClient webClient = WebClient.create(plagiarismService.getHomePageUrl());
        return ResponseEntity.of(Optional.of(webClient.post()
                .uri(URI.create("/check-plagiarism"))
                .body(Flux.fromIterable(codeList), Code.class)
                .retrieve()
                .bodyToFlux(Submission.class)));
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/submissions/{submissionId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUserSubmission(Authentication authentication, @PathVariable String submissionId,
                                                     @PathVariable String userId) {
        final User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        if (!(authentication.getName().equals(user.getUsername()) || authentication.getAuthorities().contains(ADMIN))) {
            throw new AccessDeniedException("You can't delete not your submission");
        }
        user.getSubmissions().stream().filter(submission -> submissionId.equals(submission.getId())).findFirst().ifPresent(submission -> {
                    submissionsRepository.deleteById(submission.getId());
                    user.getSubmissions().remove(submission);
                    userRepository.save(user);
                }
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
