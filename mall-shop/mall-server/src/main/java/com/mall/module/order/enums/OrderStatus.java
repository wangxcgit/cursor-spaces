package com.mall.module.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PENDING_PAYMENT(0, "待付款"),
    PENDING_SHIPMENT(1, "待发货"),
    PENDING_RECEIPT(2, "待收货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final int code;
    private final String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(int code) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status.desc;
            }
        }
        return "未知";
    }
}
