package com.babylon.insurance.quote.application;

import com.babylon.insurance.quote.domain.model.SelectedCoverage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Premium Calculator Strategy")
class PremiumCalculatorStrategyTest {

    private PremiumCalculatorStrategy calculator;

    @BeforeEach
    void setUp() {
        calculator = new DefaultPremiumCalculator();
    }

    @Test
    @DisplayName("dado una lista de coberturas cuando calcula entonces retorna la suma correcta")
    void givenCoverageList_whenCalculate_thenReturnCorrectSum() {
        List<SelectedCoverage> coverages = List.of(
                coverage("death",      BigDecimal.valueOf(12_500)),
                coverage("disability", BigDecimal.valueOf(18_500)),
                coverage("accidents",  BigDecimal.valueOf(14_800))
        );

        BigDecimal result = calculator.calculate(coverages);

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(45_800));
    }

    @Test
    @DisplayName("dado lista vacía cuando calcula entonces retorna ZERO")
    void givenEmptyList_whenCalculate_thenReturnZero() {
        BigDecimal result = calculator.calculate(List.of());

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("dado lista null cuando calcula entonces retorna ZERO")
    void givenNullList_whenCalculate_thenReturnZero() {
        BigDecimal result = calculator.calculate(null);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("dado cobertura con prima null cuando calcula entonces la ignora (trata como cero)")
    void givenCoverageWithNullPrima_whenCalculate_thenIgnoreIt() {
        List<SelectedCoverage> coverages = List.of(
                coverage("death", BigDecimal.valueOf(12_500)),
                new SelectedCoverage("disability", "t1", null, null)
        );

        BigDecimal result = calculator.calculate(coverages);

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(12_500));
    }

    @Test
    @DisplayName("dado una sola cobertura cuando calcula entonces retorna su prima")
    void givenSingleCoverage_whenCalculate_thenReturnItsPrima() {
        BigDecimal result = calculator.calculate(
                List.of(coverage("accidents", BigDecimal.valueOf(5_900))));

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(5_900));
    }

    // ── helper ───────────────────────────────────────────────────────────────

    private SelectedCoverage coverage(String moduleId, BigDecimal prima) {
        return new SelectedCoverage(moduleId, "t2", prima, BigDecimal.valueOf(10_000_000));
    }
}
