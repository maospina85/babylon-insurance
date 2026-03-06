package com.babylon.insurance.quote.domain.model;

/**
 * Beneficiary assigned to a specific coverage module.
 *
 * <p>The sum of {@code pct} values for all beneficiaries belonging to the same
 * {@code moduleId} must equal exactly {@code 100}.
 *
 * @param id            unique beneficiary identifier (UUID)
 * @param name          beneficiary's full name
 * @param relation      family relationship; one of: Cónyuge, Hijo/a, Padre/Madre, Hermano/a, Otro
 * @param pct           percentage allocation in the range [1, 100]
 * @param moduleId      module this beneficiary belongs to
 * @param coverageType  human-readable coverage type label (e.g., "Cobertura de Fallecimiento")
 */
public record Beneficiary(
        String id,
        String name,
        String relation,
        Integer pct,
        String moduleId,
        String coverageType
) {}
