package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.*;
import es.examplepb.findstockmanager.entidades.PedidoArticuloIdEntity;
import es.examplepb.findstockmanager.repositorios.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoArticuloRepository pedidoArticuloRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final ArticuloRepository articuloRepository;

    private static final Logger log = LoggerFactory.getLogger(PedidoServiceImpl.class);

    @Override
    public List<PedidoEntity> findAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Optional<PedidoEntity> findById(Integer id) {
        log.info("Buscando pedido con ID: {}", id);
        return pedidoRepository.findById(id);
    }

    // --- MÉTODO findByIdAndSetFechaRecepcion MODIFICADO ---
    @Override
    @Transactional
    public Optional<PedidoEntity> findByIdAndSetFechaRecepcion(Integer id) {
        Optional<PedidoEntity> pedidoOptional = pedidoRepository.findById(id);
        if (pedidoOptional.isPresent()) {
            PedidoEntity pedido = pedidoOptional.get();

            // Solo establece fechaRecepcion si aún no está establecida
            if (pedido.getFechaRecepcion() == null) {
                pedido.setFechaRecepcion(LocalDate.now());
                log.info("Pedido con ID {} abierto por primera vez. Fecha de recepción establecida.", id);
            }

            // Cambiar estado a "En tramite" si actualmente es "Pendiente"
            // Asumiendo que "En tramite" es el estado con ID 2 o el nombre exacto que hemos definido
            // (que ahora sabemos es "En tramite" sin tilde).
            EstadoPedidoEntity estadoActual = pedido.getEstado();
            if (estadoActual != null && "Pendiente".equals(estadoActual.getDescripcionEstado())) {
                Optional<EstadoPedidoEntity> enTramiteEstadoOptional = estadoPedidoRepository.findByDescripcionEstado("En tramite"); // Usar "En tramite" sin tilde
                if (enTramiteEstadoOptional.isPresent()) {
                    pedido.setEstado(enTramiteEstadoOptional.get());
                    log.info("Estado del pedido con ID {} cambiado a 'En tramite'.", id);
                } else {
                    log.error("Error: Estado 'En tramite' no encontrado en la base de datos de estados de pedido. Asegúrate de que existe este registro.");
                }
            }

            // Guardar los cambios (fechaRecepcion y/o estado)
            pedidoRepository.save(pedido);
        }
        return pedidoOptional;
    }
    // --------------------------------------------------------

    @Override
    public List<PedidoEntity> findAllFiltered(List<String> estados, LocalDate fechaSolicitud) {
        log.warn("findAllFiltered(List<String> estados, LocalDate fechaSolicitud) llamado, pero puede estar obsoleto. Se recomienda usar el método con 'destinoTipo'.");
        return Collections.emptyList();
    }

    @Override
    public List<PedidoEntity> findAllFiltered(List<String> estados, LocalDate fechaSolicitud, String destinoTipo) {
        log.info("Aplicando filtros: estados={}, fechaSolicitud={}, destinoTipo={}", estados, fechaSolicitud, destinoTipo);

        boolean hasEstadoFilter = estados != null && !estados.isEmpty();
        boolean hasFechaFilter = fechaSolicitud != null;
        boolean hasDestinoTipoFilter = destinoTipo != null && !destinoTipo.isEmpty();

        if (!hasEstadoFilter && !hasFechaFilter && !hasDestinoTipoFilter) {
            log.debug("No hay filtros aplicados, obteniendo todos los pedidos.");
            return pedidoRepository.findAll();
        }

        if ("tienda".equals(destinoTipo)) {
            if (!hasEstadoFilter && !hasFechaFilter) {
                log.debug("Filtrando por Destino = Tienda.");
                return pedidoRepository.findByDestinoTiendaEntityIsNotNull();
            } else if (hasEstadoFilter && !hasFechaFilter) {
                log.debug("Filtrando por Estados ({}) AND Destino = Tienda.", estados);
                return pedidoRepository.findByEstado_DescripcionEstadoInAndDestinoTiendaEntityIsNotNull(estados);
            } else if (!hasEstadoFilter && hasFechaFilter) {
                log.debug("Filtrando por Fecha ({}) AND Destino = Tienda.", fechaSolicitud);
                return pedidoRepository.findByFechaSolicitudAndDestinoTiendaEntityIsNotNull(fechaSolicitud);
            } else {
                log.debug("Filtrando por Estados ({}) AND Fecha ({}) AND Destino = Tienda.", estados, fechaSolicitud);
                return pedidoRepository.findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoTiendaEntityIsNotNull(estados, fechaSolicitud);
            }
        } else if ("almacen".equals(destinoTipo)) {
            if (!hasEstadoFilter && !hasFechaFilter) {
                log.debug("Filtrando por Destino = Almacen.");
                return pedidoRepository.findByDestinoAlmacenEntityIsNotNull();
            } else if (hasEstadoFilter && !hasFechaFilter) {
                log.debug("Filtrando por Estados ({}) AND Destino = Almacen.", estados);
                return pedidoRepository.findByEstado_DescripcionEstadoInAndDestinoAlmacenEntityIsNotNull(estados);
            } else if (!hasEstadoFilter && hasFechaFilter) {
                log.debug("Filtrando por Fecha ({}) AND Destino = Almacen.", fechaSolicitud);
                return pedidoRepository.findByFechaSolicitudAndDestinoAlmacenEntityIsNotNull(fechaSolicitud);
            } else {
                log.debug("Filtrando por Estados ({}) AND Fecha ({}) AND Destino = Almacen.", estados, fechaSolicitud);
                return pedidoRepository.findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoAlmacenEntityIsNotNull(estados, fechaSolicitud);
            }
        } else {
            if (hasEstadoFilter && !hasFechaFilter) {
                log.debug("Filtrando solo por Estados ({}).", estados);
                return pedidoRepository.findByEstado_DescripcionEstadoIn(estados);
            } else if (!hasEstadoFilter && hasFechaFilter) {
                log.debug("Filtrando solo por Fecha ({}).", fechaSolicitud);
                return pedidoRepository.findByFechaSolicitud(fechaSolicitud);
            } else if (hasEstadoFilter && hasFechaFilter) {
                log.debug("Filtrando por Estados ({}) AND Fecha ({}).", estados, fechaSolicitud);
                return pedidoRepository.findByEstado_DescripcionEstadoInAndFechaSolicitud(estados, fechaSolicitud);
            } else {
                log.warn("Tipo de destino ({}) no reconocido o combina filtros sin tipo de destino especificado. Devolviendo lista vacía.", destinoTipo);
                return Collections.emptyList();
            }
        }
    }

    @Override
    public List<PedidoArticuloEntity> findArticulosByPedidoId(Integer pedidoId) {
        log.info("Buscando artículos para el pedido con ID: {}", pedidoId);
        Optional<PedidoEntity> pedidoOptional = pedidoRepository.findById(pedidoId);
        return pedidoOptional.map(PedidoEntity::getPedidoArticulos).orElse(Collections.emptyList());
    }

    @Override
    @Transactional
    public PedidoEntity save(PedidoEntity pedidoEntity) {
        log.info("Guardando o actualizando pedido con ID: {}", pedidoEntity.getId());

        // Solo establecer fechaSolicitud al crear un nuevo pedido
        if (pedidoEntity.getId() == null) {
            log.info("Estableciendo fechaSolicitud para un nuevo pedido.");
            pedidoEntity.setFechaSolicitud(LocalDate.now());
            // El estado inicial "Pendiente" también se debería asignar aquí si no está ya
            if (pedidoEntity.getEstado() == null) {
                estadoPedidoRepository.findByDescripcionEstado("Pendiente")
                        .ifPresent(pedidoEntity::setEstado);
            }
        }
        // NOTA: fechaRecepcion NO se establece aquí; se establece en findByIdAndSetFechaRecepcion().

        return pedidoRepository.save(pedidoEntity);
    }

    @Override
    @Transactional
    public boolean finalizarPedido(Integer pedidoId) {
        log.info("Intentando finalizar pedido con ID: {}", pedidoId);
        Optional<PedidoEntity> pedidoOptional = pedidoRepository.findById(pedidoId);

        if (pedidoOptional.isPresent()) {
            PedidoEntity pedido = pedidoOptional.get();

            // Aquí solo comprobamos "Completado" porque "Archivado" ya no existe.
            if ("Completado".equals(pedido.getEstado().getDescripcionEstado())) {
                log.warn("El pedido con ID {} ya está en estado 'Completado'. No se puede finalizar de nuevo.", pedidoId);
                return false;
            }

            Optional<EstadoPedidoEntity> estadoCompletadoOptional = estadoPedidoRepository.findByDescripcionEstado("Completado");

            if (estadoCompletadoOptional.isPresent()) {
                pedido.setEstado(estadoCompletadoOptional.get());
                // *** Aquí se establece la fecha_envio al concluir el pedido ***
                pedido.setFechaEnvio(LocalDate.now());
                log.info("Estableciendo fechaEnvio para el pedido {} al concluirlo.", pedido.getId());

                // Marcar todos los artículos como recibidos al finalizar el pedido
                pedido.getPedidoArticulos().forEach(pa -> pa.setRecibido(true));

                pedidoRepository.save(pedido);
                log.info("Pedido con ID {} finalizado a estado 'Completado' y FechaEnvio establecida.", pedidoId);
                return true;
            } else {
                log.error("Error: Estado 'Completado' no encontrado en la base de datos de estados de pedido. Asegúrate de que existe este registro.");
                return false;
            }
        } else {
            log.warn("Intento de finalizar pedido con ID {} fallido: Pedido no encontrado.", pedidoId);
            return false;
        }
    }

    @Override
    @Transactional
    public PedidoEntity addArticuloToPedido(Integer pedidoId, String articuloId, Integer cantidad) {
        log.info("Recibida petición para añadir/actualizar artículo {} (cantidad {}) en el pedido con ID {}", articuloId, cantidad, pedidoId);

        PedidoEntity pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId + ". No se puede añadir artículo a un pedido inexistente."));

        // Asegúrate de que "Archivado" no esté aquí si ya lo eliminaste.
        if ("Completado".equals(pedido.getEstado().getDescripcionEstado())) {
            throw new IllegalStateException("No se pueden añadir artículos a un pedido que ya está " + pedido.getEstado().getDescripcionEstado() + ".");
        }

        ArticuloEntity articulo = articuloRepository.findById(articuloId)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado con ID: " + articuloId));

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Double nuevoImporteTotalArticulo = articulo.getPrecio() * cantidad;

        Optional<PedidoArticuloEntity> existingPedidoArticuloOptional = pedido.getPedidoArticulos().stream()
                .filter(pa -> pa.getArticuloEntity().getId().equals(articuloId))
                .findFirst();

        if (existingPedidoArticuloOptional.isPresent()) {
            PedidoArticuloEntity existingPa = existingPedidoArticuloOptional.get();
            log.info("Artículo {} ya existe en pedido {}, actualizando cantidad de {} a {}",
                    articuloId, pedido.getId(), existingPa.getCantidadPedidoArticulo(), cantidad);
            existingPa.setCantidadPedidoArticulo(cantidad);
            existingPa.setImporteTotal(nuevoImporteTotalArticulo);
        } else {
            log.info("Añadiendo nuevo artículo {} al pedido {}", articuloId, pedido.getId());
            PedidoArticuloEntity nuevoPedidoArticulo = new PedidoArticuloEntity();
            nuevoPedidoArticulo.setArticuloEntity(articulo);
            nuevoPedidoArticulo.setCantidadPedidoArticulo(cantidad);
            nuevoPedidoArticulo.setImporteTotal(nuevoImporteTotalArticulo);
            nuevoPedidoArticulo.setRecibido(false);
            nuevoPedidoArticulo.setPedidoEntity(pedido);
            nuevoPedidoArticulo.setId(new PedidoArticuloIdEntity(articulo.getId(), pedido.getId()));
            pedido.addPedidoArticulo(nuevoPedidoArticulo);
        }

        // Si el pedido está en "Pendiente" y se le añade un artículo, pasa a "En tramite"
        // Usa "En tramite" sin tilde
        if (pedido.getEstado().getDescripcionEstado().equals("Pendiente") && !pedido.getPedidoArticulos().isEmpty()) {
            EstadoPedidoEntity enTramiteEstado = estadoPedidoRepository.findByDescripcionEstado("En tramite")
                    .orElseThrow(() -> new IllegalStateException("Estado 'En tramite' no encontrado. Asegúrate de que existe."));
            pedido.setEstado(enTramiteEstado);
            log.info("Estado del pedido {} cambiado a 'En tramite'.", pedido.getId());
        }

        return pedidoRepository.save(pedido);
    }

    @Override
    public List<ArticuloEntity> findAllArticulos() {
        return articuloRepository.findAll();
    }
}