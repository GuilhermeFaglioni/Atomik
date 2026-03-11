package com.atomik.atomik_api.infrastructure.persistence;

import com.atomik.atomik_api.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct para converter entre o modelo de Domínio e a Entidade de
 * Persistência.
 * <p>
 * O uso de MapStruct é o padrão ouro em Clean Arch profissional pois:
 * 1. Gera código performático em tempo de compilação (sem Reflection).
 * 2. Garante que todos os campos foram mapeados (unmappedTargetPolicy = ERROR).
 * 3. Mantém as classes de modelo limpas de lógica de conversão.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * MapStruct usa o Builder do Lombok (adicionado anteriormente)
     * para reconstruir o objeto imutável de Domínio.
     */
    User toDomain(UserEntity entity);

    /**
     * Converte o Domínio para a Entidade JPA.
     * O campo 'updatedAt' é ignorado pois é gerenciado pelo JPA Auditing.
     */
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(User domain);
}
