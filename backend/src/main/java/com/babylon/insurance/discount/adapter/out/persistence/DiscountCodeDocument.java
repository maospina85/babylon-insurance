package com.babylon.insurance.discount.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * MongoDB document for promotional discount codes.
 */
@Document(collection = "discount_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCodeDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private BigDecimal percentOff;
    private boolean active;
}
