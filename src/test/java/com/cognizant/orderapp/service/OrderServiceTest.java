package com.cognizant.orderapp.service;

import com.cognizant.orderapp.model.DiscountType;
import com.cognizant.orderapp.model.Order;
import com.cognizant.orderapp.model.OrderItem;
import com.cognizant.orderapp.repository.ProductPriceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService")
class OrderServiceTest {

    @Mock
    private ProductPriceRepository productPriceRepository;

    @InjectMocks
    private OrderService orderService;

    private static final String PRODUCT_A = "SKU-A";
    private static final String PRODUCT_B = "SKU-B";

    @Nested
    @DisplayName("calculateSubtotal")
    class CalculateSubtotal {

        @Test
        @DisplayName("sums price * quantity across multiple line items")
        void calculatesSubtotalForMultipleItems() {
            when(productPriceRepository.getUnitPrice(PRODUCT_A)).thenReturn(new BigDecimal("25.00"));
            when(productPriceRepository.getUnitPrice(PRODUCT_B)).thenReturn(new BigDecimal("10.50"));

            Order order = new Order("ORD-1", List.of(
                    new OrderItem(PRODUCT_A, 2),   // 50.00
                    new OrderItem(PRODUCT_B, 3)    // 31.50
            ), DiscountType.NONE, BigDecimal.ZERO);

            BigDecimal subtotal = orderService.calculateSubtotal(order);

            assertEquals(new BigDecimal("81.50"), subtotal);
            verify(productPriceRepository, times(1)).getUnitPrice(PRODUCT_A);
            verify(productPriceRepository, times(1)).getUnitPrice(PRODUCT_B);
        }

        @Test
        @DisplayName("returns zero for an order with no items")
        void returnsZeroForEmptyOrder() {
            Order order = new Order("ORD-2", Collections.emptyList(), DiscountType.NONE, BigDecimal.ZERO);

            BigDecimal subtotal = orderService.calculateSubtotal(order);

            assertEquals(new BigDecimal("0.00"), subtotal);
        }

        @Test
        @DisplayName("looks up the unit price once per line item, honoring quantity in the multiplication")
        void looksUpPriceExactlyOncePerLineItem() {
            when(productPriceRepository.getUnitPrice(PRODUCT_A)).thenReturn(new BigDecimal("9.99"));

            Order order = new Order("ORD-3", List.of(new OrderItem(PRODUCT_A, 5)), DiscountType.NONE, BigDecimal.ZERO);

            BigDecimal subtotal = orderService.calculateSubtotal(order);

            assertEquals(new BigDecimal("49.95"), subtotal);
            verify(productPriceRepository, times(1)).getUnitPrice(eq(PRODUCT_A));
        }
    }

    @Nested
    @DisplayName("calculateTotal - percentage discount")
    class PercentageDiscount {

        @Test
        @DisplayName("applies percentage discount when subtotal meets the minimum order value")
        void appliesPercentageDiscountWhenMinimumMet() {
            when(productPriceRepository.getUnitPrice(PRODUCT_A)).thenReturn(new BigDecimal("50.00"));

            // 3 * 50.00 = 150.00 subtotal, which meets the 100.00 minimum
            Order order = new Order("ORD-4", List.of(new OrderItem(PRODUCT_A, 3)),
                    DiscountType.PERCENTAGE, new BigDecimal("10"));

            BigDecimal total = orderService.calculateTotal(order);

            // 150.00 - 10% (15.00) = 135.00
            assertEquals(new BigDecimal("135.00"), total);
        }

        @Test
        @DisplayName("does NOT apply percentage discount when subtotal is below the minimum order value")
        void doesNotApplyPercentageDiscountBelowMinimum() {
            when(productPriceRepository.getUnitPrice(PRODUCT_A)).thenReturn(new BigDecimal("20.00"));

            // 2 * 20.00 = 40.00 subtotal, below the 100.00 minimum
            Order order = new Order("ORD-5", List.of(new OrderItem(PRODUCT_A, 2)),
                    DiscountType.PERCENTAGE, new BigDecimal("10"));

            BigDecimal total = orderService.calculateTotal(order);

            assertEquals(new BigDecimal("40.00"), total);
        }

