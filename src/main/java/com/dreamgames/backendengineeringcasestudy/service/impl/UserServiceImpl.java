package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.domain.User;
import com.dreamgames.backendengineeringcasestudy.exception.user.UserExistsException;
import com.dreamgames.backendengineeringcasestudy.exception.user.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.mapper.UserMapper;
import com.dreamgames.backendengineeringcasestudy.model.user.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.user.UserProgressResponse;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/**
 * Service implementation for managing users.
 * <p>
 * This service handles business logic related to user operations.
 * It depends on {@link UserRepository} to interact with the database and {@link UserMapper} to map between DTOs and entity objects.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Creates a new user based on the provided request data.
     * <p>
     * This method checks if the user already exists by username or email.
     * If the user exists, it throws a {@link UserExistsException}.
     * Otherwise, it saves the new user using the provided user details and returns the details of the created user.
     *
     * @param request the user creation request data
     * @return the details of the created user as a {@link UserProgressResponse}
     * @throws UserExistsException if a user already exists with the given username or email
     */
    @Override
    @Transactional
    public UserProgressResponse createUser(CreateUserRequest request) {
        // Check user existence
        userRepository.findByEmail(request.getEmail())
                .ifPresent(s -> {
                    throw new UserExistsException(String.format("Username already exists with given username: %s", request.getUsername()));
                });

        userRepository.findByUsername(request.getUsername())
                .ifPresent(s -> {
                    throw new UserExistsException(String.format("Username already exists with given username: %s", request.getUsername()));
                });
        User createdUser = userRepository.save(userMapper.CreateUserRequestToUser(request));
        log.info("[USER SERVICE] User created with given id: {}", createdUser.getUserId());
        return userMapper.UserToUserProgressResponse(createdUser);
    }


    /**
     * Updates the level and coins of the user specified by the given ID.
     * If the user is not found, throws UserNotFoundException.
     *
     * @param id the ID of the user to update
     * @return UserProgressResponse containing the updated user details
     * @throws UserNotFoundException if no user is found with the given ID
     */
    @Override
    @Transactional
    public UserProgressResponse updateLevelAndCoins(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("No user found with id: " + id));

        user.setLevel(user.getLevel() + 1);
        user.setCoins(user.getCoins() + 25);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("[USER SERVICE] User updated with given id: {}", id);

        return userMapper.UserToUserProgressResponse(user);
    }
}
