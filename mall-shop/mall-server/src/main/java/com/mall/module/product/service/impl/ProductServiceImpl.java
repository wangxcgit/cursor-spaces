package com.mall.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.PageResult;
import com.mall.module.product.dto.ProductQuery;
import com.mall.module.product.dto.ProductVO;
import com.mall.module.product.entity.Category;
import com.mall.module.product.entity.Product;
import com.mall.module.product.mapper.CategoryMapper;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public PageResult<ProductVO> listProducts(ProductQuery query) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .eq(query.getCategoryId() != null, Product::getCategoryId, query.getCategoryId())
                .like(StringUtils.hasText(query.getKeyword()), Product::getName, query.getKeyword())
                .orderByDesc(Product::getSales);

        Page<Product> page = productMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);
        return PageResult.of(convertToVO(page));
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        Category category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        return vo;
    }

    @Override
    public void createProduct(Product product) {
        product.setSales(0);
        product.setStatus(1);
        productMapper.insert(product);
    }

    @Override
    public void updateProduct(Product product) {
        productMapper.updateById(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        productMapper.updateById(product);
    }

    private Page<ProductVO> convertToVO(Page<Product> page) {
        List<Long> categoryIds = page.getRecords().stream()
                .map(Product::getCategoryId)
                .distinct()
                .toList();
        Map<Long, String> categoryMap = categoryIds.isEmpty() ? Map.of() :
                categoryMapper.selectBatchIds(categoryIds).stream()
                        .collect(Collectors.toMap(Category::getId, Category::getName));

        Page<ProductVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ProductVO> voList = page.getRecords().stream().map(product -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(product, vo);
            vo.setCategoryName(categoryMap.get(product.getCategoryId()));
            return vo;
        }).toList();
        voPage.setRecords(voList);
        return voPage;
    }
}
