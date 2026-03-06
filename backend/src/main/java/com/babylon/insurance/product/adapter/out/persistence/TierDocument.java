package com.babylon.insurance.product.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * MongoDB sub-document for a coverage tier within a {@link ModuleDocument}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TierDocument {

    private String tierId;
    private String label;
    private BigDecimal sumAsegurada;
    private BigDecimal prima;
    private List<String> includedCoverageIds;
}
