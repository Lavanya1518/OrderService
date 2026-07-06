package com.cognizant.orderapp.service;

import com.cognizant.orderapp.model.DiscountType;
import com.cognizant.orderapp.model.Order;
import com.cognizant.orderapp.model.OrderItem;
import com.cognizant.orderapp.repository.ProductPriceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class OrderService {

    /** Discounts (percentage or flat) only apply once the subtotal reaches this amount. */
    static final BigDecimal MINIMUM_ORDER_VALUE_FOR_DISCOUNT = new BigDecimal("100.00");

    private final ProductPriceRepository productPriceRepository;

    public OrderService(ProductPriceRepository productPriceRepository) {
        this.productPriceRepository = productPriceRepository;
    }

    /**
     * Sums unit price * quantity across all line items. Does not apply any discount.
     */
    public BigDecimal calculateSubtotal(Order order) {
        List<OrderItem> items = order.getItems();
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : items) {
            BigDecimal unitPrice = productPriceRepository.getUnitPrice(item.getProductId());
            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the subtotal and, if it meets {@link #MINIMUM_ORDER_VALUE_FOR_DISCOUNT},
     * applies the order's configured discount (percentage or flat). The result is never
     * allowed to drop below zero.
     */
    public BigDecimal calculateTotal(Order order) {
        BigDecimal subtotal = calculateSubtotal(order);

        if (subtotal.compareTo(MINIMUM_ORDER_VALUE_FOR_DISCOUNT) < 0) {
            return subtotal;
        }

        BigDecimal total;
        switch (order.getDiscountType()) {
            case PERCENTAGE:
                BigDecimal discountAmount = subtotal
                        .multiply(order.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                total = subtotal.subtract(discountAmount);
                break;
            case FLAT:
                total = subtotal.subtract(order.getDiscountValue());
                break;
            case NONE:
            default:
                total = subtotal;
        }

        return total.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}
