package com.babylon.insurance.quote.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * Request body for {@code POST /api/quotes}.
 *
 * <p>All fields are subject to Bean Validation before reaching the use case.
 * PII fields ({@code holderName}, {@code holderEmail}) will be encrypted
 * before persistence.
 *
 * @param holderName        policyholder full name
 * @param holderEmail       policyholder email address
 * @param holderPhone       policyholder phone number
 * @param holderDob         policyholder date of birth; must be in the past
 * @param selectedCoverages at least one coverage selection
 * @param beneficiaries     beneficiary assignments (may be empty)
 * @param assistances       IDs of selected free assistance services
 * @param paymentFrequency  billing period: mensual or anual
 * @param discountCode      optional promotional code; validated and applied server-side
 */
public record CreateQuoteRequest(

        @NotBlank
        @Size(min = 2, max = 100)
        @Pattern(
                regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$",
                message = "El nombre solo puede contener letras y espacios")
        String holderName,

        @NotBlank
        @Email(message = "Formato de correo inválido")
        String holderEmail,

        @NotBlank
        @Pattern(regexp = "^[+\\d\\s\\-]{7,20}$", message = "Formato de teléfono inválido")
        String holderPhone,

        @NotNull
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        LocalDate holderDob,

        @NotEmpty
        @Size(min = 1, max = 4)
        List<@Valid SelectedCoverageDto> selectedCoverages,

        List<@Valid BeneficiaryDto> beneficiaries,

        List<String> assistances,

        @NotBlank
        @Pattern(regexp = "^(mensual|anual)$", message = "Frecuencia debe ser mensual o anual")
        String paymentFrequency,

        @Size(max = 40)
        String discountCode
) {}
