package com.babylon.insurance.product.adapter.out.persistence;

import com.babylon.insurance.product.domain.model.CoverageTier;
import com.babylon.insurance.product.domain.model.DeathCoverage;
import com.babylon.insurance.product.domain.model.InsuranceModule;
import com.babylon.insurance.product.domain.model.Product;
import com.babylon.insurance.product.domain.model.ProductStatus;
import com.babylon.insurance.product.domain.port.out.ProductRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * MongoDB persistence adapter implementing {@link ProductRepositoryPort}.
 *
 * <p>Responsibilities: map {@link Product} ↔ {@link ProductDocument}, delegate
 * to {@link ProductReactiveRepository}. No business logic here.
 */
@Component
public class ProductMongoAdapter implements ProductRepositoryPort {

    private final ProductReactiveRepository repository;

    public ProductMongoAdapter(ProductReactiveRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Mono<Product> findLatestActive() {
        return repository.findFirstByStatusOrderByCreatedAtDesc("ACTIVE").map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public Mono<Product> save(Product product) {
        return repository.save(toDocument(product)).map(this::toDomain);
    }

    // ── mapping: domain → document ─────────────────────────────────────────

    private ProductDocument toDocument(Product p) {
        ProductDocument doc = new ProductDocument();
        doc.setProductCode(p.productCode());
        doc.setVersion(p.version());
        doc.setStatus(p.status().name());
        doc.setModules(p.modules().stream().map(this::toDocument).toList());
        doc.setCreatedAt(p.createdAt());
        return doc;
    }

    private ModuleDocument toDocument(InsuranceModule m) {
        List<TierDocument> tiers = m.tiers().stream().map(this::toDocument).toList();
        List<ModuleDocument.CoverageDocument> coverages = m.coverages() == null ? List.of()
                : m.coverages().stream().map(this::toDocument).toList();
        return new ModuleDocument(m.moduleId(), m.labelKey(), m.icon(), m.colorToken(),
                m.description(), m.hasBeneficiaries(), m.beneficiaryType(), tiers, coverages);
    }

    private TierDocument toDocument(CoverageTier t) {
        return new TierDocument(t.tierId(), t.label(), t.sumAsegurada(), t.prima(),
                t.includedCoverageIds());
    }

    private ModuleDocument.CoverageDocument toDocument(DeathCoverage dc) {
        return new ModuleDocument.CoverageDocument(dc.id(), dc.label(), dc.description());
    }

    // ── mapping: document → domain ─────────────────────────────────────────

    private Product toDomain(ProductDocument doc) {
        List<InsuranceModule> modules = doc.getModules() == null ? List.of()
                : doc.getModules().stream().map(this::toDomain).toList();
        return new Product(doc.getProductCode(), doc.getVersion(),
                ProductStatus.valueOf(doc.getStatus()), modules, doc.getCreatedAt());
    }

    private InsuranceModule toDomain(ModuleDocument m) {
        List<CoverageTier> tiers = m.getTiers() == null ? List.of()
                : m.getTiers().stream().map(this::toDomain).toList();
        List<DeathCoverage> coverages = m.getCoverages() == null ? List.of()
                : m.getCoverages().stream().map(this::toDomain).toList();
        return new InsuranceModule(m.getModuleId(), m.getLabelKey(), m.getIcon(), m.getColorToken(),
                m.getDescription(), m.isHasBeneficiaries(), m.getBeneficiaryType(), tiers, coverages);
    }

    private CoverageTier toDomain(TierDocument t) {
        return new CoverageTier(t.getTierId(), t.getLabel(), t.getSumAsegurada(), t.getPrima(),
                t.getIncludedCoverageIds() == null ? List.of() : t.getIncludedCoverageIds());
    }

    private DeathCoverage toDomain(ModuleDocument.CoverageDocument dc) {
        return new DeathCoverage(dc.getId(), dc.getLabel(), dc.getDescription());
    }
}
