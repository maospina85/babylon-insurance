package com.babylon.insurance.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

/**
 * MongoDB configuration.
 *
 * <p>Activates reactive auditing so that {@code @CreatedDate} and
 * {@code @LastModifiedDate} annotations are populated automatically
 * on persistence documents.
 *
 * <p>Connection properties (URI, database name) are loaded from
 * {@code application.yml} via Spring Boot auto-configuration.
 */
@Configuration
@EnableReactiveMongoAuditing
public class MongoConfig {
    // Spring Boot auto-configures the reactive MongoDB client from application.yml.
    // Custom converters or codec configuration can be added here as needed.
}
