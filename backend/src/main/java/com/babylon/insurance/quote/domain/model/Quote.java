package com.babylon.insurance.quote.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Aggregate root representing an insurance quote / policy.
 *
 * <p><strong>Privacy:</strong> holder personal data ({@code holderNameEncrypted},
 * {@code holderEmailEncrypted}) is stored exclusively in AES-256-GCM encrypted form.
 * Plain-text values must never appear in logs, responses, or persistence.
 *
 * @param quoteId               UUID v4 of this quote
 * @param correlationId         traceability ID from the originating HTTP request
 * @param holderNameEncrypted   AES-256-GCM encrypted holder full name
 * @param holderEmailEncrypted  AES-256-GCM encrypted holder email address
 * @param holderPhone           holder phone number (plain-text; not PII-classified)
 * @param holderDob             holder date of birth in YYYY-MM-DD format
 * @param selectedCoverages     ordered list of coverage modules and tiers selected
 * @param beneficiaries         beneficiaries across all applicable modules
 * @param assistances           IDs of free assistance services included
 * @param totalMonthlyPrima     sum of all monthly premiums in COP
 * @param paymentFrequency      billing period: {@code mensual} or {@code anual}
 * @param policyNumber          human-readable policy number in BLF-XXXXXXX format
 * @param status                current lifecycle status
 * @param createdAt             UTC timestamp of quote creation
 * @param appliedDiscountCode   promotional code applied to {@code totalMonthlyPrima}, or {@code null} if none
 */
public record Quote(
        String quoteId,
        String correlationId,
        String holderNameEncrypted,
        String holderEmailEncrypted,
        String holderPhone,
        String holderDob,
        List<SelectedCoverage> selectedCoverages,
        List<Beneficiary> beneficiaries,
        List<String> assistances,
        BigDecimal totalMonthlyPrima,
        String paymentFrequency,
        String policyNumber,
        QuoteStatus status,
        Instant createdAt,
        String appliedDiscountCode
) {}
