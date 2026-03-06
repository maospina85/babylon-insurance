package com.babylon.insurance.product.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MongoDB sub-document for an insurance module within a {@link ProductDocument}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDocument {

    private String moduleId;
    private String labelKey;
    private String icon;
    private String colorToken;
    private String description;
    private boolean hasBeneficiaries;
    private String beneficiaryType;
    private List<TierDocument> tiers;
    private List<CoverageDocument> coverages;

    /**
     * Embedded document for a named death coverage item.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoverageDocument {
        private String id;
        private String label;
        private String description;
    }
}
