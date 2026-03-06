package com.babylon.insurance.quote.adapter.in.web;

import com.babylon.insurance.quote.adapter.in.web.dto.BeneficiaryDto;
import com.babylon.insurance.quote.adapter.in.web.dto.CreateQuoteRequest;
import com.babylon.insurance.quote.adapter.in.web.dto.QuoteResponse;
import com.babylon.insurance.quote.adapter.in.web.dto.SelectedCoverageDto;
import com.babylon.insurance.quote.domain.model.Beneficiary;
import com.babylon.insurance.quote.domain.model.SelectedCoverage;
import com.babylon.insurance.quote.domain.port.in.CreateQuoteCommand;
import com.babylon.insurance.quote.domain.port.in.CreateQuoteUseCase;
import com.babylon.insurance.quote.domain.port.in.GetQuotesUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * REST adapter for quote operations.
 *
 * <p>This class only delegates to use cases and maps DTOs ↔ domain objects.
 * No business logic lives here.
 */
@RestController("babylonQuoteController")
@RequestMapping("/api/quotes")
public class QuoteController {

    private final CreateQuoteUseCase createQuoteUseCase;
    private final GetQuotesUseCase getQuotesUseCase;

    public QuoteController(CreateQuoteUseCase createQuoteUseCase,
                           GetQuotesUseCase getQuotesUseCase) {
        this.createQuoteUseCase = createQuoteUseCase;
        this.getQuotesUseCase   = getQuotesUseCase;
    }

    /**
     * Creates a new insurance quote / policy.
     *
     * @param request validated request body
     * @return {@code 201 Created} with the persisted quote
     */
    @PostMapping
    public Mono<ResponseEntity<QuoteResponse>> createQuote(
            @Valid @RequestBody CreateQuoteRequest request) {

        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", UUID.randomUUID().toString());
            CreateQuoteCommand command = toCommand(request);
            return createQuoteUseCase.execute(command, correlationId)
                    .map(quote -> ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(QuoteResponse.from(quote)));
        });
    }

    /**
     * Returns all persisted quotes.
     *
     * @return {@code 200 OK} with the list of quotes
     */
    @GetMapping
    public Flux<QuoteResponse> findAll() {
        return getQuotesUseCase.findAll().map(QuoteResponse::from);
    }

    // ── mapping helpers ───────────────────────────────────────────────────────

    private CreateQuoteCommand toCommand(CreateQuoteRequest req) {
        List<SelectedCoverage> coverages = req.selectedCoverages().stream()
                .map(this::toDomain)
                .toList();
        List<Beneficiary> beneficiaries = req.beneficiaries() == null
                ? List.of()
                : req.beneficiaries().stream().map(this::toDomain).toList();
        List<String> assistances = req.assistances() == null ? List.of() : req.assistances();

        return new CreateQuoteCommand(
                req.holderName(),
                req.holderEmail(),
                req.holderPhone(),
                req.holderDob().toString(),
                coverages,
                beneficiaries,
                assistances,
                req.paymentFrequency()
        );
    }

    private SelectedCoverage toDomain(SelectedCoverageDto dto) {
        return new SelectedCoverage(dto.moduleId(), dto.tierId(), dto.prima(), dto.sumAsegurada());
    }

    private Beneficiary toDomain(BeneficiaryDto dto) {
        return new Beneficiary(
                UUID.randomUUID().toString(),
                dto.name(),
                dto.relation(),
                dto.pct(),
                dto.moduleId(),
                dto.coverageType()
        );
    }
}
