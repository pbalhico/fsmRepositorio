package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.Pedido;
import es.examplepb.findstockmanager.repositorios.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private Logger log;

    @Override // Este método ya no se usa directamente desde el controlador filtrado
    public List<Pedido> findAll() {
        return pedidoRepository.findAll(); // Puedes mantenerlo para otros casos si es necesario
    }

    @Override
    public Pedido findById(Integer id) {
        return null;
    }

    @Override
    public List<Pedido> findAllFiltered(List<String> estados, LocalDate fechaSolicitud) {
        return List.of();
    }

    @Override
    public List<Pedido> findAllFiltered(List<String> estados, LocalDate fechaSolicitud, String destinoTipo) {
        // Lógica para construir la consulta o llamar a los métodos del repositorio
        // basándose en la combinación de filtros.

        boolean hasEstadoFilter = estados != null && !estados.isEmpty();
        boolean hasFechaFilter = fechaSolicitud != null;
        boolean hasDestinoTipoFilter = destinoTipo != null && !destinoTipo.isEmpty();

        if (!hasEstadoFilter && !hasFechaFilter && !hasDestinoTipoFilter) {
            // Caso 1: Ningún filtro -> Obtener todos
            return pedidoRepository.findAll();
        }

        // ----- Lógica de Filtrado por Destino -----
        if ("tienda".equals(destinoTipo)) {
            if (!hasEstadoFilter && !hasFechaFilter) {
                // Caso 2: Solo Destino = Tienda
                return pedidoRepository.findByDestinoTiendaIsNotNull(); // Necesitas este método
            } else if (hasEstadoFilter && !hasFechaFilter) {
                // Caso 3: Estados AND Destino = Tienda
                return pedidoRepository.findByEstado_DescripcionEstadoInAndDestinoTiendaIsNotNull(estados); // Necesitas este método
            } else if (!hasEstadoFilter && hasFechaFilter) {
                // Caso 4: Fecha AND Destino = Tienda
                return pedidoRepository.findByFechaSolicitudAndDestinoTiendaIsNotNull(fechaSolicitud); // Necesitas este método
            } else { // hasEstadoFilter && hasFechaFilter
                // Caso 5: Estados AND Fecha AND Destino = Tienda
                return pedidoRepository.findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoTiendaIsNotNull(estados, fechaSolicitud); // Necesitas este método
            }
        } else if ("almacen".equals(destinoTipo)) {
            if (!hasEstadoFilter && !hasFechaFilter) {
                // Caso 6: Solo Destino = Almacen
                return pedidoRepository.findByDestinoAlmacenIsNotNull(); // Necesitas este método
            } else if (hasEstadoFilter && !hasFechaFilter) {
                // Caso 7: Estados AND Destino = Almacen
                return pedidoRepository.findByEstado_DescripcionEstadoInAndDestinoAlmacenIsNotNull(estados); // Necesitas este método
            } else if (!hasEstadoFilter && hasFechaFilter) {
                // Caso 8: Fecha AND Destino = Almacen
                return pedidoRepository.findByFechaSolicitudAndDestinoAlmacenIsNotNull(fechaSolicitud); // Necesitas este método
            } else { // hasEstadoFilter && hasFechaFilter
                // Caso 9: Estados AND Fecha AND Destino = Almacen
                return pedidoRepository.findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoAlmacenIsNotNull(estados, fechaSolicitud); // Necesitas este método
            }
        }
        // ----- Lógica de Filtrado SIN Filtro de Destino Específico -----
        else { // destinoTipo es null o un valor no reconocido (significa "no filtrar por destino")
            if (hasEstadoFilter && !hasFechaFilter) {
                // Caso 10: Solo Estados (ya implementado)
                return pedidoRepository.findByEstado_DescripcionEstadoIn(estados);
            } else if (!hasEstadoFilter && hasFechaFilter) {
                // Caso 11: Solo Fecha (ya implementado)
                return pedidoRepository.findByFechaSolicitud(fechaSolicitud);
            } else if (hasEstadoFilter && hasFechaFilter) {
                // Caso 12: Estados AND Fecha (ya implementado)
                return pedidoRepository.findByEstado_DescripcionEstadoInAndFechaSolicitud(estados, fechaSolicitud);
            } else {
                // Si llega aquí con filtros pero sin destinoTipo especificado,
                // y ya manejamos el caso de ningún filtro al inicio,
                // debería significar que destinoTipo no era "tienda" ni "almacen".
                // Podrías devolver una lista vacía o lanzar un error.
                log.warn("Tipo de destino ({}) no reconocido o combina filtros sin tipo de destino especificado.", destinoTipo);
                return Collections.emptyList();
            }
        }
    }
}
