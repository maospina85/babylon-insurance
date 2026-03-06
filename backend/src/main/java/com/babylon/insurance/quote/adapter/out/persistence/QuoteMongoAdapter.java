package com.babylon.insurance.quote.adapter.out.persistence;

import com.babylon.insurance.quote.domain.model.Beneficiary;
import com.babylon.insurance.quote.domain.model.Quote;
import com.babylon.insurance.quote.domain.model.QuoteStatus;
import com.babylon.insurance.quote.domain.model.SelectedCoverage;
import com.babylon.insurance.quote.domain.port.out.QuoteRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * MongoDB persistence adapter implementing {@link QuoteRepositoryPort}.
 *
 * <p>Responsibilities: map {@link Quote} ↔ {@link QuoteDocument}, delegate
 * to {@link QuoteReactiveRepository}. No business logic here.
 */
@Component
public class QuoteMongoAdapter implements QuoteRepositoryPort {

    private final QuoteReactiveRepository repository;

    public QuoteMongoAdapter(QuoteReactiveRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Mono<Quote> save(Quote quote) {
        return repository.save(toDocument(quote)).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public Flux<Quote> findAll() {
        return repository.findAll().map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public Mono<Quote> findByPolicyNumber(String policyNumber) {
        return repository.findByPolicyNumber(policyNumber).map(this::toDomain);
    }

    // ── mapping: domain → document ─────────────────────────────────────────

    private QuoteDocument toDocument(Quote q) {
        QuoteDocument doc = new QuoteDocument();
        doc.setQuoteId(q.quoteId());
        doc.setCorrelationId(q.correlationId());
        doc.setHolderNameEncrypted(q.holderNameEncrypted());
        doc.setHolderEmailEncrypted(q.holderEmailEncrypted());
        doc.setHolderPhone(q.holderPhone());
        doc.setHolderDob(q.holderDob());
        doc.setSelectedCoverages(q.selectedCoverages().stream().map(this::toDocument).toList());
        doc.setBeneficiaries(q.beneficiaries().stream().map(this::toDocument).toList());
        doc.setAssistances(q.assistances());
        doc.setTotalMonthlyPrima(q.totalMonthlyPrima());
        doc.setPaymentFrequency(q.paymentFrequency());
        doc.setPolicyNumber(q.policyNumber());
        doc.setStatus(q.status().name());
        doc.setCreatedAt(q.createdAt());
        return doc;
    }

    private SelectedCoverageDocument toDocument(SelectedCoverage sc) {
        return new SelectedCoverageDocument(sc.moduleId(), sc.tierId(), sc.prima(), sc.sumAsegurada());
    }

    private BeneficiaryDocument toDocument(Beneficiary b) {
        return new BeneficiaryDocument(b.id(), b.name(), b.relation(), b.pct(), b.moduleId(), b.coverageType());
    }

    // ── mapping: document → domain ─────────────────────────────────────────

    private Quote toDomain(QuoteDocument doc) {
        List<SelectedCoverage> coverages = doc.getSelectedCoverages() == null ? List.of()
                : doc.getSelectedCoverages().stream().map(this::toDomain).toList();
        List<Beneficiary> beneficiaries = doc.getBeneficiaries() == null ? List.of()
                : doc.getBeneficiaries().stream().map(this::toDomain).toList();
        List<String> assistances = doc.getAssistances() == null ? List.of() : doc.getAssistances();

        return new Quote(
                doc.getQuoteId(),
                doc.getCorrelationId(),
                doc.getHolderNameEncrypted(),
                doc.getHolderEmailEncrypted(),
                doc.getHolderPhone(),
                doc.getHolderDob(),
                coverages,
                beneficiaries,
                assistances,
                doc.getTotalMonthlyPrima(),
                doc.getPaymentFrequency(),
                doc.getPolicyNumber(),
                QuoteStatus.valueOf(doc.getStatus()),
                doc.getCreatedAt()
        );
    }

    private SelectedCoverage toDomain(SelectedCoverageDocument d) {
        return new SelectedCoverage(d.getModuleId(), d.getTierId(), d.getPrima(), d.getSumAsegurada());
    }

    private Beneficiary toDomain(BeneficiaryDocument d) {
        return new Beneficiary(d.getId(), d.getName(), d.getRelation(), d.getPct(), d.getModuleId(), d.getCoverageType());
    }
}
