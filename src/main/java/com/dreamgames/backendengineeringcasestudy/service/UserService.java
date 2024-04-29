package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.user.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.user.UserProgressResponse;

public interface UserService {
    UserProgressResponse createUser(CreateUserRequest request);
    UserProgressResponse updateLevelAndCoins(Long id);
}
