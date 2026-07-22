package com.babylon.insurance.discount.domain.port.out;

import com.babylon.insurance.discount.domain.model.DiscountCode;
import reactor.core.publisher.Mono;

/**
 * Output port: persistence contract for {@link DiscountCode} lookups.
 */
public interface DiscountCodeRepositoryPort {

    /**
     * Finds a discount code by its exact code text (case-insensitive).
     *
     * @param code the code entered by the holder
     * @return the matching discount code, or empty if none exists
     */
    Mono<DiscountCode> findByCode(String code);

    /**
     * Persists a discount code (used during catalogue initialisation).
     *
     * @param discountCode the code to save
     * @return the saved discount code
     */
    Mono<DiscountCode> save(DiscountCode discountCode);
}
