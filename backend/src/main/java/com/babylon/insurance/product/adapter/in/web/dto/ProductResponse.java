package com.babylon.insurance.product.adapter.in.web.dto;

import com.babylon.insurance.product.domain.model.Product;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for the full product catalogue.
 *
 * @param productCode  unique product code
 * @param version      semantic version string
 * @param status       lifecycle status
 * @param modules      available insurance modules
 * @param createdAt    catalogue version creation timestamp
 */
public record ProductResponse(
        String productCode,
        String version,
        String status,
        List<ModuleResponse> modules,
        Instant createdAt
) {

    /**
     * Maps a domain {@link Product} to its response representation.
     *
     * @param product the domain object; must not be {@code null}
     * @return the response DTO
     */
    public static ProductResponse from(Product product) {
        List<ModuleResponse> modules = product.modules().stream()
                .map(ModuleResponse::from)
                .toList();

        return new ProductResponse(
                product.productCode(),
                product.version(),
                product.status().name(),
                modules,
                product.createdAt()
        );
    }
}