        @Test
        @DisplayName("applies percentage discount when subtotal exactly equals the minimum order value")
        void appliesPercentageDiscountAtExactMinimum() {
            when(productPriceRepository.getUnitPrice(PRODUCT_A)).thenReturn(new BigDecimal("100.00"));

            Order order = new Order("ORD-6", List.of(new OrderItem(PRODUCT_A, 1)),
                    DiscountType.PERCENTAGE, new BigDecimal("20"));

            BigDecimal total = orderService.calculateTotal(order);

            // 100.00 - 20% (20.00) = 80.00
            assertEquals(new BigDecimal("80.00"), total);
        }
    }

    @Nested
    @DisplayName("calculateTotal - flat discount")
    class FlatDiscount {

        @Test
        @DisplayName("applies flat discount when subtotal meets the minimum order value")
        void appliesFlatDiscountWhenMinimumMet() {
            when(productPriceRepository.getUnitPrice(PRODUCT_A)).thenReturn(new BigDecimal("60.00"));

            // 2 * 60.00 = 120.00 subtotal, meets the 100.00 minimum
            Order order = new Order("ORD-7", List.of(new OrderItem(PRODUCT_A, 2)),
                    DiscountType.FLAT, new BigDecimal("15.00"));

            BigDecimal total = orderService.calculateTotal(order);

            assertEquals(new BigDecimal("105.00"), total);
        }

        @Test
        @DisplayName("does NOT apply flat discount when subtotal is below the minimum order value")
        void doesNotApplyFlatDiscountBelowMinimum() {
            when(productPriceRepository.getUnitPrice(PRODUCT_A)).thenReturn(new BigDecimal("30.00"));

            Order order = new Order("ORD-8", List.of(new OrderItem(PRODUCT_A, 1)),
                    DiscountType.FLAT, new BigDecimal("15.00"));

            BigDecimal total = orderService.calculateTotal(order);

            assertEquals(new BigDecimal("30.00"), total);
        }

        @Test
        @DisplayName("never returns a negative total when flat discount exceeds the subtotal")
        void flatDiscountDoesNotDriveTotalNegative() {
            when(productPriceRepository.getUnitPrice(PRODUCT_A)).thenReturn(new BigDecimal("110.00"));

            Order order = new Order("ORD-9", List.of(new OrderItem(PRODUCT_A, 1)),
                    DiscountType.FLAT, new BigDecimal("500.00"));

            BigDecimal total = orderService.calculateTotal(order);

            assertEquals(new BigDecimal("0.00"), total);
        }
    }

    @Nested
    @DisplayName("calculateTotal - no discount")
    class NoDiscount {

        @Test
        @DisplayName("returns the subtotal unchanged when discount type is NONE, even above the minimum")
        void returnsSubtotalWhenNoDiscountConfigured() {
            when(productPriceRepository.getUnitPrice(PRODUCT_A)).thenReturn(new BigDecimal("200.00"));

            Order order = new Order("ORD-10", List.of(new OrderItem(PRODUCT_A, 1)),
                    DiscountType.NONE, BigDecimal.ZERO);

            BigDecimal total = orderService.calculateTotal(order);

            assertEquals(new BigDecimal("200.00"), total);
        }
    }

    @Nested
    @DisplayName("calculateTotal - boundary and parameterized cases")
    class BoundaryCases {

        @ParameterizedTest(name = "unitPrice={0}, quantity={1}, discountPct={2} -> total={3}")
        @DisplayName("percentage discount across a range of subtotal values")
        @CsvSource({
                "99.99, 1, 10, 99.99",   // just below minimum -> no discount
                "100.00, 1, 10, 90.00",  // exactly at minimum -> discount applies
                "50.00, 4, 25, 150.00"   // well above minimum -> discount applies
        })
        void percentageDiscountBoundaryTable(String unitPrice, int quantity, String discountPct, String expectedTotal) {
            when(productPriceRepository.getUnitPrice(PRODUCT_A)).thenReturn(new BigDecimal(unitPrice));

            Order order = new Order("ORD-11", List.of(new OrderItem(PRODUCT_A, quantity)),
                    DiscountType.PERCENTAGE, new BigDecimal(discountPct));

            BigDecimal total = orderService.calculateTotal(order);

            assertEquals(new BigDecimal(expectedTotal), total);
        }
    }
}
