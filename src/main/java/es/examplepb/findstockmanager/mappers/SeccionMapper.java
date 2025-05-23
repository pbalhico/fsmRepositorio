package es.examplepb.findstockmanager.mappers;

import es.examplepb.findstockmanager.dto.SeccionDto;
import es.examplepb.findstockmanager.entidades.SeccionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para convertir entre SeccionEntity y SeccionDTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeccionMapper {
    SeccionDto toDto(SeccionEntity entity);

    SeccionEntity toEntity(SeccionDto dto);
}
