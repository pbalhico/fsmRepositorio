package es.examplepb.findstockmanager.mappers;

import es.examplepb.findstockmanager.dto.UsuarioDto;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Indica a MapStruct que genere un bean de Spring
public interface UsuarioMapper {

    // Método para convertir UsuarioDto a UsuarioEntity
    // MapStruct mapeará automáticamente los campos con el mismo nombre (nombre, apellido, nif, email, password)
    // No mapeamos directamente almacenId y rolId aquí, ya que debemos buscar las entidades completas en el servicio
    @Mapping(target = "almacen", ignore = true) // Ignoramos el mapeo directo de almacen
    @Mapping(target = "rolEntity", ignore = true)
    @Mapping(target = "fotografiaUsuario", ignore = true)
    // Ignoramos el mapeo directo de fotografiaUsuario (String)
    UsuarioEntity toUsuarioEntity(UsuarioDto usuarioDto);

    // Si necesitaras mapear de Entity a DTO para mostrar, podrías añadir:
    // UsuarioDto toUsuarioDto(UsuarioEntity usuarioEntity);
}