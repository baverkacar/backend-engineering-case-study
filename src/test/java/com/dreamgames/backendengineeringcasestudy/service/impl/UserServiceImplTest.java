package com.dreamgames.backendengineeringcasestudy.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.dreamgames.backendengineeringcasestudy.domain.User;
import com.dreamgames.backendengineeringcasestudy.exception.user.UserExistsException;
import com.dreamgames.backendengineeringcasestudy.mapper.UserMapper;
import com.dreamgames.backendengineeringcasestudy.model.user.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.user.UserProgressResponse;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // This is optional if you use @InjectMocks, which initializes the service with mocks
    }

    @Test
    void shouldThrowUserExistsExceptionWhenEmailExists() {
        // Given
        CreateUserRequest request = new CreateUserRequest("testUser", "test@example.com", "password123");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        // When & Then
        assertThrows(UserExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowUserExistsExceptionWhenUsernameExists() {
        // Given
        CreateUserRequest request = new CreateUserRequest("testUser", "test@example.com", "password123");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        // When & Then
        assertThrows(UserExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCreateUserWhenNoUserExists() {
        // Given
        CreateUserRequest request = new CreateUserRequest("testUser", "test@example.com", "password123");
        User mockUser = new User();
        UserProgressResponse expectedResponse = new UserProgressResponse(1L, 1, 5000, "TURKEY");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.CreateUserRequestToUser(any(CreateUserRequest.class))).thenReturn(mockUser);
        when(userMapper.UserToUserProgressResponse(any(User.class))).thenReturn(expectedResponse);

        // When
        UserProgressResponse actualResponse = userService.createUser(request);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(userRepository).save(any(User.class));
    }
}
