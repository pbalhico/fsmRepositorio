package es.examplepb.findstockmanager.mappers;

import es.examplepb.findstockmanager.dto.ArticuloDto;
import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import es.examplepb.findstockmanager.entidades.SeccionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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

    // Método para actualizar una entidad existente desde un DTO
    @Mapping(target = "seccionEntity.id", source = "seccionId")
    @Mapping(target = "seccionEntity.categoriaSeccion", ignore = true)
    // Ignorar el ID para que MapStruct no intente cambiarlo si la entidad ya existe y tiene un ID establecido
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ArticuloDto dto, @MappingTarget ArticuloEntity entity);

    // Método para crear una SeccionEntity con solo el ID (para establecer la relación)
    default SeccionEntity map(Integer seccionId) {
        if (seccionId == null) {
            return null;
        }
        SeccionEntity seccion = new SeccionEntity();
        seccion.setId(seccionId);
        return seccion;
    }
}
