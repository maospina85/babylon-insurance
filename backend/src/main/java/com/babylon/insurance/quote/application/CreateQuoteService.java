package com.babylon.insurance.quote.application;

import com.babylon.insurance.quote.domain.model.Quote;
import com.babylon.insurance.quote.domain.model.QuoteStatus;
import com.babylon.insurance.quote.domain.port.in.CreateQuoteCommand;
import com.babylon.insurance.quote.domain.port.in.CreateQuoteUseCase;
import com.babylon.insurance.quote.domain.port.out.QuoteRepositoryPort;
import com.babylon.insurance.shared.encryption.EncryptionPort;
import com.babylon.insurance.shared.logging.StructuredLogger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Application service that implements {@link CreateQuoteUseCase}.
 *
 * <p>Orchestrates premium calculation, PII encryption, policy-number
 * generation and persistence. Contains zero framework-routing logic.
 */
@Service
public class CreateQuoteService implements CreateQuoteUseCase {

    private final QuoteRepositoryPort repository;
    private final EncryptionPort encryption;
    private final PremiumCalculatorStrategy calculator;
    private final StructuredLogger log;

    public CreateQuoteService(QuoteRepositoryPort repository,
                              EncryptionPort encryption,
                              PremiumCalculatorStrategy calculator,
                              StructuredLogger log) {
        this.repository  = repository;
        this.encryption  = encryption;
        this.calculator  = calculator;
        this.log         = log;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Execution steps:
     * <ol>
     *   <li>Calculate total monthly premium.</li>
     *   <li>Generate unique quoteId (UUID) and policyNumber (BLF-XXXXXXX).</li>
     *   <li>Encrypt holderName and holderEmail with AES-256-GCM.</li>
     *   <li>Build immutable {@link Quote} with status {@link QuoteStatus#ISSUED}.</li>
     *   <li>Persist via {@link QuoteRepositoryPort}.</li>
     *   <li>Emit structured log event {@code quote_issued}.</li>
     * </ol>
     */
    @Override
    public Mono<Quote> execute(CreateQuoteCommand command, String correlationId) {
        return Mono.fromCallable(() -> buildQuote(command, correlationId))
                .flatMap(repository::save)
                .doOnSuccess(q -> log.info(
                        "quote_issued",
                        correlationId,
                        Map.of(
                                "policyNumber", q.policyNumber(),
                                "modules", q.selectedCoverages().size(),
                                "paymentFrequency", q.paymentFrequency()
                        )))
                .onErrorMap(ex -> !(ex instanceof RuntimeException),
                        ex -> new RuntimeException("Error al crear la cotización", ex));
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private Quote buildQuote(CreateQuoteCommand cmd, String correlationId) {
        BigDecimal totalPrima = calculator.calculate(cmd.selectedCoverages());
        String quoteId       = UUID.randomUUID().toString();
        String policyNumber  = generatePolicyNumber();
        String encName       = encryption.encrypt(cmd.holderName());
        String encEmail      = encryption.encrypt(cmd.holderEmail());

        return new Quote(
                quoteId,
                correlationId,
                encName,
                encEmail,
                cmd.holderPhone(),
                cmd.holderDob(),
                List.copyOf(cmd.selectedCoverages()),
                List.copyOf(cmd.beneficiaries()),
                List.copyOf(cmd.assistances()),
                totalPrima,
                cmd.paymentFrequency(),
                policyNumber,
                QuoteStatus.ISSUED,
                Instant.now()
        );
    }

    /**
     * Generates a policy number in {@code BLF-XXXXXXX} format using the
     * current timestamp in base-36 (uppercase), zero-padded to 7 characters.
     */
    private String generatePolicyNumber() {
        String base36 = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
        String padded = base36.length() >= 7 ? base36.substring(base36.length() - 7) : base36;
        return "BLF-" + padded;
    }
}
