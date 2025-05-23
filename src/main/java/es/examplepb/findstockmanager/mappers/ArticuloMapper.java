package es.examplepb.findstockmanager.mappers;

import es.examplepb.findstockmanager.dto.ArticuloDto;
import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para convertir entre ArticuloEntity y ArticuloDTO.
 * MapStruct genera automáticamente la implementación en tiempo de compilación.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArticuloMapper {

    @Mapping(source = "seccionEntity.id", target = "seccionId")
    @Mapping(source = "seccionEntity.categoriaSeccion", target = "categoriaSeccion")
    ArticuloDto toDto(ArticuloEntity entity);

    @Mapping(target = "seccionEntity.id", source = "seccionId")
    @Mapping(target = "seccionEntity.categoriaSeccion", ignore = true)
        // Ignorar para evitar carga perezosa inesperada o errores
    ArticuloEntity toEntity(ArticuloDto dto);
}
