package com.babylon.insurance.quote.adapter.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for a beneficiary in the create-quote request.
 *
 * @param name          full name; letters and spaces only
 * @param relation      family relationship
 * @param pct           percentage allocation [1, 100]
 * @param moduleId      owning module identifier
 * @param coverageType  human-readable coverage label
 */
public record BeneficiaryDto(

        @NotBlank
        @Size(min = 2, max = 100)
        @Pattern(
                regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$",
                message = "El nombre solo puede contener letras y espacios (2–100 caracteres)")
        String name,

        @NotBlank
        @Pattern(
                regexp = "^(Cónyuge|Hijo\\/a|Padre\\/Madre|Hermano\\/a|Otro)$",
                message = "Parentesco inválido")
        String relation,

        @NotNull
        @Min(value = 1, message = "El porcentaje mínimo es 1")
        @Max(value = 100, message = "El porcentaje máximo es 100")
        Integer pct,

        @NotBlank
        @Pattern(
                regexp = "^(death|disability|accidents|assistance)$",
                message = "moduleId debe ser: death, disability, accidents o assistance")
        String moduleId,

        @NotBlank
        String coverageType
) {}
