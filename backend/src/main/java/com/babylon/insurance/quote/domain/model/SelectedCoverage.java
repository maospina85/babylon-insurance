package com.babylon.insurance.quote.domain.model;

import java.math.BigDecimal;

/**
 * Represents a single coverage module and tier chosen by the policyholder.
 *
 * <p>This is a pure value object — no framework dependencies.
 *
 * @param moduleId      identifier of the insurance module (e.g. {@code death}, {@code disability}, {@code accidents})
 * @param tierId        identifier of the selected tier (t1–t4)
 * @param prima         monthly premium amount for this coverage; must be {@code >= 0}
 * @param sumAsegurada  insured capital; {@code null} for modules without a capital component
 */
public record SelectedCoverage(
        String moduleId,
        String tierId,
        BigDecimal prima,
        BigDecimal sumAsegurada
) {}
