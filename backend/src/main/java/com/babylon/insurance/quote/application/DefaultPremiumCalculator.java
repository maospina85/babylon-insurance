package com.babylon.insurance.quote.application;

import com.babylon.insurance.quote.domain.model.SelectedCoverage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Default implementation of {@link PremiumCalculatorStrategy}.
 *
 * <p>Sums the {@code prima} of each {@link SelectedCoverage}, treating
 * {@code null} prima values as {@link BigDecimal#ZERO}.
 */
@Component
public class DefaultPremiumCalculator implements PremiumCalculatorStrategy {

    /**
     * {@inheritDoc}
     *
     * <p>Uses {@link BigDecimal#add} to avoid floating-point accumulation errors.
     */
    @Override
    public BigDecimal calculate(List<SelectedCoverage> coverages) {
        if (coverages == null || coverages.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return coverages.stream()
                .map(c -> c.prima() != null ? c.prima() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
