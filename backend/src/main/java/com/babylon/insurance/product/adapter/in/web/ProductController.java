package com.babylon.insurance.product.adapter.in.web;

import com.babylon.insurance.product.adapter.in.web.dto.ProductResponse;
import com.babylon.insurance.product.domain.port.in.GetProductUseCase;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * REST adapter for product catalogue queries.
 *
 * <p>This class only delegates to the use case and maps the domain object to a DTO.
 * No business logic lives here.
 */
@RestController("babylonProductController")
@RequestMapping("/api/products")
public class ProductController {

    private static final CacheControl CATALOG_CACHE =
            CacheControl.maxAge(Duration.ofSeconds(300)).cachePublic();

    private final GetProductUseCase getProductUseCase;

    public ProductController(GetProductUseCase getProductUseCase) {
        this.getProductUseCase = getProductUseCase;
    }

    /**
     * Returns the current active product catalogue.
     *
     * <p>Response is publicly cacheable for 5 minutes because the catalogue
     * changes infrequently.
     *
     * @return {@code 200 OK} with the product catalogue
     */
    @GetMapping("/catalog")
    public Mono<ResponseEntity<ProductResponse>> getCatalog() {
        return getProductUseCase.findLatestActive()
                .map(product -> ResponseEntity.ok()
                        .cacheControl(CATALOG_CACHE)
                        .body(ProductResponse.from(product)));
    }
}
