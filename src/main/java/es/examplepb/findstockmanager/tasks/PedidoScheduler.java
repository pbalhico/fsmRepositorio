// src/main/java/es/examplepb/findstockmanager/tasks/PedidoScheduler.java
package es.examplepb.findstockmanager.tasks;

import es.examplepb.findstockmanager.entidades.AlmacenEntity;
import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import es.examplepb.findstockmanager.entidades.EstadoPedidoEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloIdEntity;
import es.examplepb.findstockmanager.entidades.PedidoEntity;
import es.examplepb.findstockmanager.entidades.TiendaEntity;
import es.examplepb.findstockmanager.entidades.TipoPedidoEntity;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import es.examplepb.findstockmanager.repositorios.AlmacenRepository;
import es.examplepb.findstockmanager.repositorios.ArticuloRepository;
import es.examplepb.findstockmanager.repositorios.EstadoPedidoRepository;
import es.examplepb.findstockmanager.repositorios.PedidoArticuloRepository;
import es.examplepb.findstockmanager.repositorios.PedidoRepository;
import es.examplepb.findstockmanager.repositorios.TiendaRepository;
import es.examplepb.findstockmanager.repositorios.TipoPedidoRepository;
import es.examplepb.findstockmanager.repositorios.UsuarioRepository;
import es.examplepb.findstockmanager.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Clase que contiene las tareas programadas para la generación automática de pedidos.
 * Incluye la lógica para pedidos de reposición de stock y pedidos a tienda.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoScheduler {

    private final ArticuloRepository articuloRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoArticuloRepository pedidoArticuloRepository;
    private final TipoPedidoRepository tipoPedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final UsuarioRepository usuarioRepository; // Se mantiene si se usa para buscar el usuario del sistema
    private final AlmacenRepository almacenRepository;
    private final TiendaRepository tiendaRepository;

    private final Random random = new Random();

    /**
     * Tarea programada para generar pedidos de reposición de stock al almacén.
     * Se ejecuta periódicamente según la configuración en application.properties.
     * Busca artículos cuyo stock esté por debajo del mínimo definido (100 unidades).
     */
    @Scheduled(fixedRateString = "${app.scheduling.reponer-stock.fixed-rate}")
    @Transactional
    public void generarPedidosReposicionStock() {
        log.info("Iniciando tarea programada: Generar Pedidos de Reposición de Stock.");

        // Obtener el tipo de pedido "reposicion stock almacen"
        Optional<TipoPedidoEntity> tipoReposicionOpt = tipoPedidoRepository.findByDescripcionTipo(Constants.TIPO_REPOSICION_STOCK_ALMACEN);
        if (tipoReposicionOpt.isEmpty()) {
            log.error("No se encontró el tipo de pedido '{}'. No se pueden generar pedidos de reposición.", Constants.TIPO_REPOSICION_STOCK_ALMACEN);
            return;
        }
        TipoPedidoEntity tipoReposicion = tipoReposicionOpt.get();

        // Obtener el estado "Pendiente"
        Optional<EstadoPedidoEntity> estadoPendienteOpt = estadoPedidoRepository.findByDescripcionEstado(Constants.ESTADO_PENDIENTE);
        if (estadoPendienteOpt.isEmpty()) {
            log.error("No se encontró el estado de pedido '{}'. No se pueden generar pedidos de reposición.", Constants.ESTADO_PENDIENTE);
            return;
        }
        EstadoPedidoEntity estadoPendiente = estadoPendienteOpt.get();

        // Obtener el usuario del sistema (se mantiene la búsqueda si se necesita para otros fines, pero no se asigna al Pedido)
        Optional<UsuarioEntity> usuarioSistemaOpt = usuarioRepository.findByEmail(Constants.SYSTEM_USER_EMAIL);
        if (usuarioSistemaOpt.isEmpty()) {
            log.error("No se encontró el usuario del sistema con email '{}'. No se pueden generar pedidos de reposición.", Constants.SYSTEM_USER_EMAIL);
            return;
        }
        UsuarioEntity usuarioSistema = usuarioSistemaOpt.get(); // Se obtiene, pero no se usará para setear en PedidoEntity

        // Obtener el almacén de destino (el único almacén existente)
        // CORREGIDO: Buscar por nombre en lugar de ID fijo
        Optional<AlmacenEntity> almacenOpt = almacenRepository.findByNombreAlmacen("Almacén Principal");
        if (almacenOpt.isEmpty()) {
            log.error("No se encontró el almacén con nombre 'Almacén Principal'. No se pueden generar pedidos de reposición.");
            return;
        }
        AlmacenEntity almacenDestino = almacenOpt.get();


        List<ArticuloEntity> articulosBajoStock = articuloRepository.findByStockLessThan(Constants.STOCK_MINIMO_REPOSICION);

        if (articulosBajoStock.isEmpty()) {
            log.info("No hay artículos con stock por debajo de {}. No se generaron pedidos de reposición.", Constants.STOCK_MINIMO_REPOSICION);
            return;
        }

        log.info("Se encontraron {} artículos con stock bajo.", articulosBajoStock.size());

        for (ArticuloEntity articulo : articulosBajoStock) {
            int cantidadNecesaria = Constants.STOCK_MINIMO_REPOSICION - articulo.getStock();

            // Crear un nuevo pedido de reposición
            PedidoEntity nuevoPedido = new PedidoEntity();
            nuevoPedido.setTipo(tipoReposicion);
            nuevoPedido.setEstado(estadoPendiente);
            nuevoPedido.setOrigenTiendaEntity(null);
            nuevoPedido.setDestinoTiendaEntity(null);
            nuevoPedido.setOrigenAlmacenEntity(almacenDestino); // Origen es el mismo almacén para reposición
            nuevoPedido.setDestinoAlmacenEntity(almacenDestino); // Destino es el almacén
            // ELIMINADO: nuevoPedido.setUsuarioEntity(usuarioSistema); // Esta línea ha sido eliminada
            nuevoPedido.setFechaSolicitud(LocalDate.now());
            nuevoPedido.setFechaRecepcion(null);
            nuevoPedido.setFechaEnvio(null);

            PedidoEntity pedidoGuardado = pedidoRepository.save(nuevoPedido);
            log.info("Pedido de reposición creado con ID: {} para artículo: {}", pedidoGuardado.getId(), articulo.getNombreArticulo());

            // Crear la línea del pedido
            PedidoArticuloIdEntity pedidoArticuloId = new PedidoArticuloIdEntity(articulo.getId(), pedidoGuardado.getId());
            PedidoArticuloEntity lineaPedido = new PedidoArticuloEntity();
            lineaPedido.setId(pedidoArticuloId);
            lineaPedido.setPedidoEntity(pedidoGuardado);
            lineaPedido.setArticuloEntity(articulo);
            lineaPedido.setCantidadPedidoArticulo(cantidadNecesaria);
            lineaPedido.setImporteTotal(articulo.getPrecio() * cantidadNecesaria);
            lineaPedido.setRecibido(false); // Inicialmente no recibido

            pedidoArticuloRepository.save(lineaPedido);
            log.info("Línea de pedido de reposición creada para artículo {} con cantidad {}", articulo.getNombreArticulo(), cantidadNecesaria);
        }
        log.info("Tarea programada: Generación de Pedidos de Reposición de Stock finalizada.");
    }

    /**
     * Tarea programada para generar pedidos aleatorios a tienda.
     * Se ejecuta periódicamente según la configuración en application.properties.
     * Genera un pedido con 1 a 3 artículos aleatorios, con cantidades aleatorias.
     */
    @Scheduled(fixedRateString = "${app.scheduling.generar-pedidos-tienda.fixed-rate}")
    @Transactional
    public void generarPedidosATienda() {
        log.info("Iniciando tarea programada: Generar Pedidos a Tienda.");

        List<ArticuloEntity> todosLosArticulos = articuloRepository.findAll();
        if (todosLosArticulos.isEmpty()) {
            log.warn("No hay artículos disponibles para generar pedidos a tienda.");
            return;
        }

        // Obtener el tipo de pedido "almacen-tienda"
        Optional<TipoPedidoEntity> tipoTiendaOpt = tipoPedidoRepository.findByDescripcionTipo(Constants.TIPO_ALMACEN_TIENDA);
        if (tipoTiendaOpt.isEmpty()) {
            log.error("No se encontró el tipo de pedido '{}'. No se pueden generar pedidos a tienda.", Constants.TIPO_ALMACEN_TIENDA);
            return;
        }
        TipoPedidoEntity tipoTienda = tipoTiendaOpt.get();

        // Obtener el estado "Pendiente"
        Optional<EstadoPedidoEntity> estadoPendienteOpt = estadoPedidoRepository.findByDescripcionEstado(Constants.ESTADO_PENDIENTE);
        if (estadoPendienteOpt.isEmpty()) {
            log.error("No se encontró el estado de pedido '{}'. No se pueden generar pedidos a tienda.", Constants.ESTADO_PENDIENTE);
            return;
        }
        EstadoPedidoEntity estadoPendiente = estadoPendienteOpt.get();

        // Obtener el usuario del sistema (se mantiene la búsqueda si se necesita para otros fines, pero no se asigna al Pedido)
        Optional<UsuarioEntity> usuarioSistemaOpt = usuarioRepository.findByEmail(Constants.SYSTEM_USER_EMAIL);
        if (usuarioSistemaOpt.isEmpty()) {
            log.error("No se encontró el usuario del sistema con email '{}'. No se pueden generar pedidos a tienda.", Constants.SYSTEM_USER_EMAIL);
            return;
        }
        UsuarioEntity usuarioSistema = usuarioSistemaOpt.get(); // Se obtiene, pero no se usará para setear en PedidoEntity

        // Obtener el almacén de origen (el único almacén existente)
        // CORREGIDO: Buscar por nombre en lugar de ID fijo
        Optional<AlmacenEntity> almacenOrigenOpt = almacenRepository.findByNombreAlmacen("Almacén Principal");
        if (almacenOrigenOpt.isEmpty()) {
            log.error("No se encontró el almacén con nombre 'Almacén Principal'. No se pueden generar pedidos a tienda.");
            return;
        }
        AlmacenEntity almacenOrigen = almacenOrigenOpt.get();

        // Obtener la tienda de destino (la única tienda existente)
        // CORREGIDO: Buscar por nombre en lugar de ID fijo
        Optional<TiendaEntity> tiendaDestinoOpt = tiendaRepository.findByNombreTienda("Tienda A");
        if (tiendaDestinoOpt.isEmpty()) {
            log.error("No se encontró la tienda con nombre 'Tienda A'. No se pueden generar pedidos a tienda.");
            return;
        }
        TiendaEntity tiendaDestino = tiendaDestinoOpt.get();


        // Crear un nuevo pedido a tienda
        PedidoEntity nuevoPedido = new PedidoEntity();
        nuevoPedido.setTipo(tipoTienda);
        nuevoPedido.setEstado(estadoPendiente);
        nuevoPedido.setOrigenTiendaEntity(null);
        nuevoPedido.setDestinoTiendaEntity(tiendaDestino); // Destino es la tienda
        nuevoPedido.setOrigenAlmacenEntity(almacenOrigen); // Origen es el almacén
        nuevoPedido.setDestinoAlmacenEntity(null);
        // ELIMINADO: nuevoPedido.setUsuarioEntity(usuarioSistema); // Esta línea ha sido eliminada
        nuevoPedido.setFechaSolicitud(LocalDate.now());
        nuevoPedido.setFechaRecepcion(null);
        nuevoPedido.setFechaEnvio(null);

        PedidoEntity pedidoGuardado = pedidoRepository.save(nuevoPedido);
        log.info("Pedido a tienda creado con ID: {}", pedidoGuardado.getId());

        // Generar entre 1 y 3 líneas de pedido aleatorias
        int numArticulosEnPedido = random.nextInt(3) + 1; // 1, 2 o 3 artículos
        List<ArticuloEntity> articulosSeleccionados = new ArrayList<>();
        for (int i = 0; i < numArticulosEnPedido; i++) {
            if (todosLosArticulos.isEmpty()) break; // No hay más artículos para seleccionar
            int randomIndex = random.nextInt(todosLosArticulos.size());
            ArticuloEntity articuloAleatorio = todosLosArticulos.get(randomIndex);

            if (!articulosSeleccionados.contains(articuloAleatorio)) {
                articulosSeleccionados.add(articuloAleatorio);
            } else {
                if (todosLosArticulos.size() > articulosSeleccionados.size()) {
                    i--;
                }
            }
        }

        for (ArticuloEntity articulo : articulosSeleccionados) {
            int cantidad = random.nextInt(Constants.MAX_UNIDADES_POR_ARTICULO_PEDIDO_TIENDA) + 1;

            PedidoArticuloIdEntity pedidoArticuloId = new PedidoArticuloIdEntity(articulo.getId(), pedidoGuardado.getId());
            PedidoArticuloEntity lineaPedido = new PedidoArticuloEntity();
            lineaPedido.setId(pedidoArticuloId);
            lineaPedido.setPedidoEntity(pedidoGuardado);
            lineaPedido.setArticuloEntity(articulo);
            lineaPedido.setCantidadPedidoArticulo(cantidad);
            lineaPedido.setImporteTotal(articulo.getPrecio() * cantidad);
            lineaPedido.setRecibido(false); // Inicialmente no recibido

            pedidoArticuloRepository.save(lineaPedido);
            log.info("Línea de pedido a tienda creada para artículo {} con cantidad {}", articulo.getNombreArticulo(), cantidad);
        }
        log.info("Tarea programada: Generación de Pedidos a Tienda finalizada.");
    }
}
