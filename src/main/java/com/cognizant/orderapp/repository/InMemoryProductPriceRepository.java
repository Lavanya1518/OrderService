package com.cognizant.orderapp.repository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;

@Repository
public class InMemoryProductPriceRepository implements ProductPriceRepository {

    // Sample catalog. Replace with a real data source (JPA repository, external
    // pricing service, etc.) in production.
    private final Map<String, BigDecimal> catalog = Map.of(
            "SKU-A", new BigDecimal("25.00"),
            "SKU-B", new BigDecimal("10.50"),
            "SKU-C", new BigDecimal("99.99")
    );

    @Override
    public BigDecimal getUnitPrice(String productId) {
        BigDecimal price = catalog.get(productId);
        if (price == null) {
            throw new NoSuchElementException("Unknown product: " + productId);
        }
        return price;
    }
}
