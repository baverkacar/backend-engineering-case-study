package com.dreamgames.backendengineeringcasestudy.service.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.dreamgames.backendengineeringcasestudy.domain.GroupInfo;
import com.dreamgames.backendengineeringcasestudy.domain.TournamentGroups;
import com.dreamgames.backendengineeringcasestudy.domain.User;
import com.dreamgames.backendengineeringcasestudy.exception.UserExistsException;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.mapper.UserMapper;
import com.dreamgames.backendengineeringcasestudy.model.user.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.user.UserProgressResponse;
import com.dreamgames.backendengineeringcasestudy.repository.GroupInfoRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.RedisService;
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
    private GroupInfoRepository groupInfoRepository;

    @Mock
    private RedisService redisService;

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
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.updateLevelAndCoins(userId));

        assertEquals("No user found with id: " + userId, exception.getMessage());
    }

    @Test
    void testUpdateLevelAndCoinsWithoutActiveTournament() {
        // Arrange
        User user = new User(1L, "TestUser", "test@example.com", "pass", "TestCountry", 1, 5000, LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(redisService.getActiveTournamentId()).thenReturn(null);

        // Act
        userService.updateLevelAndCoins(user.getUserId());

        // Assert
        assertEquals(2, user.getLevel());
        assertEquals(5025, user.getCoins());
        verify(userRepository, times(1)).save(user);
        verify(redisService, times(1)).checkActiveTournament();
        verifyNoMoreInteractions(redisService);
    }

    @Test
    void testUpdateLevelAndCoinsWithActiveTournament() {
        // Arrange
        User user = new User(1L, "TestUser", "test@example.com", "pass", "TestCountry", 1, 5000, LocalDateTime.now(), LocalDateTime.now());
        GroupInfo groupInfo = new GroupInfo(1L, TournamentGroups.builder().build(), user, 0, true, LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(redisService.getActiveTournamentId()).thenReturn(1L);
        when(groupInfoRepository.findByTournamentIdAndUserId(any(), any())).thenReturn(Optional.of(groupInfo));
        when(userMapper.UserToUserProgressResponse(any())).thenReturn(new UserProgressResponse());

        // Act
        userService.updateLevelAndCoins(user.getUserId());

        // Assert
        assertEquals(2, user.getLevel());
        assertEquals(5025, user.getCoins());
        assertEquals(1, groupInfo.getScore());
        verify(groupInfoRepository, times(1)).save(groupInfo);
        verify(redisService, times(1)).incrementGroupLeaderBoardScore(any(), any(), anyInt());
        verify(redisService, times(1)).incrementCountryLeaderBoardScore(any(), anyInt(), any());
    }
}
