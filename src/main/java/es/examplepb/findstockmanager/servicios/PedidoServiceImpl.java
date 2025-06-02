package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.*;
import es.examplepb.findstockmanager.repositorios.*;
import es.examplepb.findstockmanager.util.Constants; // Asegúrate de que esta importación sea correcta
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

    @Override
    @Transactional
    public Optional<PedidoEntity> findByIdAndSetFechaRecepcion(Integer id) {
        Optional<PedidoEntity> pedidoOptional = pedidoRepository.findById(id);
        if (pedidoOptional.isPresent()) {
            PedidoEntity pedido = pedidoOptional.get();

            // Solo establecemos fechaRecepcion si es un pedido de reposición de stock (o similar)
            // y aún no tiene fecha de recepción. Esto indica la llegada física de la mercancía al almacén.
            if (pedido.getTipo().getDescripcionTipo().equals(Constants.TIPO_REPOSICION_STOCK_ALMACEN) && pedido.getFechaRecepcion() == null) {
                pedido.setFechaRecepcion(LocalDate.now());
                log.info("Pedido de reposición con ID {} abierto por primera vez. Fecha de recepción de mercancía en almacén establecida.", id);
            }

            EstadoPedidoEntity estadoActual = pedido.getEstado();
            // Si el pedido está "Pendiente" y se abre (por primera vez), lo pasamos a "En tramite".
            // No cambiamos el estado si ya está "En tramite" o "Completado".
            if (estadoActual != null && Constants.ESTADO_PENDIENTE.equals(estadoActual.getDescripcionEstado())) {
                Optional<EstadoPedidoEntity> enTramiteEstadoOptional = estadoPedidoRepository.findByDescripcionEstado(Constants.ESTADO_ENTRAMITE);
                if (enTramiteEstadoOptional.isPresent()) {
                    pedido.setEstado(enTramiteEstadoOptional.get());
                    log.info("Estado del pedido con ID {} cambiado a 'En tramite'.", id);
                } else {
                    log.error("Error: Estado '{}' no encontrado en la base de datos. Asegúrate de que existe este registro.", Constants.ESTADO_ENTRAMITE);
                }
            }
            pedidoRepository.save(pedido);
        }
        return pedidoOptional;
    }

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
    @Transactional(readOnly = true)
    public List<PedidoArticuloEntity> findArticulosByPedidoId(Integer pedidoId) {
        log.info("Buscando artículos para el pedido con ID: {}", pedidoId);
        // Usa el método del repositorio que carga el ArticuloEntity
        return pedidoArticuloRepository.findById_PedidoId(pedidoId);
    }

    @Override
    @Transactional
    public PedidoEntity save(PedidoEntity pedidoEntity) {
        log.info("Guardando o actualizando pedido con ID: {}", pedidoEntity.getId());

        if (pedidoEntity.getId() == null) {
            log.info("Estableciendo fechaSolicitud para un nuevo pedido.");
            pedidoEntity.setFechaSolicitud(LocalDate.now());
            if (pedidoEntity.getEstado() == null) {
                estadoPedidoRepository.findByDescripcionEstado(Constants.ESTADO_PENDIENTE)
                        .ifPresent(pedidoEntity::setEstado);
            }
        }

        return pedidoRepository.save(pedidoEntity);
    }

    @Override
    @Transactional
    public boolean finalizarPedido(Integer pedidoId) {
        log.info("Intentando finalizar pedido con ID: {}", pedidoId);
        Optional<PedidoEntity> pedidoOptional = pedidoRepository.findById(pedidoId);

        if (pedidoOptional.isEmpty()) {
            log.warn("Pedido con ID {} no encontrado para finalizar.", pedidoId);
            return false;
        }

        PedidoEntity pedido = pedidoOptional.get();

        // Obtener el estado 'Completado'
        Optional<EstadoPedidoEntity> estadoCompletadoOptional = estadoPedidoRepository.findByDescripcionEstado(Constants.ESTADO_COMPLETADO);
        if (estadoCompletadoOptional.isEmpty()) {
            log.error("Error: El estado '{}' no está configurado en la base de datos de estados de pedido. Asegúrate de que existe este registro.", Constants.ESTADO_COMPLETADO);
            return false;
        }
        EstadoPedidoEntity estadoCompletado = estadoCompletadoOptional.get();

        // Verificar si el pedido ya está en un estado final (Completado)
        if (pedido.getEstado().getDescripcionEstado().equals(Constants.ESTADO_COMPLETADO)) {
            log.warn("El pedido con ID {} ya está en estado final ('{}'). No se puede finalizar de nuevo.", pedidoId, pedido.getEstado().getDescripcionEstado());
            return false;
        }

        // Establecer la fecha de envío al finalizar el pedido, si no está ya establecida
        if (pedido.getFechaEnvio() == null) {
            pedido.setFechaEnvio(LocalDate.now());
            log.info("Fecha de envío establecida para el pedido {} al concluirlo.", pedido.getId());
        }

        // ***************************************************************************************************
        // LÓGICA CLAVE PARA LA ACTUALIZACIÓN DE STOCK BASADA EN EL TIPO DE PEDIDO
        // ***************************************************************************************************

        // Obtener los artículos del pedido. PedidoArticuloRepository usa @EntityGraph para cargar ArticuloEntity
        List<PedidoArticuloEntity> articulosDelPedido = pedidoArticuloRepository.findById_PedidoId(pedido.getId());

        if (pedido.getTipo().getDescripcionTipo().equals(Constants.TIPO_REPOSICION_STOCK_ALMACEN)) {
            // Lógica para pedidos de REPOSICIÓN DE STOCK (Almacén a Almacén): EL STOCK AUMENTA
            log.info("Pedido {} es de tipo '{}'. Aumentando stock de artículos...", pedidoId, Constants.TIPO_REPOSICION_STOCK_ALMACEN);
            for (PedidoArticuloEntity pa : articulosDelPedido) {
                ArticuloEntity articulo = pa.getArticuloEntity();
                int cantidadRepuesta = pa.getCantidadPedidoArticulo();

                if (articulo != null) {
                    int nuevoStock = articulo.getStock() + cantidadRepuesta;
                    articulo.setStock(nuevoStock);
                    articuloRepository.save(articulo);
                    pa.setRecibido(true); // Marcar como recibido al completar la reposición
                    log.info("Artículo '{}' (ID: {}) stock actualizado de {} a {} (añadidos {} unidades).",
                            articulo.getNombreArticulo(), articulo.getId(), (nuevoStock - cantidadRepuesta), nuevoStock, cantidadRepuesta);
                } else {
                    log.warn("Artículo nulo encontrado en la línea de pedido para el pedido ID {}. No se pudo actualizar el stock.", pedidoId);
                }
            }
        } else if (pedido.getTipo().getDescripcionTipo().equals(Constants.TIPO_ALMACEN_TIENDA)) {
            // Lógica para pedidos a TIENDA (Almacén a Tienda): EL STOCK DISMINUYE EN EL ALMACÉN DE ORIGEN
            // Asumo que el "concluir" aquí significa que el almacén ya ha enviado la mercancía.
            log.info("Pedido {} es de tipo '{}' (pedido a tienda). Disminuyendo stock de artículos en almacén de origen...", pedidoId, Constants.TIPO_ALMACEN_TIENDA);
            for (PedidoArticuloEntity pa : articulosDelPedido) {
                ArticuloEntity articulo = pa.getArticuloEntity();
                int cantidadEnviada = pa.getCantidadPedidoArticulo();

                if (articulo != null) {
                    if (articulo.getStock() >= cantidadEnviada) {
                        int nuevoStock = articulo.getStock() - cantidadEnviada;
                        articulo.setStock(nuevoStock);
                        articuloRepository.save(articulo);
                        pa.setRecibido(true); // Marcar como enviado/recibido (desde la perspectiva del almacén)
                        log.info("Artículo '{}' (ID: {}) stock actualizado de {} a {} (restados {} unidades).",
                                articulo.getNombreArticulo(), articulo.getId(), (nuevoStock + cantidadEnviada), nuevoStock, cantidadEnviada);
                    } else {
                        log.error("Stock insuficiente para el artículo '{}' (ID: {}) en el almacén. Stock actual: {}, Cantidad pedida: {}. El pedido no se pudo completar completamente.",
                                articulo.getNombreArticulo(), articulo.getId(), articulo.getStock(), cantidadEnviada);
                        // IMPORTANTE: Considera lanzar una excepción aquí para revertir la transacción si el stock es insuficiente.
                        // throw new IllegalStateException("Stock insuficiente para el artículo: " + articulo.getNombreArticulo());
                    }
                } else {
                    log.warn("Artículo nulo encontrado en la línea de pedido para el pedido ID {}. No se pudo actualizar el stock para la disminución.", pedidoId);
                }
            }
        } else {
            log.warn("Tipo de pedido desconocido ('{}') para el pedido con ID {}. No se realizó ninguna actualización de stock.",
                    pedido.getTipo().getDescripcionTipo(), pedidoId);
        }

        // Cambiar el estado del pedido a "Completado"
        pedido.setEstado(estadoCompletado);
        pedidoRepository.save(pedido);
        log.info("Pedido con ID {} finalizado a estado '{}'.", pedidoId, Constants.ESTADO_COMPLETADO);

        return true;
    }

    @Override
    @Transactional // Asegúrate de que este método sea transaccional
    public PedidoEntity addArticuloToPedido(Integer pedidoId, String articuloId, Integer cantidad) {
        log.info("Intentando añadir/actualizar artículo {} (cantidad {}) al pedido con ID {}", articuloId, cantidad, pedidoId);

        // 1. Obtener el Pedido existente (debe estar gestionado por la transacción)
        PedidoEntity pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalStateException("Pedido con ID " + pedidoId + " no encontrado."));

        // 2. Obtener el Artículo
        ArticuloEntity articulo = articuloRepository.findById(articuloId)
                .orElseThrow(() -> new IllegalStateException("Artículo con ID " + articuloId + " no encontrado."));

        // 3. Buscar si el PedidoArticulo ya existe en la colección del pedido
        Optional<PedidoArticuloEntity> existingPedidoArticuloOpt = pedido.getPedidoArticulos().stream()
                .filter(pa -> pa.getArticuloEntity().getId().equals(articuloId))
                .findFirst();

        if (existingPedidoArticuloOpt.isPresent()) {
            // Si el artículo ya está en el pedido, actualizar la cantidad
            PedidoArticuloEntity existingPedidoArticulo = existingPedidoArticuloOpt.get();
            existingPedidoArticulo.setCantidadPedidoArticulo(existingPedidoArticulo.getCantidadPedidoArticulo() + cantidad);
            existingPedidoArticulo.setImporteTotal(existingPedidoArticulo.getCantidadPedidoArticulo() * articulo.getPrecio());
            log.info("Artículo {} actualizado en el pedido {}. Nueva cantidad: {}", articuloId, pedidoId, existingPedidoArticulo.getCantidadPedidoArticulo());
            // No es necesario llamar a pedido.addPedidoArticulo aquí, ya que el objeto ya está en la colección
            // y Hibernate detectará los cambios en el objeto gestionado.
        } else {
            // Si el artículo no está en el pedido, crear una nueva entrada
            PedidoArticuloIdEntity nuevoPedidoArticuloId = new PedidoArticuloIdEntity(articuloId, pedidoId);
            PedidoArticuloEntity nuevoPedidoArticulo = new PedidoArticuloEntity();
            nuevoPedidoArticulo.setId(nuevoPedidoArticuloId);
            nuevoPedidoArticulo.setArticuloEntity(articulo);
            nuevoPedidoArticulo.setCantidadPedidoArticulo(cantidad);
            nuevoPedidoArticulo.setImporteTotal(cantidad * articulo.getPrecio());
            nuevoPedidoArticulo.setRecibido(false); // Por defecto, no recibido al añadir

            // **CRÍTICO**: Usar el método helper de PedidoEntity para añadir a la colección
            // y establecer la relación bidireccional correctamente.
            pedido.addPedidoArticulo(nuevoPedidoArticulo);
            log.info("Artículo {} añadido como nuevo al pedido {}. Cantidad: {}", articuloId, pedidoId, cantidad);

            // No es necesario guardar PedidoArticuloRepository.save(nuevoPedidoArticulo) aquí
            // porque PedidoEntity tiene CascadeType.ALL, y el save del pedido lo gestionará.
        }

        // 4. Guardar el Pedido (Hibernate gestionará los cambios en la colección)
        // El save aquí es importante para asegurar que los cambios en la colección
        // se persistan, aunque si el método es @Transactional, a veces basta con que termine la transacción.
        return pedidoRepository.save(pedido);
    }


    @Override
    public List<ArticuloEntity> findAllArticulos() {
        return articuloRepository.findAll();
    }
}