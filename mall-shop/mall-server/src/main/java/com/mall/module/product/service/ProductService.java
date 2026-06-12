package com.mall.module.product.service;

import com.mall.common.result.PageResult;
import com.mall.module.product.dto.ProductQuery;
import com.mall.module.product.dto.ProductVO;
import com.mall.module.product.entity.Product;

public interface ProductService {

    PageResult<ProductVO> listProducts(ProductQuery query);

    ProductVO getProductDetail(Long id);

    void createProduct(Product product);

    void updateProduct(Product product);

    void deleteProduct(Long id);

    void updateStatus(Long id, Integer status);
}
