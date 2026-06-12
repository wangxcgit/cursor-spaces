package com.mall.module.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {

    private Long productId;
    private String productName;
    private String mainImage;
    private BigDecimal price;
    private Integer quantity;
    private Integer stock;
    private Boolean selected;
    private BigDecimal subtotal;
}
