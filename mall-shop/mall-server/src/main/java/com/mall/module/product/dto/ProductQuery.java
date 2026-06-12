package com.mall.module.product.dto;

import lombok.Data;

@Data
public class ProductQuery {

    private Long categoryId;
    private String keyword;
    private Integer page = 1;
    private Integer size = 10;
}
