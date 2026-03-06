package com.babylon.insurance.quote.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * MongoDB sub-document for a selected coverage within a {@link QuoteDocument}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectedCoverageDocument {

    private String moduleId;
    private String tierId;
    private BigDecimal prima;
    private BigDecimal sumAsegurada;
}
