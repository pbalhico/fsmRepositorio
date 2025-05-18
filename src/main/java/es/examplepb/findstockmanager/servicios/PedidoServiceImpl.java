package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.PedidoEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity; // Importar PedidoArticulo
import es.examplepb.findstockmanager.repositorios.PedidoRepository;
import es.examplepb.findstockmanager.repositorios.PedidoArticuloRepository; // Importar PedidoArticuloRepository
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Importar LoggerFactory
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional; // Importar Optional

@Service
@RequiredArgsConstructor // Lombok generará el constructor con los campos 'final'
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoArticuloRepository pedidoArticuloRepository; // Inyectar PedidoArticuloRepository
    private static final Logger log = LoggerFactory.getLogger(PedidoServiceImpl.class); // Inicializar Logger correctamente


    @Override // Este método ya no se usa directamente desde el controlador filtrado (generalmente)
    public List<PedidoEntity> findAll() {
        return pedidoRepository.findAll(); // Puedes mantenerlo para otros casos si es necesario
    }

    @Override
    public PedidoEntity findById(Integer id) {
        log.info("Buscando pedido con ID: {}", id);
        // Usar el repositorio para buscar el pedido por ID
        Optional<PedidoEntity> pedidoOptional = pedidoRepository.findById(id);
        // Retornar el pedido si está presente, de lo contrario null
        return pedidoOptional.orElse(null);
    }

    @Override
    public List<PedidoEntity> findAllFiltered(List<String> estados, LocalDate fechaSolicitud) {
        // Este método parece que ya no se usa en el controlador actual que usa findAllFiltered(estados, fechaSolicitud, destinoTipo)
        // Puedes decidir si lo mantienes o lo eliminas si no se usa en ninguna parte.
        // Si lo mantienes, necesitarías implementar su lógica de filtrado.
        log.warn("findAllFiltered(List<String> estados, LocalDate fechaSolicitud) llamado, pero puede estar obsoleto.");
        return Collections.emptyList(); // Implementación placeholder
    }


    @Override
    public List<PedidoEntity> findAllFiltered(List<String> estados, LocalDate fechaSolicitud, String destinoTipo) {
        log.info("Aplicando filtros: estados={}, fechaSolicitud={}, destinoTipo={}", estados, fechaSolicitud, destinoTipo);
        // Lógica para construir la consulta o llamar a los métodos del repositorio
        // basándose en la combinación de filtros.

        boolean hasEstadoFilter = estados != null && !estados.isEmpty();
        boolean hasFechaFilter = fechaSolicitud != null;
        boolean hasDestinoTipoFilter = destinoTipo != null && !destinoTipo.isEmpty();

        if (!hasEstadoFilter && !hasFechaFilter && !hasDestinoTipoFilter) {
            // Caso 1: Ningún filtro -> Obtener todos
            log.debug("No hay filtros aplicados, obteniendo todos los pedidos.");
            return pedidoRepository.findAll();
        }

        // ----- Lógica de Filtrado por Destino -----
        if ("tienda".equals(destinoTipo)) {
            if (!hasEstadoFilter && !hasFechaFilter) {
                // Caso 2: Solo Destino = Tienda
                log.debug("Filtrando por Destino = Tienda.");
                return pedidoRepository.findByDestinoTiendaEntityIsNotNull(); // Necesitas este método
            } else if (hasEstadoFilter && !hasFechaFilter) {
                // Caso 3: Estados AND Destino = Tienda
                log.debug("Filtrando por Estados ({}) AND Destino = Tienda.", estados);
                return pedidoRepository.findByEstado_DescripcionEstadoInAndDestinoTiendaEntityIsNotNull(estados); // Necesitas este método
            } else if (!hasEstadoFilter && hasFechaFilter) {
                // Caso 4: Fecha ({}) AND Destino = Tienda
                log.debug("Filtrando por Fecha ({}) AND Destino = Tienda.", fechaSolicitud);
                return pedidoRepository.findByFechaSolicitudAndDestinoTiendaEntityIsNotNull(fechaSolicitud); // Necesitas este método
            } else { // hasEstadoFilter && hasFechaFilter
                // Caso 5: Estados AND Fecha AND Destino = Tienda
                log.debug("Filtrando por Estados ({}) AND Fecha ({}) AND Destino = Tienda.", estados, fechaSolicitud);
                return pedidoRepository.findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoTiendaEntityIsNotNull(estados, fechaSolicitud); // Necesitas este método
            }
        } else if ("almacen".equals(destinoTipo)) {
            if (!hasEstadoFilter && !hasFechaFilter) {
                // Caso 6: Solo Destino = Almacen
                log.debug("Filtrando por Destino = Almacen.");
                return pedidoRepository.findByDestinoAlmacenEntityIsNotNull(); // Necesitas este método
            } else if (hasEstadoFilter && !hasFechaFilter) {
                // Caso 7: Estados AND Destino = Almacen
                log.debug("Filtrando por Estados ({}) AND Destino = Almacen.", estados);
                return pedidoRepository.findByEstado_DescripcionEstadoInAndDestinoAlmacenEntityIsNotNull(estados); // Necesitas este método
            } else if (!hasEstadoFilter && hasFechaFilter) {
                // Caso 8: Fecha ({}) AND Destino = Almacen
                log.debug("Filtrando por Fecha ({}) AND Destino = Almacen.", fechaSolicitud);
                return pedidoRepository.findByFechaSolicitudAndDestinoAlmacenEntityIsNotNull(fechaSolicitud); // Necesitas este método
            } else { // hasEstadoFilter && hasFechaFilter
                // Caso 9: Estados AND Fecha AND Destino = Almacen
                log.debug("Filtrando por Estados ({}) AND Fecha ({}) AND Destino = Almacen.", estados, fechaSolicitud);
                return pedidoRepository.findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoAlmacenEntityIsNotNull(estados, fechaSolicitud); // Necesitas este método
            }
        }
        // ----- Lógica de Filtrado SIN Filtro de Destino Específico -----
        else { // destinoTipo es null o un valor no reconocido (significa "no filtrar por destino")
            if (hasEstadoFilter && !hasFechaFilter) {
                // Caso 10: Solo Estados (ya implementado)
                log.debug("Filtrando solo por Estados ({}).", estados);
                return pedidoRepository.findByEstado_DescripcionEstadoIn(estados);
            } else if (!hasEstadoFilter && hasFechaFilter) {
                // Caso 11: Solo Fecha (ya implementado)
                log.debug("Filtrando solo por Fecha ({}).", fechaSolicitud);
                return pedidoRepository.findByFechaSolicitud(fechaSolicitud);
            } else if (hasEstadoFilter && hasFechaFilter) {
                // Caso 12: Estados AND Fecha (ya implementado)
                log.debug("Filtrando por Estados ({}) AND Fecha ({}).", estados, fechaSolicitud);
                return pedidoRepository.findByEstado_DescripcionEstadoInAndFechaSolicitud(estados, fechaSolicitud);
            } else {
                // Si llega aquí con filtros pero sin destinoTipo especificado,
                // y ya manejamos el caso de ningún filtro al inicio,
                // debería significar que destinoTipo no era "tienda" ni "almacen".
                // Podrías devolver una lista vacía o lanzar un error.
                log.warn("Tipo de destino ({}) no reconocido o combina filtros sin tipo de destino especificado. Devolviendo lista vacía.", destinoTipo);
                return Collections.emptyList();
            }
        }
    }

    /**
     * Busca todos los PedidoArticulo asociados a un pedido específico por su ID.
     *
     * @param pedidoId El ID del pedido.
     * @return Una lista de PedidoArticulo.
     */
    @Override
    public List<PedidoArticuloEntity> findArticulosByPedidoId(Integer pedidoId) {
        log.info("Buscando artículos para el pedido con ID: {}", pedidoId);
        // Usar el repositorio de PedidoArticulo para buscar por el ID del pedido
        return pedidoArticuloRepository.findById_Pedido(pedidoId); // Necesitas este método en PedidoArticuloRepository
    }
}
