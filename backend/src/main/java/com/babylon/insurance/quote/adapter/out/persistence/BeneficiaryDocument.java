package com.babylon.insurance.quote.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB sub-document for a beneficiary within a {@link QuoteDocument}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryDocument {

    private String id;
    private String name;
    private String relation;
    private Integer pct;
    private String moduleId;
    private String coverageType;
}
