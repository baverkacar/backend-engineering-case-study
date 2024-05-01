package com.dreamgames.backendengineeringcasestudy.service.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.dreamgames.backendengineeringcasestudy.domain.User;
import com.dreamgames.backendengineeringcasestudy.exception.UserExistsException;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.mapper.UserMapper;
import com.dreamgames.backendengineeringcasestudy.model.user.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.user.UserProgressResponse;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

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

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.updateLevelAndCoins(userId));

        assertEquals("No user found with id: " + userId, exception.getMessage());
    }

    @Test
    void updateLevelAndCoins_Successful() {
        // Given
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setLevel(1);
        mockUser.setCoins(100);
        mockUser.setUpdatedAt(LocalDateTime.now());

        UserProgressResponse expectedResponse = new UserProgressResponse();
        expectedResponse.setLevel(2);
        expectedResponse.setCoins(125);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userMapper.UserToUserProgressResponse(mockUser)).thenReturn(expectedResponse);

        // When
        UserProgressResponse actualResponse = userService.updateLevelAndCoins(userId);

        // Then
        assertEquals(2, actualResponse.getLevel());
        assertEquals(125, actualResponse.getCoins());
        verify(userRepository).save(mockUser);
        verify(userMapper).UserToUserProgressResponse(mockUser);
    }
}
