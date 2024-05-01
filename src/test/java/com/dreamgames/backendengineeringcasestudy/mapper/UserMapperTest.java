package com.dreamgames.backendengineeringcasestudy.mapper;

import com.dreamgames.backendengineeringcasestudy.domain.User;
import com.dreamgames.backendengineeringcasestudy.model.user.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.user.UserProgressResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @InjectMocks
    UserMapper userMapper;

    @Test
    public void testCreateUserRequestToUser() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password123")
                .build();

        User user = userMapper.CreateUserRequestToUser(request);

        assertEquals("testUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(DigestUtils.sha256Hex("password123"), user.getPassword());
        assertEquals(UserMapper.COINS, user.getCoins());
        assertEquals(UserMapper.LEVEL, user.getLevel());
    }

    @Test
    public void testUserToUserProgressResponse() {
        User user = User.builder()
                .userId(1L)
                .username("testUser")
                .email("test@example.com")
                .country("TURKEY") // Adjust depending on your Country enum and User class configuration
                .coins(5000)
                .level(1)
                .build();

        UserProgressResponse response = userMapper.UserToUserProgressResponse(user);

        assertEquals(1L, response.getId());
        assertEquals(1, response.getLevel());
        assertEquals(5000, response.getCoins());
        assertEquals("TURKEY", response.getCountry());
    }
}