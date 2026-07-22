package com.babylon.insurance.quote.application;

import com.babylon.insurance.discount.domain.model.DiscountCode;
import com.babylon.insurance.discount.domain.port.out.DiscountCodeRepositoryPort;
import com.babylon.insurance.quote.domain.model.Quote;
import com.babylon.insurance.quote.domain.model.QuoteStatus;
import com.babylon.insurance.quote.domain.port.in.CreateQuoteCommand;
import com.babylon.insurance.quote.domain.port.in.CreateQuoteUseCase;
import com.babylon.insurance.quote.domain.port.out.QuoteRepositoryPort;
import com.babylon.insurance.shared.encryption.EncryptionPort;
import com.babylon.insurance.shared.exception.QuoteValidationException;
import com.babylon.insurance.shared.logging.StructuredLogger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Application service that implements {@link CreateQuoteUseCase}.
 *
 * <p>Orchestrates premium calculation, discount-code application, PII
 * encryption, policy-number generation and persistence. Contains zero
 * framework-routing logic.
 */
@Service
public class CreateQuoteService implements CreateQuoteUseCase {

    private final QuoteRepositoryPort repository;
    private final EncryptionPort encryption;
    private final PremiumCalculatorStrategy calculator;
    private final DiscountCodeRepositoryPort discountCodeRepository;
    private final StructuredLogger log;

    public CreateQuoteService(QuoteRepositoryPort repository,
                              EncryptionPort encryption,
                              PremiumCalculatorStrategy calculator,
                              DiscountCodeRepositoryPort discountCodeRepository,
                              StructuredLogger log) {
        this.repository             = repository;
        this.encryption             = encryption;
        this.calculator             = calculator;
        this.discountCodeRepository = discountCodeRepository;
        this.log                    = log;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Execution steps:
     * <ol>
     *   <li>Resolve and validate the optional discount code.</li>
     *   <li>Calculate total monthly premium and apply the discount, if any.</li>
     *   <li>Generate unique quoteId (UUID) and policyNumber (BLF-XXXXXXX).</li>
     *   <li>Encrypt holderName and holderEmail with AES-256-GCM.</li>
     *   <li>Build immutable {@link Quote} with status {@link QuoteStatus#ISSUED}.</li>
     *   <li>Persist via {@link QuoteRepositoryPort}.</li>
     *   <li>Emit structured log event {@code quote_issued}.</li>
     * </ol>
     */
    @Override
    public Mono<Quote> execute(CreateQuoteCommand command, String correlationId) {
        return resolveDiscountPercent(command.discountCode())
                .map(percentOff -> buildQuote(command, correlationId, percentOff))
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

    /**
     * Resolves the percentage to discount from the total premium.
     *
     * <p>Returns {@link BigDecimal#ZERO} when no code was submitted. When a
     * code is submitted but does not match an active {@link DiscountCode},
     * fails with {@link QuoteValidationException} rather than silently
     * ignoring it.
     */
    private Mono<BigDecimal> resolveDiscountPercent(String discountCode) {
        if (discountCode == null || discountCode.isBlank()) {
            return Mono.just(BigDecimal.ZERO);
        }
        return discountCodeRepository.findByCode(discountCode)
                .filter(DiscountCode::active)
                .map(DiscountCode::percentOff)
                .switchIfEmpty(Mono.error(
                        new QuoteValidationException("Código de descuento inválido o inactivo")));
    }

    private Quote buildQuote(CreateQuoteCommand cmd, String correlationId, BigDecimal discountPercent) {
        BigDecimal totalPrima = applyDiscount(calculator.calculate(cmd.selectedCoverages()), discountPercent);
        String quoteId        = UUID.randomUUID().toString();
        String policyNumber   = generatePolicyNumber();
        String encName        = encryption.encrypt(cmd.holderName());
        String encEmail       = encryption.encrypt(cmd.holderEmail());
        boolean discountApplied = discountPercent.compareTo(BigDecimal.ZERO) > 0;

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
                Instant.now(),
                discountApplied ? cmd.discountCode() : null
        );
    }

    private BigDecimal applyDiscount(BigDecimal totalPrima, BigDecimal percentOff) {
        if (percentOff == null || percentOff.compareTo(BigDecimal.ZERO) <= 0) {
            return totalPrima;
        }
        BigDecimal reduction = totalPrima.multiply(percentOff)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        return totalPrima.subtract(reduction);
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
