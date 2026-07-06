package com.cognizant.orderapp.controller;

import com.cognizant.orderapp.model.Order;
import com.cognizant.orderapp.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/orders/subtotal")
    public Map<String, BigDecimal> subtotal(@RequestBody Order order) {
        return Map.of("subtotal", orderService.calculateSubtotal(order));
    }

    @PostMapping("/api/orders/total")
    public Map<String, BigDecimal> total(@RequestBody Order order) {
        return Map.of("total", orderService.calculateTotal(order));
    }
}
