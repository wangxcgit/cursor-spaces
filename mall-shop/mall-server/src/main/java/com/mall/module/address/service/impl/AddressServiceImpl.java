package com.mall.module.address.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.module.address.dto.AddressRequest;
import com.mall.module.address.entity.Address;
import com.mall.module.address.mapper.AddressMapper;
import com.mall.module.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public List<Address> listByUserId(Long userId) {
        return addressMapper.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime));
    }

    @Override
    public Address getById(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }

    @Override
    @Transactional
    public void create(Long userId, AddressRequest request) {
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            clearDefault(userId);
        }
        Address address = new Address();
        BeanUtils.copyProperties(request, address);
        address.setUserId(userId);
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        addressMapper.insert(address);
    }

    @Override
    @Transactional
    public void update(Long userId, Long addressId, AddressRequest request) {
        Address address = getById(userId, addressId);
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            clearDefault(userId);
        }
        BeanUtils.copyProperties(request, address);
        addressMapper.updateById(address);
    }

    @Override
    public void delete(Long userId, Long addressId) {
        getById(userId, addressId);
        addressMapper.deleteById(addressId);
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long addressId) {
        getById(userId, addressId);
        clearDefault(userId);
        Address address = new Address();
        address.setId(addressId);
        address.setIsDefault(1);
        addressMapper.updateById(address);
    }

    private void clearDefault(Long userId) {
        addressMapper.update(null, new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 0));
    }
}
