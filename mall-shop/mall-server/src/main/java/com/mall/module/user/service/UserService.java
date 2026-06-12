package com.mall.module.user.service;

import com.mall.module.user.dto.LoginRequest;
import com.mall.module.user.dto.LoginResponse;
import com.mall.module.user.dto.RegisterRequest;
import com.mall.module.user.dto.UserProfileVO;

public interface UserService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserProfileVO getProfile(Long userId);

    void updateProfile(Long userId, UserProfileVO profile);
}
