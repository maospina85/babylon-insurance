package com.babylon.insurance.product.domain.model;

import java.util.List;

/**
 * An insurable module offered within a {@link Product}.
 *
 * <p>Each module has a set of pricing tiers and, for the {@code death} module,
 * a list of named coverages that vary by tier.
 *
 * @param moduleId          unique identifier (death, disability, accidents)
 * @param labelKey          display label key (e.g., "Vida / Fallecimiento")
 * @param icon              emoji or icon token for the UI
 * @param colorToken        design-system color token for this module
 * @param hasBeneficiaries  whether this module requires beneficiary assignment
 * @param beneficiaryType   human-readable label for the beneficiary section; {@code null} if none
 * @param tiers             available pricing tiers, ordered from lowest to highest
 * @param coverages         named coverages for the death module; empty for other modules
 */
public record InsuranceModule(
        String moduleId,
        String labelKey,
        String icon,
        String colorToken,
        String description,
        boolean hasBeneficiaries,
        String beneficiaryType,
        List<CoverageTier> tiers,
        List<DeathCoverage> coverages
) {}
