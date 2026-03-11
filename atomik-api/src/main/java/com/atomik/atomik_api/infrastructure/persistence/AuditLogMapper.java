package com.atomik.atomik_api.infrastructure.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.atomik.atomik_api.domain.model.AuditLog;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditLogMapper {
    @Mapping(target = "transactionId", source = "transaction.id")
    AuditLog toDomain(AuditLogEntity auditLogEntity);

    @Mapping(target = "transaction.id", source = "transactionId")
    AuditLogEntity toEntity(AuditLog auditLog);
}
