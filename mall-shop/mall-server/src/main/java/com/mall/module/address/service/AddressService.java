package com.mall.module.address.service;

import com.mall.module.address.dto.AddressRequest;
import com.mall.module.address.entity.Address;

import java.util.List;

public interface AddressService {

    List<Address> listByUserId(Long userId);

    Address getById(Long userId, Long addressId);

    void create(Long userId, AddressRequest request);

    void update(Long userId, Long addressId, AddressRequest request);

    void delete(Long userId, Long addressId);

    void setDefault(Long userId, Long addressId);
}
