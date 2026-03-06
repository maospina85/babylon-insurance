package com.babylon.insurance.product.domain.model;

/**
 * Lifecycle status of a product catalogue version.
 */
public enum ProductStatus {

    /** Product is active and available for quoting. */
    ACTIVE,

    /** Product is no longer offered; existing policies remain valid. */
    INACTIVE
}
