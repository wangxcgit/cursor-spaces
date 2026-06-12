package com.mall.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.constant.Constants;
import com.mall.common.exception.BusinessException;
import com.mall.common.utils.JwtUtils;
import com.mall.module.user.dto.LoginRequest;
import com.mall.module.user.dto.LoginResponse;
import com.mall.module.user.dto.RegisterRequest;
import com.mall.module.user.dto.UserProfileVO;
import com.mall.module.user.entity.User;
import com.mall.module.user.mapper.UserMapper;
import com.mall.module.user.service.UserService;
import com.mall.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public void register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        if (request.getPhone() != null) {
            Long phoneCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, request.getPhone()));
            if (phoneCount > 0) {
                throw new BusinessException("手机号已被注册");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(Constants.ROLE_USER);
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String token = jwtUtils.generateToken(loginUser.getUserId(), loginUser.getUsername(), loginUser.getRole());
        return LoginResponse.builder()
                .token(token)
                .userId(loginUser.getUserId())
                .username(loginUser.getUsername())
                .nickname(loginUser.getUsername())
                .role(loginUser.getRole())
                .build();
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserProfileVO vo = new UserProfileVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public void updateProfile(Long userId, UserProfileVO profile) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setNickname(profile.getNickname());
        user.setPhone(profile.getPhone());
        user.setEmail(profile.getEmail());
        user.setAvatar(profile.getAvatar());
        userMapper.updateById(user);
    }
}
