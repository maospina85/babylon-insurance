package com.babylon.insurance.quote.adapter.in.web.dto;

import com.babylon.insurance.quote.domain.model.Quote;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response body for quote creation and retrieval endpoints.
 *
 * <p><strong>Privacy:</strong> encrypted holder fields are intentionally excluded
 * from this response to avoid exposing ciphertext to callers.
 *
 * @param quoteId            UUID of the quote
 * @param policyNumber       BLF-XXXXXXX formatted policy number
 * @param status             current lifecycle status
 * @param totalMonthlyPrima  computed monthly premium
 * @param paymentFrequency   billing period
 * @param coverageCount      number of selected modules
 * @param beneficiaryCount   number of assigned beneficiaries
 * @param assistanceCount    number of selected assistance services
 * @param createdAt          creation timestamp
 * @param appliedDiscountCode promotional code applied to {@code totalMonthlyPrima}, or {@code null} if none
 */
public record QuoteResponse(
        String quoteId,
        String policyNumber,
        String status,
        BigDecimal totalMonthlyPrima,
        String paymentFrequency,
        int coverageCount,
        int beneficiaryCount,
        int assistanceCount,
        Instant createdAt,
        String appliedDiscountCode
) {

    /**
     * Maps a domain {@link Quote} to its response representation.
     *
     * @param quote the domain object; must not be {@code null}
     * @return the response DTO
     */
    public static QuoteResponse from(Quote quote) {
        return new QuoteResponse(
                quote.quoteId(),
                quote.policyNumber(),
                quote.status().name(),
                quote.totalMonthlyPrima(),
                quote.paymentFrequency(),
                quote.selectedCoverages().size(),
                quote.beneficiaries().size(),
                quote.assistances().size(),
                quote.createdAt(),
                quote.appliedDiscountCode()
        );
    }
}
