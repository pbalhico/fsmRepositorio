package es.examplepb.findstockmanager.mappers;

import es.examplepb.findstockmanager.dto.PedidoArticuloDto;
import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para convertir entre PedidoArticuloEntity y PedidoArticuloDTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PedidoArticuloMapper {

    @Mapping(source = "articuloEntity.id", target = "articuloId")
    @Mapping(source = "articuloEntity.nombreArticulo", target = "nombreArticulo")
    PedidoArticuloDto toDto(PedidoArticuloEntity entity);

    // No se necesita toEntity para este caso, ya que PedidoArticuloId es un EmbeddedId
    // y la creación de PedidoArticuloEntity es más compleja.
}
