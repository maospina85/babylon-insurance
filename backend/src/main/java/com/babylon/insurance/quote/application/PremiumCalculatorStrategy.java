package com.babylon.insurance.quote.application;

import com.babylon.insurance.quote.domain.model.SelectedCoverage;

import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy for computing the total monthly premium from a list of selected coverages.
 *
 * <p>Implementations must use {@link BigDecimal} arithmetic to avoid floating-point errors.
 * A {@code null} or missing prima on any coverage must be treated as {@link BigDecimal#ZERO}.
 */
public interface PremiumCalculatorStrategy {

    /**
     * Calculates the total monthly premium for the given coverages.
     *
     * @param coverages list of selected coverage modules and tiers; may be empty
     * @return the sum of all prima values; never {@code null}; minimum {@link BigDecimal#ZERO}
     */
    BigDecimal calculate(List<SelectedCoverage> coverages);
}
