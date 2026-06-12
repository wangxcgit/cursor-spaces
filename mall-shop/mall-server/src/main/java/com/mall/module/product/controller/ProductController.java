package com.mall.module.product.controller;

import com.mall.common.result.PageResult;
import com.mall.common.result.Result;
import com.mall.module.product.dto.ProductQuery;
import com.mall.module.product.dto.ProductVO;
import com.mall.module.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品列表")
    @GetMapping
    public Result<PageResult<ProductVO>> list(ProductQuery query) {
        return Result.success(productService.listProducts(query));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }
}
