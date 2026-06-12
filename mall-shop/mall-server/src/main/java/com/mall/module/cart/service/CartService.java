package com.mall.module.cart.service;

import com.mall.module.cart.dto.AddCartRequest;
import com.mall.module.cart.dto.CartVO;

public interface CartService {

    CartVO getCart(Long userId);

    void addItem(Long userId, AddCartRequest request);

    void updateQuantity(Long userId, Long productId, Integer quantity);

    void removeItem(Long userId, Long productId);

    void clearCart(Long userId);
}
