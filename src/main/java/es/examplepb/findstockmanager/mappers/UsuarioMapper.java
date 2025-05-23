// src/main/java/es/examplepb/findstockmanager/mappers/UsuarioMapper.java
package es.examplepb.findstockmanager.mappers;

import es.examplepb.findstockmanager.dto.UsuarioDto;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    // Método para convertir UsuarioDto a UsuarioEntity
    // MapStruct mapeará automáticamente los campos con el mismo nombre (nombre, apellido, email, password)
    // Se ignoran las relaciones complejas para que sean manejadas en el servicio.
    // CORREGIDO: "almacenEntity" a "almacen" para que coincida con UsuarioEntity
    @Mapping(target = "almacen", ignore = true)
    // Mantenemos ignore=true; el servicio asignará la entidad AlmacenEntity
    @Mapping(target = "rolEntity", ignore = true)   // Mantenemos ignore=true; el servicio asignará la entidad RolEntity
    @Mapping(target = "fotografiaUsuario", ignore = true)
    // Ignoramos el MultipartFile; el servicio lo procesará
    UsuarioEntity toEntity(UsuarioDto usuarioDto);

    // Método para convertir UsuarioEntity a UsuarioDto
    @Mapping(source = "rolEntity.tipoRol", target = "rolTipo") // Mapea el tipo de rol (String)
    // CORREGIDO: "almacenEntity.nombreAlmacen" a "almacen.nombreAlmacen" para que coincida con UsuarioEntity
    @Mapping(target = "nombreAlmacen", expression = "java(usuarioEntity.getAlmacen() != null ? usuarioEntity.getAlmacen().getNombreAlmacen() : null)")
    UsuarioDto toDto(UsuarioEntity usuarioEntity);
}
