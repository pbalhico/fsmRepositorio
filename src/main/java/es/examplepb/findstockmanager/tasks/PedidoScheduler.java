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
    private final UsuarioRepository usuarioRepository;
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

        // Obtener el usuario del sistema
        Optional<UsuarioEntity> usuarioSistemaOpt = usuarioRepository.findByEmail(Constants.SYSTEM_USER_EMAIL);
        if (usuarioSistemaOpt.isEmpty()) {
            log.error("No se encontró el usuario del sistema con email '{}'. No se pueden generar pedidos de reposición.", Constants.SYSTEM_USER_EMAIL);
            return;
        }
        UsuarioEntity usuarioSistema = usuarioSistemaOpt.get();

        // Obtener el almacén de destino (el único almacén existente)
        Optional<AlmacenEntity> almacenOpt = almacenRepository.findById(Constants.ALMACEN_ID);
        if (almacenOpt.isEmpty()) {
            log.error("No se encontró el almacén con ID {}. No se pueden generar pedidos de reposición.", Constants.ALMACEN_ID);
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
            nuevoPedido.setUsuarioEntity(usuarioSistema);
            nuevoPedido.setFechaSolicitud(LocalDate.now());
            nuevoPedido.setFechaRecepcion(null);
            nuevoPedido.setFechaEnvio(null);

            PedidoEntity pedidoGuardado = pedidoRepository.save(nuevoPedido);
            log.info("Pedido de reposición creado con ID: {} para artículo: {}", pedidoGuardado.getId(), articulo.getNombreArticulo());

            // Crear la línea del pedido
            PedidoArticuloIdEntity pedidoArticuloId = new PedidoArticuloIdEntity(articulo.getId(), pedidoGuardado.getId());
            PedidoArticuloEntity lineaPedido = new PedidoArticuloEntity();
            lineaPedido.setId(pedidoArticuloId);
            // CORRECCIÓN: Establecer explícitamente las entidades relacionadas para la clave compuesta
            lineaPedido.setPedidoEntity(pedidoGuardado); // ¡IMPORTANTE! Reañadido
            lineaPedido.setArticuloEntity(articulo);     // ¡IMPORTANTE! Reañadido
            lineaPedido.setCantidadPedidoArticulo(cantidadNecesaria);
            lineaPedido.setImporteTotal(articulo.getPrecio() * cantidadNecesaria);

            pedidoArticuloRepository.save(lineaPedido);
            log.info("Línea de pedido de reposición creada para artículo {} con cantidad {}", articulo.getNombreArticulo(), cantidadNecesaria);

            // Opcional: Actualizar el stock del artículo si el pedido se considera "en proceso" o "completado"
            // Por ahora, solo se crea el pedido. La actualización de stock se haría al "procesar" el pedido.
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

        // Obtener el usuario del sistema
        Optional<UsuarioEntity> usuarioSistemaOpt = usuarioRepository.findByEmail(Constants.SYSTEM_USER_EMAIL);
        if (usuarioSistemaOpt.isEmpty()) {
            log.error("No se encontró el usuario del sistema con email '{}'. No se pueden generar pedidos a tienda.", Constants.SYSTEM_USER_EMAIL);
            return;
        }
        UsuarioEntity usuarioSistema = usuarioSistemaOpt.get();

        // Obtener el almacén de origen (el único almacén existente)
        Optional<AlmacenEntity> almacenOrigenOpt = almacenRepository.findById(Constants.ALMACEN_ID);
        if (almacenOrigenOpt.isEmpty()) {
            log.error("No se encontró el almacén con ID {}. No se pueden generar pedidos a tienda.", Constants.ALMACEN_ID);
            return;
        }
        AlmacenEntity almacenOrigen = almacenOrigenOpt.get();

        // Obtener la tienda de destino (la única tienda existente)
        Optional<TiendaEntity> tiendaDestinoOpt = tiendaRepository.findById(Constants.TIENDA_ID);
        if (tiendaDestinoOpt.isEmpty()) {
            log.error("No se encontró la tienda con ID {}. No se pueden generar pedidos a tienda.", Constants.TIENDA_ID);
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
        nuevoPedido.setUsuarioEntity(usuarioSistema);
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

            // Asegurarse de no añadir el mismo artículo dos veces en el mismo pedido si no se desea
            if (!articulosSeleccionados.contains(articuloAleatorio)) {
                articulosSeleccionados.add(articuloAleatorio);
            } else {
                // Si ya está, intenta otra vez o simplemente ignora si ya se han hecho suficientes intentos
                // Para este ejemplo, simplemente permite duplicados o intenta un par de veces más.
                // Para evitar bucles infinitos en listas pequeñas, se podría limitar los intentos.
                if (todosLosArticulos.size() > articulosSeleccionados.size()) { // Evitar bucle infinito si todos son iguales
                    i--; // Decrementa i para intentar añadir otro artículo
                }
            }
        }

        for (ArticuloEntity articulo : articulosSeleccionados) {
            // Cantidad aleatoria entre 1 y MAX_UNIDADES_POR_ARTICULO_PEDIDO_TIENDA (100)
            int cantidad = random.nextInt(Constants.MAX_UNIDADES_POR_ARTICULO_PEDIDO_TIENDA) + 1;

            // Crear la línea del pedido
            PedidoArticuloIdEntity pedidoArticuloId = new PedidoArticuloIdEntity(articulo.getId(), pedidoGuardado.getId());
            PedidoArticuloEntity lineaPedido = new PedidoArticuloEntity();
            lineaPedido.setId(pedidoArticuloId);
            // CORRECCIÓN: Establecer explícitamente las entidades relacionadas para la clave compuesta
            lineaPedido.setPedidoEntity(pedidoGuardado); // ¡IMPORTANTE! Reañadido
            lineaPedido.setArticuloEntity(articulo);     // ¡IMPORTANTE! Reañadido
            lineaPedido.setCantidadPedidoArticulo(cantidad);
            lineaPedido.setImporteTotal(articulo.getPrecio() * cantidad);

            pedidoArticuloRepository.save(lineaPedido);
            log.info("Línea de pedido a tienda creada para artículo {} con cantidad {}", articulo.getNombreArticulo(), cantidad);

            // Opcional: Reducir el stock del artículo si el pedido se considera "en proceso" o "enviado"
            // Por ahora, solo se crea el pedido. La reducción de stock se haría al "procesar" el pedido.
        }
        log.info("Tarea programada: Generación de Pedidos a Tienda finalizada.");
    }
}
