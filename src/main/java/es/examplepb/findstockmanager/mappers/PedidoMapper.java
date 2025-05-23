package es.examplepb.findstockmanager.mappers;

import es.examplepb.findstockmanager.dto.PedidoDto;
import es.examplepb.findstockmanager.entidades.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para convertir entre PedidoEntity y PedidoDTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {PedidoArticuloMapper.class})
public interface PedidoMapper {

    @Mapping(source = "tipo.descripcionTipo", target = "tipoPedido")
    @Mapping(source = "estado.descripcionEstado", target = "estadoPedido")
    @Mapping(source = "origenTiendaEntity.nombreTienda", target = "nombreOrigenTienda")
    @Mapping(source = "destinoTiendaEntity.nombreTienda", target = "nombreDestinoTienda")
    @Mapping(source = "origenAlmacenEntity.nombreAlmacen", target = "nombreOrigenAlmacen")
    @Mapping(source = "destinoAlmacenEntity.nombreAlmacen", target = "nombreDestinoAlmacen")
    @Mapping(source = "usuarioEntity.nombre", target = "nombreUsuario")
    // Se asume que los artículos serán mapeados por un método separado si es necesario cargar la lista completa
    @Mapping(target = "articulos", ignore = true)
        // Ignorar por ahora para evitar carga cíclica o N+1
    PedidoDto toDto(PedidoEntity entity);

    // No se necesita toEntity para este caso, ya que la creación de PedidoEntity es más compleja
}
