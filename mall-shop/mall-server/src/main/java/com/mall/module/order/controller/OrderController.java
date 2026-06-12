package com.mall.module.order.controller;

import com.mall.common.result.PageResult;
import com.mall.common.result.Result;
import com.mall.common.utils.SecurityUtils;
import com.mall.module.order.dto.CreateOrderRequest;
import com.mall.module.order.dto.OrderVO;
import com.mall.module.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderVO> create(@Valid @RequestBody CreateOrderRequest request) {
        return Result.success(orderService.createOrder(SecurityUtils.getUserId(), request));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(SecurityUtils.getUserId(), id));
    }

    @Operation(summary = "订单列表")
    @GetMapping
    public Result<PageResult<OrderVO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.listOrders(SecurityUtils.getUserId(), status, page, size));
    }

    @Operation(summary = "支付订单")
    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.payOrder(SecurityUtils.getUserId(), id);
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancelOrder(SecurityUtils.getUserId(), id);
        return Result.success();
    }

    @Operation(summary = "确认收货")
    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.confirmReceipt(SecurityUtils.getUserId(), id);
        return Result.success();
    }
}
