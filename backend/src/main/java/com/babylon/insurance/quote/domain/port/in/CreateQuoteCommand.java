package com.babylon.insurance.quote.domain.port.in;

import com.babylon.insurance.quote.domain.model.Beneficiary;
import com.babylon.insurance.quote.domain.model.SelectedCoverage;

import java.util.List;

/**
 * Input command for the {@link CreateQuoteUseCase}.
 *
 * <p>This record carries all data required to calculate, build, and persist
 * a new insurance quote. Plain-text PII fields ({@code holderName},
 * {@code holderEmail}) will be encrypted by the use-case implementation
 * before any persistence occurs.
 *
 * @param holderName        policyholder full name (plain-text; will be encrypted)
 * @param holderEmail       policyholder email address (plain-text; will be encrypted)
 * @param holderPhone       policyholder phone number
 * @param holderDob         policyholder date of birth (YYYY-MM-DD)
 * @param selectedCoverages at least one coverage module + tier pair
 * @param beneficiaries     beneficiaries; total pct per module must equal 100
 * @param assistances       IDs of selected free assistance services
 * @param paymentFrequency  {@code mensual} or {@code anual}
 */
public record CreateQuoteCommand(
        String holderName,
        String holderEmail,
        String holderPhone,
        String holderDob,
        List<SelectedCoverage> selectedCoverages,
        List<Beneficiary> beneficiaries,
        List<String> assistances,
        String paymentFrequency
) {}
