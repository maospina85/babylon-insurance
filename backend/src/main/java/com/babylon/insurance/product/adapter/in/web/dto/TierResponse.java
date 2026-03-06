package com.babylon.insurance.product.adapter.in.web.dto;

import com.babylon.insurance.product.domain.model.CoverageTier;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for a single coverage tier within a module.
 *
 * @param tierId               tier identifier (t1–t4)
 * @param label                display name
 * @param sumAsegurada         insured capital in COP; null if not applicable
 * @param prima                monthly premium in COP
 * @param includedCoverageIds  coverage IDs included at this tier
 */
public record TierResponse(
        String tierId,
        String label,
        BigDecimal sumAsegurada,
        BigDecimal prima,
        List<String> includedCoverageIds
) {

    /**
     * Maps a domain {@link CoverageTier} to its response representation.
     *
     * @param tier the domain tier; must not be {@code null}
     * @return the response DTO
     */
    public static TierResponse from(CoverageTier tier) {
        return new TierResponse(
                tier.tierId(),
                tier.label(),
                tier.sumAsegurada(),
                tier.prima(),
                tier.includedCoverageIds()
        );
    }
}
