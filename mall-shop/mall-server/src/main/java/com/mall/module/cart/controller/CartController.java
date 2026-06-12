package com.mall.module.cart.controller;

import com.mall.common.result.Result;
import com.mall.common.utils.SecurityUtils;
import com.mall.module.cart.dto.AddCartRequest;
import com.mall.module.cart.dto.CartVO;
import com.mall.module.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "购物车")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "获取购物车")
    @GetMapping
    public Result<CartVO> getCart() {
        return Result.success(cartService.getCart(SecurityUtils.getUserId()));
    }

    @Operation(summary = "添加商品到购物车")
    @PostMapping
    public Result<Void> addItem(@Valid @RequestBody AddCartRequest request) {
        cartService.addItem(SecurityUtils.getUserId(), request);
        return Result.success();
    }

    @Operation(summary = "更新商品数量")
    @PutMapping("/{productId}")
    public Result<Void> updateQuantity(@PathVariable Long productId, @RequestParam Integer quantity) {
        cartService.updateQuantity(SecurityUtils.getUserId(), productId, quantity);
        return Result.success();
    }

    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{productId}")
    public Result<Void> removeItem(@PathVariable Long productId) {
        cartService.removeItem(SecurityUtils.getUserId(), productId);
        return Result.success();
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping
    public Result<Void> clearCart() {
        cartService.clearCart(SecurityUtils.getUserId());
        return Result.success();
    }
}
