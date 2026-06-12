package com.mall.module.admin.controller;

import com.mall.common.result.PageResult;
import com.mall.common.result.Result;
import com.mall.module.order.dto.OrderVO;
import com.mall.module.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-订单管理")
@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "订单列表")
    @GetMapping
    public Result<PageResult<OrderVO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.adminListOrders(status, page, size));
    }

    @Operation(summary = "订单发货")
    @PostMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id) {
        orderService.shipOrder(id);
        return Result.success();
    }
}
