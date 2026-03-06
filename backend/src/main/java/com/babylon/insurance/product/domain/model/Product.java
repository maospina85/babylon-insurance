package com.babylon.insurance.product.domain.model;

import java.time.Instant;
import java.util.List;

/**
 * The product catalogue aggregate.
 *
 * <p>A product version bundles all insurable modules with their pricing tiers.
 * Only one product should be {@link ProductStatus#ACTIVE} at a time.
 *
 * @param productCode  unique product code (e.g., {@code BABYLON_LIFE})
 * @param version      semantic version string (e.g., {@code 2025.1})
 * @param status       current lifecycle status
 * @param modules      available insurance modules, ordered for display
 * @param createdAt    UTC timestamp of this product version creation
 */
public record Product(
        String productCode,
        String version,
        ProductStatus status,
        List<InsuranceModule> modules,
        Instant createdAt
) {}
