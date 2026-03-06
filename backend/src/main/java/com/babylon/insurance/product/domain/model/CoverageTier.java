package com.babylon.insurance.product.domain.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * A pricing tier within an insurance module.
 *
 * @param tierId               identifier (t1–t4)
 * @param label                display name (e.g., "Esencial", "Familiar")
 * @param sumAsegurada         insured capital in COP; {@code null} for modules without capital
 * @param prima                monthly premium in COP
 * @param includedCoverageIds  IDs of {@link DeathCoverage} items included at this tier;
 *                             empty for non-death modules
 */
public record CoverageTier(
        String tierId,
        String label,
        BigDecimal sumAsegurada,
        BigDecimal prima,
        List<String> includedCoverageIds
) {}
