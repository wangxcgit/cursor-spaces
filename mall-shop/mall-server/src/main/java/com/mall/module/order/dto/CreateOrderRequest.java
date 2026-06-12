package com.mall.module.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    private String remark;

    /** 直接购买时传入，从购物车下单时为空 */
    private List<OrderItemRequest> items;
}
