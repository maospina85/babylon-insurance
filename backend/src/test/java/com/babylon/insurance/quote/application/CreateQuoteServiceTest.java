package com.babylon.insurance.quote.application;

import com.babylon.insurance.discount.domain.model.DiscountCode;
import com.babylon.insurance.discount.domain.port.out.DiscountCodeRepositoryPort;
import com.babylon.insurance.quote.domain.model.Beneficiary;
import com.babylon.insurance.quote.domain.model.QuoteStatus;
import com.babylon.insurance.quote.domain.model.SelectedCoverage;
import com.babylon.insurance.quote.domain.port.in.CreateQuoteCommand;
import com.babylon.insurance.quote.domain.port.out.QuoteRepositoryPort;
import com.babylon.insurance.shared.encryption.EncryptionPort;
import com.babylon.insurance.shared.exception.QuoteValidationException;
import com.babylon.insurance.shared.logging.StructuredLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateQuoteService")
class CreateQuoteServiceTest {

    @Mock private QuoteRepositoryPort repository;
    @Mock private EncryptionPort encryption;
    @Mock private DiscountCodeRepositoryPort discountCodeRepository;
    @Mock private StructuredLogger log;

    private CreateQuoteService service;
    private static final String CORRELATION_ID = "test-correlation-id";

    @BeforeEach
    void setUp() {
        service = new CreateQuoteService(
                repository, encryption, new DefaultPremiumCalculator(), discountCodeRepository, log);
        lenient().when(encryption.encrypt(anyString())).thenReturn("ENCRYPTED_VALUE");
        // lenient + null-safe: Mockito evaluates the answer with null during re-stubbing;
        // Mono.just(null) would throw NPE, so guard explicitly.
        lenient().when(repository.save(any())).thenAnswer(inv -> {
            com.babylon.insurance.quote.domain.model.Quote q = inv.getArgument(0);
            return q != null ? Mono.just(q) : Mono.empty();
        });
        lenient().doNothing().when(log).info(anyString(), anyString(), anyMap());
    }

    @Test
    @DisplayName("dado un comando válido cuando se ejecuta entonces retorna Quote con status ISSUED")
    void givenValidCommand_whenExecute_thenReturnQuoteWithIssuedStatus() {
        StepVerifier.create(service.execute(buildCommand(), CORRELATION_ID))
                .assertNext(quote -> assertThat(quote.status()).isEqualTo(QuoteStatus.ISSUED))
                .verifyComplete();
    }

    @Test
    @DisplayName("dado un comando válido cuando se ejecuta entonces el policyNumber tiene formato BLF-")
    void givenValidCommand_whenExecute_thenPolicyNumberStartsWithBLF() {
        StepVerifier.create(service.execute(buildCommand(), CORRELATION_ID))
                .assertNext(quote -> assertThat(quote.policyNumber()).startsWith("BLF-"))
                .verifyComplete();
    }

