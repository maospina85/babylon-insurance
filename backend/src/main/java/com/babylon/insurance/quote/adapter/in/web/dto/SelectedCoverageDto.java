package com.babylon.insurance.quote.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/**
 * DTO for a single selected coverage in the create-quote request.
 *
 * @param moduleId      insurance module identifier
 * @param tierId        selected tier identifier
 * @param prima         monthly premium; must be {@code >= 0}
 * @param sumAsegurada  insured capital; nullable for assistance-type modules
 */
public record SelectedCoverageDto(

        @NotBlank
        @Pattern(
                regexp = "^(death|disability|accidents|assistance)$",
                message = "moduleId debe ser: death, disability, accidents o assistance")
        String moduleId,

        @NotBlank
        @Pattern(regexp = "^t[1-4]$", message = "tierId debe ser t1, t2, t3 o t4")
        String tierId,

        @NotNull
        @DecimalMin(value = "0", message = "La prima debe ser mayor o igual a 0")
        BigDecimal prima,

        BigDecimal sumAsegurada
) {}
