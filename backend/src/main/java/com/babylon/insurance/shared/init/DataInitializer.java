package com.babylon.insurance.shared.init;

import com.babylon.insurance.discount.domain.model.DiscountCode;
import com.babylon.insurance.discount.domain.port.out.DiscountCodeRepositoryPort;
import com.babylon.insurance.product.domain.model.CoverageTier;
import com.babylon.insurance.product.domain.model.DeathCoverage;
import com.babylon.insurance.product.domain.model.InsuranceModule;
import com.babylon.insurance.product.domain.model.Product;
import com.babylon.insurance.product.domain.model.ProductStatus;
import com.babylon.insurance.product.domain.port.out.ProductRepositoryPort;
import com.babylon.insurance.shared.logging.StructuredLogger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Seeds the product catalogue and promotional discount codes on startup if
 * their respective collections are empty.
 *
 * <p>This initialiser is idempotent: it checks for existing data before
 * inserting, so it can be restarted safely.
 *
 * <p>Note: {@code .block()} is acceptable here because this runs exclusively
 * at application startup, outside any reactive pipeline.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private final ProductRepositoryPort productRepository;
    private final DiscountCodeRepositoryPort discountCodeRepository;
    private final StructuredLogger log;

    public DataInitializer(ProductRepositoryPort productRepository,
                           DiscountCodeRepositoryPort discountCodeRepository,
                           StructuredLogger log) {
        this.productRepository      = productRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.log                    = log;
    }

    /**
     * Entry point invoked by Spring Boot after the application context is ready.
     *
     * @param args application arguments (unused)
     */
    @Override
    public void run(ApplicationArguments args) {
        productRepository.findLatestActive()
                .switchIfEmpty(seedProduct())
                .block();

        discountCodeRepository.findByCode("BABYLON10")
                .switchIfEmpty(seedDiscountCodes())
                .block();
    }

    // ── discount codes ──────────────────────────────────────────────────────

    private Mono<DiscountCode> seedDiscountCodes() {
        return discountCodeRepository.save(new DiscountCode("BABYLON20", BigDecimal.valueOf(20), true))
                .then(discountCodeRepository.save(new DiscountCode("BABYLON10", BigDecimal.valueOf(10), true)))
                .doOnSuccess(d -> log.info(
                        "discount_codes_seeded", "system", Map.of("codes", 2)));
    }

    // ── private builders ──────────────────────────────────────────────────────

    private Mono<Product> seedProduct() {
        Product product = new Product(
                "BABYLON_LIFE",
                "2025.1",
                ProductStatus.ACTIVE,
                List.of(buildDeathModule(), buildDisabilityModule(), buildAccidentsModule()),
                Instant.now()
        );
        return productRepository.save(product)
                .doOnSuccess(p -> log.info(
                        "catalogue_seeded",
                        "system",
                        Map.of("version", p.version(), "modules", p.modules().size())));
    }

    private InsuranceModule buildDeathModule() {
        List<DeathCoverage> coverages = List.of(
                new DeathCoverage("death_main",      "Fallecimiento por cualquier causa",
                        "Paga la suma asegurada a beneficiarios. Cubre homicidio y suicidio según condiciones."),
                new DeathCoverage("disability_perm", "Invalidez total y permanente",
                        "Indemnización si el asegurado pierde capacidad laboral por accidente o enfermedad."),
                new DeathCoverage("serious_illness",  "Enfermedades graves",
                        "Anticipo por diagnóstico de cáncer, infarto o trasplante de órganos."),
                new DeathCoverage("acc_death",        "Muerte accidental — doble indemnización",
                        "Pago adicional si el fallecimiento ocurre por accidente."),
                new DeathCoverage("funeral",          "Gastos funerarios",
                        "Cobertura de servicios exequiales."),
                new DeathCoverage("daily_hosp",       "Renta diaria por hospitalización",
                        "Auxilio económico por cada día de hospitalización."),
                new DeathCoverage("debt_coverage",    "Amparo de deudas",
                        "Cubre deudas vigentes del asegurado al momento del fallecimiento.")
        );

        List<CoverageTier> tiers = List.of(
                tier("t1", "Esencial",    bd(10_000_000), bd(12_500),
                        List.of("death_main", "funeral")),
                tier("t2", "Familiar",    bd(25_000_000), bd(28_900),
                        List.of("death_main", "funeral", "disability_perm")),
                tier("t3", "Patrimonial", bd(50_000_000), bd(52_000),
                        List.of("death_main", "funeral", "disability_perm", "acc_death", "serious_illness")),
                tier("t4", "Élite",      bd(100_000_000), bd(95_000),
                        List.of("death_main", "funeral", "disability_perm", "acc_death",
                                "serious_illness", "daily_hosp", "debt_coverage"))
        );

        return new InsuranceModule("death", "Vida / Fallecimiento", "🛡️", "death",
                "Capital asegurado pagado a tus beneficiarios. Coberturas adicionales según nivel elegido.",
                true, "Cobertura de Fallecimiento", tiers, coverages);
    }

    private InsuranceModule buildDisabilityModule() {
        List<CoverageTier> tiers = List.of(
                tier("t1", "Básico",    bd(1_000_000),  bd(8_200),  List.of()),
                tier("t2", "Estándar",  bd(2_500_000),  bd(18_500), List.of()),
                tier("t3", "Plus",      bd(5_000_000),  bd(34_000), List.of()),
                tier("t4", "Premium",   bd(10_000_000), bd(62_000), List.of())
        );
        return new InsuranceModule("disability", "Invalidez Total", "⚕️", "disability",
                "Renta mensual garantizada si pierdes total y permanentemente tu capacidad laboral.",
                false, null, tiers, List.of());
    }

    private InsuranceModule buildAccidentsModule() {
        List<CoverageTier> tiers = List.of(
                tier("t1", "Ligero",    bd(5_000_000),  bd(5_900),  List.of()),
                tier("t2", "Completo",  bd(15_000_000), bd(14_800), List.of()),
                tier("t3", "Total",     bd(30_000_000), bd(27_500), List.of()),
                tier("t4", "Máximo",    bd(50_000_000), bd(44_000), List.of())
        );
        return new InsuranceModule("accidents", "Accidentes", "⚡", "accidents",
                "Indemnización inmediata por accidentes personales 24/7 en cualquier lugar del mundo.",
                false, null, tiers, List.of());
    }

    private CoverageTier tier(String id, String label, BigDecimal sa, BigDecimal prima,
                              List<String> coverageIds) {
        return new CoverageTier(id, label, sa, prima, coverageIds);
    }

    private BigDecimal bd(long value) {
        return BigDecimal.valueOf(value);
    }
}