    @Test
    @DisplayName("dado un comando válido cuando se ejecuta entonces holderName está cifrado")
    void givenValidCommand_whenExecute_thenHolderNameIsEncrypted() {
        StepVerifier.create(service.execute(buildCommand(), CORRELATION_ID))
                .assertNext(quote -> {
                    assertThat(quote.holderNameEncrypted()).isEqualTo("ENCRYPTED_VALUE");
                    assertThat(quote.holderEmailEncrypted()).isEqualTo("ENCRYPTED_VALUE");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("dado un comando válido cuando se ejecuta entonces la prima total es la suma de coberturas")
    void givenValidCommand_whenExecute_thenTotalPrimaIsSum() {
        StepVerifier.create(service.execute(buildCommand(), CORRELATION_ID))
                .assertNext(quote ->
                        assertThat(quote.totalMonthlyPrima())
                                .isEqualByComparingTo(BigDecimal.valueOf(12_500)))
                .verifyComplete();
    }

    @Test
    @DisplayName("dado que el repositorio falla cuando se ejecuta entonces propaga el error")
    void givenRepositoryFails_whenExecute_thenPropagateError() {
        when(repository.save(any())).thenReturn(Mono.error(new RuntimeException("DB down")));

        StepVerifier.create(service.execute(buildCommand(), CORRELATION_ID))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("dado coberturas vacías cuando se ejecuta entonces prima total es ZERO")
    void givenEmptyCoverages_whenExecute_thenTotalPrimaIsZero() {
        CreateQuoteCommand cmd = new CreateQuoteCommand(
                "Test", "test@t.com", "+57 300", "1990-01-01",
                List.of(), List.of(), List.of(), "mensual", null);

        StepVerifier.create(service.execute(cmd, CORRELATION_ID))
                .assertNext(quote ->
                        assertThat(quote.totalMonthlyPrima())
                                .isEqualByComparingTo(BigDecimal.ZERO))
                .verifyComplete();
    }

    @Test
    @DisplayName("dado un comando válido cuando se ejecuta entonces correlationId y campos holder están presentes")
    void givenValidCommand_whenExecute_thenCorrelationIdAndHolderFieldsArePresent() {
        StepVerifier.create(service.execute(buildCommand(), CORRELATION_ID))
                .assertNext(quote -> {
                    assertThat(quote.correlationId()).isEqualTo(CORRELATION_ID);
                    assertThat(quote.holderPhone()).isEqualTo("+57 300 000 0000");
                    assertThat(quote.holderDob()).isEqualTo("1990-06-15");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("dado comando con beneficiarios cuando se ejecuta entonces beneficiarios están en el quote")
    void givenCommandWithBeneficiaries_whenExecute_thenBeneficiariesIncluded() {
        Beneficiary ben = new Beneficiary("b-1", "Juan García", "Cónyuge", 100, "death", "Fallecimiento");
        List<SelectedCoverage> coverages = List.of(
                new SelectedCoverage("death", "t1",
                        BigDecimal.valueOf(12_500), BigDecimal.valueOf(10_000_000)));
        CreateQuoteCommand cmd = new CreateQuoteCommand(
                "María García", "maria@email.com", "+57 300 000 0000", "1990-06-15",
                coverages, List.of(ben), List.of(), "mensual", null);

        StepVerifier.create(service.execute(cmd, CORRELATION_ID))
                .assertNext(quote -> {
                    assertThat(quote.beneficiaries()).hasSize(1);
                    Beneficiary saved = quote.beneficiaries().get(0);
                    assertThat(saved.id()).isEqualTo("b-1");
                    assertThat(saved.name()).isEqualTo("Juan García");
                    assertThat(saved.relation()).isEqualTo("Cónyuge");
                    assertThat(saved.pct()).isEqualTo(100);
                    assertThat(saved.moduleId()).isEqualTo("death");
                    assertThat(saved.coverageType()).isEqualTo("Fallecimiento");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("dado un código de descuento válido cuando se ejecuta entonces la prima se reduce y el código queda registrado")
    void givenValidDiscountCode_whenExecute_thenPrimaIsDiscountedAndCodeStored() {
        when(discountCodeRepository.findByCode("BABYLON10"))
                .thenReturn(Mono.just(new DiscountCode("BABYLON10", BigDecimal.valueOf(10), true)));

        CreateQuoteCommand cmd = buildCommandWithDiscount("BABYLON10");

        StepVerifier.create(service.execute(cmd, CORRELATION_ID))
                .assertNext(quote -> {
                    assertThat(quote.totalMonthlyPrima()).isEqualByComparingTo(BigDecimal.valueOf(11_250));
                    assertThat(quote.appliedDiscountCode()).isEqualTo("BABYLON10");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("dado un código de descuento inactivo cuando se ejecuta entonces falla con QuoteValidationException")
    void givenInactiveDiscountCode_whenExecute_thenThrowsQuoteValidationException() {
        when(discountCodeRepository.findByCode("OLDCODE"))
                .thenReturn(Mono.just(new DiscountCode("OLDCODE", BigDecimal.valueOf(15), false)));

        CreateQuoteCommand cmd = buildCommandWithDiscount("OLDCODE");

        StepVerifier.create(service.execute(cmd, CORRELATION_ID))
                .expectError(QuoteValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("dado un código de descuento inexistente cuando se ejecuta entonces falla con QuoteValidationException")
    void givenUnknownDiscountCode_whenExecute_thenThrowsQuoteValidationException() {
        when(discountCodeRepository.findByCode("NOEXISTE")).thenReturn(Mono.empty());

        CreateQuoteCommand cmd = buildCommandWithDiscount("NOEXISTE");

        StepVerifier.create(service.execute(cmd, CORRELATION_ID))
                .expectError(QuoteValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("dado que no se envía código de descuento cuando se ejecuta entonces appliedDiscountCode es null")
    void givenNoDiscountCode_whenExecute_thenAppliedDiscountCodeIsNull() {
        StepVerifier.create(service.execute(buildCommand(), CORRELATION_ID))
                .assertNext(quote -> assertThat(quote.appliedDiscountCode()).isNull())
                .verifyComplete();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private CreateQuoteCommand buildCommand() {
        List<SelectedCoverage> coverages = List.of(
                new SelectedCoverage("death", "t1",
                        BigDecimal.valueOf(12_500), BigDecimal.valueOf(10_000_000))
        );
        return new CreateQuoteCommand(
                "María García", "maria@email.com", "+57 300 000 0000", "1990-06-15",
                coverages, List.of(), List.of("medico_virtual"), "mensual", null);
    }

    private CreateQuoteCommand buildCommandWithDiscount(String discountCode) {
        List<SelectedCoverage> coverages = List.of(
                new SelectedCoverage("death", "t1",
                        BigDecimal.valueOf(12_500), BigDecimal.valueOf(10_000_000))
        );
        return new CreateQuoteCommand(
                "María García", "maria@email.com", "+57 300 000 0000", "1990-06-15",
                coverages, List.of(), List.of("medico_virtual"), "mensual", discountCode);
    }
}
