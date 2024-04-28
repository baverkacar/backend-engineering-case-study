package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.user.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.user.UserProgressResponse;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing user-related operations.
 * <p>
 * This controller provides API endpoints for creating new users.
 * It uses {@link UserService} to perform the actual user management operations.
 * */
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user with the provided user details.
     *
     * @param createUserRequest the user details required to create a new user
     * @return a {@link ResponseEntity} containing the created user's details and HTTP status code
     */
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserProgressResponse userProgressResponse = userService.createUser(createUserRequest);
        log.info("User Created Successfully: {}", userProgressResponse.toString());
        return new ResponseEntity<>(userProgressResponse, HttpStatus.CREATED);
    }
}
