package com.mall.module.user.controller;

import com.mall.common.result.Result;
import com.mall.common.utils.SecurityUtils;
import com.mall.module.user.dto.UserProfileVO;
import com.mall.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile() {
        return Result.success(userService.getProfile(SecurityUtils.getUserId()));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserProfileVO profile) {
        userService.updateProfile(SecurityUtils.getUserId(), profile);
        return Result.success();
    }
}
