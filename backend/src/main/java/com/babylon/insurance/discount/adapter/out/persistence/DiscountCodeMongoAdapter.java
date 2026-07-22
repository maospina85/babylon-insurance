package com.babylon.insurance.discount.adapter.out.persistence;

import com.babylon.insurance.discount.domain.model.DiscountCode;
import com.babylon.insurance.discount.domain.port.out.DiscountCodeRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * MongoDB persistence adapter implementing {@link DiscountCodeRepositoryPort}.
 *
 * <p>Responsibilities: map {@link DiscountCode} ↔ {@link DiscountCodeDocument}, delegate
 * to {@link DiscountCodeReactiveRepository}. No business logic here.
 */
@Component
public class DiscountCodeMongoAdapter implements DiscountCodeRepositoryPort {

    private final DiscountCodeReactiveRepository repository;

    public DiscountCodeMongoAdapter(DiscountCodeReactiveRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Mono<DiscountCode> findByCode(String code) {
        return repository.findByCodeIgnoreCase(code).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public Mono<DiscountCode> save(DiscountCode discountCode) {
        return repository.save(toDocument(discountCode)).map(this::toDomain);
    }

    private DiscountCodeDocument toDocument(DiscountCode d) {
        return new DiscountCodeDocument(null, d.code(), d.percentOff(), d.active());
    }

    private DiscountCode toDomain(DiscountCodeDocument doc) {
        return new DiscountCode(doc.getCode(), doc.getPercentOff(), doc.isActive());
    }
}
