package com.mall.module.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.PageResult;
import com.mall.module.address.entity.Address;
import com.mall.module.address.service.AddressService;
import com.mall.module.cart.dto.CartItemVO;
import com.mall.module.cart.dto.CartVO;
import com.mall.module.cart.service.CartService;
import com.mall.module.order.dto.*;
import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.OrderItem;
import com.mall.module.order.enums.OrderStatus;
import com.mall.module.order.mapper.OrderItemMapper;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.order.service.OrderService;
import com.mall.module.product.entity.Product;
import com.mall.module.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final AddressService addressService;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, CreateOrderRequest request) {
        Address address = addressService.getById(userId, request.getAddressId());
        List<OrderItemRequest> itemRequests = resolveOrderItems(userId, request);
        if (itemRequests.isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : itemRequests) {
            Product product = productMapper.selectById(itemReq.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException("商品 " + itemReq.getProductId() + " 不存在或已下架");
            }
            if (product.getStock() < itemReq.getQuantity()) {
                throw new BusinessException("商品 " + product.getName() + " 库存不足");
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);

            product.setStock(product.getStock() - itemReq.getQuantity());
            productMapper.updateById(product);
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());
        order.setRemark(request.getRemark());
        orderMapper.insert(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            cartService.clearCart(userId);
        }

        return convertToVO(order, orderItems);
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        Order order = getOrderByUser(userId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        return convertToVO(order, items);
    }

    @Override
    public PageResult<OrderVO> listOrders(Long userId, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(convertPage(orderPage));
    }

    @Override
    @Transactional
    public void payOrder(Long userId, Long orderId) {
        Order order = getOrderByUser(userId, orderId);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException("订单状态不允许支付");
        }
        order.setStatus(OrderStatus.PENDING_SHIPMENT.getCode());
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setSales(product.getSales() + item.getQuantity());
                productMapper.updateById(product);
            }
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = getOrderByUser(userId, orderId);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException("仅待付款订单可取消");
        }
        restoreStock(orderId);
        order.setStatus(OrderStatus.CANCELLED.getCode());
        orderMapper.updateById(order);
    }

    @Override
    public void confirmReceipt(Long userId, Long orderId) {
        Order order = getOrderByUser(userId, orderId);
        if (order.getStatus() != OrderStatus.PENDING_RECEIPT.getCode()) {
            throw new BusinessException("订单状态不允许确认收货");
        }
        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void shipOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_SHIPMENT.getCode()) {
            throw new BusinessException("订单状态不允许发货");
        }
        order.setStatus(OrderStatus.PENDING_RECEIPT.getCode());
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public PageResult<OrderVO> adminListOrders(Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(convertPage(orderPage));
    }

    private List<OrderItemRequest> resolveOrderItems(Long userId, CreateOrderRequest request) {
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            return request.getItems();
        }
        CartVO cart = cartService.getCart(userId);
        return cart.getItems().stream().map(item -> {
            OrderItemRequest req = new OrderItemRequest();
            req.setProductId(item.getProductId());
            req.setQuantity(item.getQuantity());
            return req;
        }).toList();
    }

    private void restoreStock(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productMapper.updateById(product);
            }
        }
    }

    private Order getOrderByUser(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return timestamp + random;
    }

    private Page<OrderVO> convertPage(Page<Order> orderPage) {
        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderVO> voList = orderPage.getRecords().stream().map(order -> {
            List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            return convertToVO(order, items);
        }).toList();
        voPage.setRecords(voList);
        return voPage;
    }

    private OrderVO convertToVO(Order order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusDesc(OrderStatus.getDesc(order.getStatus()));
        vo.setItems(items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).toList());
        return vo;
    }
}
