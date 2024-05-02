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
import org.springframework.web.bind.annotation.*;

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
        log.info("[USER CONTROLLER] User created with given id: {}", userProgressResponse.getId());
        return new ResponseEntity<>(userProgressResponse, HttpStatus.CREATED);
    }


    /**
     * Handles the HTTP PUT request to update a user's level and coins after completing a level.
     * <p>
     * This endpoint is designed to increment the user's level by one and add 25 coins to their total
     * every time a level is completed. It accepts a user ID as a path variable and returns the updated
     * user progress data.
     *
     * @param id The ID of the user whose level and coins are to be updated. This should be a path variable
     *           included in the URL of the PUT request.
     * @return A {@link ResponseEntity} containing the {@link UserProgressResponse} with updated user data
     *         including the new level and coin total, wrapped in the HTTP response with a status of OK (200).
     */
    @PatchMapping("/{id}/level-up")
    public ResponseEntity<UserProgressResponse> updateLevelAndCoins(@PathVariable Long id) {
        UserProgressResponse userProgressResponse = userService.updateLevelAndCoins(id);
        log.info("[USER CONTROLLER] User level updated with given id: {}", userProgressResponse.getId());
        return new ResponseEntity<>(userProgressResponse, HttpStatus.OK);
    }
}
