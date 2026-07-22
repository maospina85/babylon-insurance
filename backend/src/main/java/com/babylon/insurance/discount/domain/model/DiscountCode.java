package com.babylon.insurance.discount.domain.model;

import java.math.BigDecimal;

/**
 * A promotional discount code applicable to a quote's total monthly premium.
 *
 * @param code       the code text, matched case-insensitively
 * @param percentOff percentage discount (0-100) applied to the total monthly premium
 * @param active     whether this code can currently be redeemed
 */
public record DiscountCode(
        String code,
        BigDecimal percentOff,
        boolean active
) {}
