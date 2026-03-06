package com.babylon.insurance.product.domain.model;

/**
 * A single named coverage included in the Death (Vida/Fallecimiento) module.
 *
 * @param id          unique coverage identifier (e.g., {@code death_main}, {@code funeral})
 * @param label       short human-readable name shown in the UI
 * @param description longer explanation of what this coverage provides
 */
public record DeathCoverage(
        String id,
        String label,
        String description
) {}
