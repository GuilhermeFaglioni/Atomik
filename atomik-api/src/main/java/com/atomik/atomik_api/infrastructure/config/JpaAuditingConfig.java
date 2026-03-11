package com.atomik.atomik_api.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Habilita a auditoria de datas (createdAt, updatedAt) via Spring Data JPA.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
