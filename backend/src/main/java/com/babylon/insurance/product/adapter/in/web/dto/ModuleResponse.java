package com.babylon.insurance.product.adapter.in.web.dto;

import com.babylon.insurance.product.domain.model.DeathCoverage;
import com.babylon.insurance.product.domain.model.InsuranceModule;

import java.util.List;

/**
 * Response DTO for an insurance module within the product catalogue.
 *
 * @param moduleId         module identifier
 * @param labelKey         display label
 * @param icon             emoji / icon token
 * @param colorToken       design-system color token
 * @param hasBeneficiaries whether this module requires beneficiary assignment
 * @param beneficiaryType  human-readable beneficiary section label; null if none
 * @param tiers            available pricing tiers
 * @param coverages        named coverages (non-empty only for the death module)
 */
public record ModuleResponse(
        String moduleId,
        String labelKey,
        String icon,
        String colorToken,
        String description,
        boolean hasBeneficiaries,
        String beneficiaryType,
        List<TierResponse> tiers,
        List<CoverageItemResponse> coverages
) {

    /**
     * Nested DTO for a single named coverage item.
     *
     * @param id          coverage identifier
     * @param label       short display name
     * @param description full description
     */
    public record CoverageItemResponse(String id, String label, String description) {

        /** Maps from domain {@link DeathCoverage}. */
        public static CoverageItemResponse from(DeathCoverage dc) {
            return new CoverageItemResponse(dc.id(), dc.label(), dc.description());
        }
    }

    /**
     * Maps a domain {@link InsuranceModule} to its response representation.
     *
     * @param module the domain module; must not be {@code null}
     * @return the response DTO
     */
    public static ModuleResponse from(InsuranceModule module) {
        List<TierResponse> tiers = module.tiers().stream().map(TierResponse::from).toList();
        List<CoverageItemResponse> coverages = module.coverages() == null
                ? List.of()
                : module.coverages().stream().map(CoverageItemResponse::from).toList();

        return new ModuleResponse(
                module.moduleId(),
                module.labelKey(),
                module.icon(),
                module.colorToken(),
                module.description(),
                module.hasBeneficiaries(),
                module.beneficiaryType(),
                tiers,
                coverages
        );
    }
}
