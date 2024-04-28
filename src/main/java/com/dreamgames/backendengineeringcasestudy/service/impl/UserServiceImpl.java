package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.domain.User;
import com.dreamgames.backendengineeringcasestudy.exception.user.UserExistsException;
import com.dreamgames.backendengineeringcasestudy.mapper.UserMapper;
import com.dreamgames.backendengineeringcasestudy.model.user.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.user.UserProgressResponse;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * Service implementation for managing users.
 * <p>
 * This service handles business logic related to user operations.
 * It depends on {@link UserRepository} to interact with the database and {@link UserMapper} to map between DTOs and entity objects.
 */
@Service
@RequiredArgsConstructor
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
        return userMapper.UserToUserProgressResponse(createdUser);
    }
}
