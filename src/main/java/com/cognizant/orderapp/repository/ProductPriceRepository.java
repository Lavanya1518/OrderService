package com.cognizant.orderapp.repository;

import java.math.BigDecimal;

public interface ProductPriceRepository {

    /**
     * @return unit price of the given product.
     * @throws java.util.NoSuchElementException if the product is unknown.
     */
    BigDecimal getUnitPrice(String productId);
}
