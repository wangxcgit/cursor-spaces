package com.mall.module.order.service;

import com.mall.common.result.PageResult;
import com.mall.module.order.dto.CreateOrderRequest;
import com.mall.module.order.dto.OrderVO;

public interface OrderService {

    OrderVO createOrder(Long userId, CreateOrderRequest request);

    OrderVO getOrderDetail(Long userId, Long orderId);

    PageResult<OrderVO> listOrders(Long userId, Integer status, Integer page, Integer size);

    void payOrder(Long userId, Long orderId);

    void cancelOrder(Long userId, Long orderId);

    void confirmReceipt(Long userId, Long orderId);

    void shipOrder(Long orderId);

    PageResult<OrderVO> adminListOrders(Integer status, Integer page, Integer size);
}
