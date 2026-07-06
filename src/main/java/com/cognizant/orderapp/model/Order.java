package com.cognizant.orderapp.model;

import java.math.BigDecimal;
import java.util.List;

public class Order {

    private final String orderId;
    private final List<OrderItem> items;
    private final DiscountType discountType;
    private final BigDecimal discountValue; // percentage (e.g. 10 = 10%) or flat amount

    public Order(String orderId, List<OrderItem> items, DiscountType discountType, BigDecimal discountValue) {
        this.orderId = orderId;
        this.items = items;
        this.discountType = discountType == null ? DiscountType.NONE : discountType;
        this.discountValue = discountValue == null ? BigDecimal.ZERO : discountValue;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }
}
