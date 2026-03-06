package com.babylon.insurance.quote.domain.model;

/**
 * Possible lifecycle states of an insurance quote / policy.
 *
 * <p>State transitions:
 * <pre>
 *   QUOTED ──► ISSUED
 *   QUOTED ──► CANCELLED
 *   ISSUED ──► CANCELLED
 * </pre>
 */
public enum QuoteStatus {

    /** Quote has been calculated and presented to the holder. */
    QUOTED,

    /** Quote has been confirmed and a policy has been issued. */
    ISSUED,

    /** Quote or policy has been cancelled. */
    CANCELLED
}
