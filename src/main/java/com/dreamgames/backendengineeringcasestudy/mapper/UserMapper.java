package com.dreamgames.backendengineeringcasestudy.mapper;

import com.dreamgames.backendengineeringcasestudy.domain.User;
import com.dreamgames.backendengineeringcasestudy.enums.Country;
import com.dreamgames.backendengineeringcasestudy.model.user.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.user.UserProgressResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * UserMapper is a utility component that provides methods for mapping between
 * various user-related data models.
 */

@Component
@RequiredArgsConstructor
public class UserMapper {
    public static final int LEVEL = 1;
    public static final int COINS = 5000;

    public User CreateUserRequestToUser(CreateUserRequest createUserRequest) {
        return User.builder()
                .username(createUserRequest.getUsername())
                .email(createUserRequest.getEmail())
                .password(DigestUtils.sha256Hex(createUserRequest.getPassword()))
                .country(Country.assignRandomCountry())
                .coins(COINS)
                .level(LEVEL)
                .build();
    }

    public UserProgressResponse UserToUserProgressResponse(User user) {
        return UserProgressResponse.builder()
                .id(user.getId())
                .level(user.getLevel())
                .coins(user.getCoins())
                .country(user.getCountry())
                .build();
    }
}
