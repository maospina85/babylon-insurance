package com.babylon.insurance.quote.application;

import com.babylon.insurance.quote.domain.model.Quote;
import com.babylon.insurance.quote.domain.port.in.GetQuotesUseCase;
import com.babylon.insurance.quote.domain.port.out.QuoteRepositoryPort;
import com.babylon.insurance.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service that implements {@link GetQuotesUseCase}.
 */
@Service
public class GetQuotesService implements GetQuotesUseCase {

    private final QuoteRepositoryPort repository;

    public GetQuotesService(QuoteRepositoryPort repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<Quote> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no quote matches the given policy number
     */
    @Override
    public Mono<Quote> findByPolicyNumber(String policyNumber) {
        return repository.findByPolicyNumber(policyNumber)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Póliza no encontrada: " + policyNumber)));
    }
}
