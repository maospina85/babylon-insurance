package com.babylon.insurance.quote.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * MongoDB document for persisting insurance quotes.
 *
 * <p><strong>Privacy:</strong> {@code holderNameEncrypted} and
 * {@code holderEmailEncrypted} are stored exclusively in AES-256-GCM form.
 * Plain-text PII must never appear in this collection.
 */
@Document(collection = "quotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteDocument {

    @Id
    private String id;

    private String quoteId;
    private String correlationId;

    /** AES-256-GCM encrypted holder full name — never stored in clear. */
    private String holderNameEncrypted;

    /** AES-256-GCM encrypted holder email — never stored in clear. */
    private String holderEmailEncrypted;

    private String holderPhone;
    private String holderDob;

    private List<SelectedCoverageDocument> selectedCoverages;
    private List<BeneficiaryDocument> beneficiaries;
    private List<String> assistances;

    private BigDecimal totalMonthlyPrima;
    private String paymentFrequency;

    @Indexed(unique = true)
    private String policyNumber;

    private String status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
