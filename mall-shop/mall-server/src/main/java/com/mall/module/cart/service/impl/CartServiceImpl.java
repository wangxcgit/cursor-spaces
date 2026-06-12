package com.mall.module.cart.service.impl;

import com.mall.common.constant.Constants;
import com.mall.common.exception.BusinessException;
import com.mall.module.cart.dto.AddCartRequest;
import com.mall.module.cart.dto.CartItemVO;
import com.mall.module.cart.dto.CartVO;
import com.mall.module.cart.service.CartService;
import com.mall.module.product.entity.Product;
import com.mall.module.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductMapper productMapper;

    @Override
    public CartVO getCart(Long userId) {
        Map<Object, Object> cartMap = redisTemplate.opsForHash().entries(cartKey(userId));
        List<CartItemVO> items = new ArrayList<>();
        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<Object, Object> entry : cartMap.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());
            Integer quantity = Integer.valueOf(entry.getValue().toString());
            Product product = productMapper.selectById(productId);
            if (product == null || product.getStatus() != 1) {
                continue;
            }
            CartItemVO item = buildCartItem(product, quantity);
            items.add(item);
            totalQuantity += quantity;
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        CartVO cart = new CartVO();
        cart.setItems(items);
        cart.setTotalQuantity(totalQuantity);
        cart.setTotalAmount(totalAmount);
        return cart;
    }

    @Override
    public void addItem(Long userId, AddCartRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        String key = cartKey(userId);
        String productIdStr = request.getProductId().toString();
        Object existing = redisTemplate.opsForHash().get(key, productIdStr);
        int newQuantity = existing != null ?
                Integer.parseInt(existing.toString()) + request.getQuantity() : request.getQuantity();
        if (newQuantity > product.getStock()) {
            throw new BusinessException("库存不足");
        }
        redisTemplate.opsForHash().put(key, productIdStr, newQuantity);
    }

    @Override
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            removeItem(userId, productId);
            return;
        }
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (quantity > product.getStock()) {
            throw new BusinessException("库存不足");
        }
        redisTemplate.opsForHash().put(cartKey(userId), productId.toString(), quantity);
    }

    @Override
    public void removeItem(Long userId, Long productId) {
        redisTemplate.opsForHash().delete(cartKey(userId), productId.toString());
    }

    @Override
    public void clearCart(Long userId) {
        redisTemplate.delete(cartKey(userId));
    }

    private CartItemVO buildCartItem(Product product, Integer quantity) {
        CartItemVO item = new CartItemVO();
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setMainImage(product.getMainImage());
        item.setPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setStock(product.getStock());
        item.setSelected(true);
        item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    private String cartKey(Long userId) {
        return Constants.CART_KEY_PREFIX + userId;
    }
}
