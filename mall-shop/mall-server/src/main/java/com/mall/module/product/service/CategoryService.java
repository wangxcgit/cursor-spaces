package com.mall.module.product.service;

import com.mall.module.product.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> listAll();

    Category getById(Long id);

    void create(Category category);

    void update(Category category);

    void delete(Long id);
}
