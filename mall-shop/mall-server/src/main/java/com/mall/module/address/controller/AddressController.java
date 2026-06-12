package com.mall.module.address.controller;

import com.mall.common.result.Result;
import com.mall.common.utils.SecurityUtils;
import com.mall.module.address.dto.AddressRequest;
import com.mall.module.address.entity.Address;
import com.mall.module.address.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收货地址")
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "地址列表")
    @GetMapping
    public Result<List<Address>> list() {
        return Result.success(addressService.listByUserId(SecurityUtils.getUserId()));
    }

    @Operation(summary = "地址详情")
    @GetMapping("/{id}")
    public Result<Address> detail(@PathVariable Long id) {
        return Result.success(addressService.getById(SecurityUtils.getUserId(), id));
    }

    @Operation(summary = "新增地址")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody AddressRequest request) {
        addressService.create(SecurityUtils.getUserId(), request);
        return Result.success();
    }

    @Operation(summary = "更新地址")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        addressService.update(SecurityUtils.getUserId(), id, request);
        return Result.success();
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(SecurityUtils.getUserId(), id);
        return Result.success();
    }

    @Operation(summary = "设为默认地址")
    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(SecurityUtils.getUserId(), id);
        return Result.success();
    }
}
