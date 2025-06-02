package es.examplepb.findstockmanager.controladores;


import es.examplepb.findstockmanager.entidades.*;
import es.examplepb.findstockmanager.repositorios.AlmacenRepository;
import es.examplepb.findstockmanager.repositorios.EstadoPedidoRepository;
import es.examplepb.findstockmanager.repositorios.TipoPedidoRepository;
import es.examplepb.findstockmanager.servicios.ArticuloService;
import es.examplepb.findstockmanager.servicios.PedidoService;
import es.examplepb.findstockmanager.servicios.UsuarioService;
import es.examplepb.findstockmanager.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/pedidos")

public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final ArticuloService articuloService;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final TipoPedidoRepository tipoPedidoRepository;
    private final AlmacenRepository almacenRepository;

// Método para mostrar la página de listado de pedidos

    @GetMapping("/lista")

    public String listarPedidos(

            @RequestParam(required = false) List<String> estado,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSolicitud,

            @RequestParam(required = false) String destinoTipo,

            HttpServletRequest request, Model model) {


        log.info("Accediendo a la lista de pedidos. Filtros: estados={}, fechaSolicitud={}, destinoTipo={}", estado, fechaSolicitud, destinoTipo);

        model.addAttribute("request", request);


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();


        Optional<UsuarioEntity> usuarioAutenticadoOptional = usuarioService.findByEmail(username);

        usuarioAutenticadoOptional.ifPresent(usuario -> model.addAttribute("usuarioAutenticado", usuario));


        List<PedidoEntity> listaDePedidoEntities;


        List<String> effectiveEstados = (estado != null && !estado.isEmpty()) ? estado : null;

        LocalDate effectiveFechaSolicitud = fechaSolicitud;

        String effectiveDestinoTipo = destinoTipo;


        List<PedidoEntity> fetchedPedidoEntities;

        try {

            fetchedPedidoEntities = pedidoService.findAllFiltered(effectiveEstados, effectiveFechaSolicitud, effectiveDestinoTipo);

        } catch (Exception e) {

            log.error("Error obteniendo lista de pedidos filtrada", e);

            fetchedPedidoEntities = Collections.emptyList();

            model.addAttribute("errorMessage", "Hubo un error al cargar los pedidos.");

        }


        listaDePedidoEntities = (fetchedPedidoEntities != null) ? fetchedPedidoEntities : Collections.emptyList();


        model.addAttribute("pedidos", listaDePedidoEntities);

        model.addAttribute("selectedEstados", (estado != null) ? estado : Collections.emptyList());

        model.addAttribute("selectedFechaSolicitud", fechaSolicitud);

        model.addAttribute("selectedDestinoTipo", destinoTipo);


        String pageTitle = "Listado General de Pedidos";

        if ("tienda".equals(destinoTipo)) {

            pageTitle = "Listado de Pedidos a Tienda";

        } else if ("almacen".equals(destinoTipo)) {

            pageTitle = "Listado de Pedidos a Almacén (Restock)";

        }

        model.addAttribute("pageTitle", pageTitle);


        return "listaPedidos";

    }


// Método para mostrar los detalles de un pedido específico por su ID

    @GetMapping("/{id}")

    public String verDetallePedido(@PathVariable Integer id,

                                   @RequestParam(required = false) String returnUrl,

                                   Model model,

                                   RedirectAttributes redirectAttributes) {

        log.info("Accediendo a detalles del pedido con ID: {}", id);

// *** CAMBIO CLAVE AQUÍ: Llamar al nuevo método para establecer fechaRecepcion ***

        Optional<PedidoEntity> pedidoOptional = pedidoService.findByIdAndSetFechaRecepcion(id);


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();


        Optional<UsuarioEntity> usuarioAutenticadoOptional = usuarioService.findByEmail(username);

        usuarioAutenticadoOptional.ifPresent(usuario -> model.addAttribute("usuarioAutenticado", usuario));


        if (pedidoOptional.isPresent()) {

            PedidoEntity pedidoEntity = pedidoOptional.get();

            model.addAttribute("pedidoEntity", pedidoEntity);


            List<PedidoArticuloEntity> articulosPedido = pedidoService.findArticulosByPedidoId(id);

            model.addAttribute("articulosPedido", articulosPedido);


            model.addAttribute("todosLosArticulosDisponibles", articuloService.findAll());


            model.addAttribute("returnToListUrl", returnUrl != null ? returnUrl : "/pedidos/lista");


        } else {

            log.warn("Pedido con ID {} no encontrado. Redirigiendo a la lista de pedidos.", id);

            redirectAttributes.addFlashAttribute("errorMessage", "El pedido solicitado no existe.");

            return "redirect:/pedidos/lista";

        }


        return "detallePedido";

    }


// Endpoint para AÑADIR/ACTUALIZAR un artículo a un pedido EXISTENTE

    @PostMapping("/{pedidoId}/add-articulo")

    public String addArticuloToPedido(@PathVariable Integer pedidoId,

                                      @RequestParam("articuloId") String articuloId,

                                      @RequestParam("cantidad") Integer cantidad,

                                      RedirectAttributes redirectAttributes) {

        log.info("Recibida petición para añadir/actualizar artículo {} (cantidad {}) al pedido con ID {}", articuloId, cantidad, pedidoId);

        try {

            PedidoEntity pedidoActualizado = pedidoService.addArticuloToPedido(pedidoId, articuloId, cantidad);

            redirectAttributes.addFlashAttribute("successMessage", "Artículo añadido/actualizado en el pedido " + pedidoActualizado.getId() + " exitosamente.");

            return "redirect:/pedidos/" + pedidoId;

        } catch (IllegalStateException e) {

            log.error("Error al añadir/actualizar artículo en el pedido {}: {}", pedidoId, e.getMessage(), e);

            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());

            return "redirect:/pedidos/" + pedidoId;

        } catch (RuntimeException e) {

            log.error("Error al añadir/actualizar artículo en el pedido {}: {}", pedidoId, e.getMessage(), e);

            redirectAttributes.addFlashAttribute("errorMessage", "Error al añadir/actualizar artículo: " + e.getMessage());

            return "redirect:/pedidos/" + pedidoId;

        }

    }


    @PostMapping("/concluir/{id}")

    @ResponseBody

    public ResponseEntity<?> concluirPedido(@PathVariable Integer id) {

        log.info("Intentando concluir el pedido con ID: {}", id);

        try {

            boolean exito = pedidoService.finalizarPedido(id);


            if (exito) {

                log.info("Pedido con ID {} concluido exitosamente y fecha de envío actualizada.", id);

                return ResponseEntity.ok().body(Map.of("message", "Pedido concluido exitosamente y fecha de envío establecida."));

            } else {

                log.warn("Fallo al concluir el pedido con ID {}. Posiblemente el pedido ya estaba finalizado o el estado 'Completado' no está configurado.", id);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "No se pudo concluir el pedido. Puede que ya estuviera finalizado o que haya un problema con el estado 'Completado'."));

            }

        } catch (Exception e) {

            log.error("Error inesperado al concluir el pedido con ID {}: {}", id, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno al concluir el pedido: " + e.getMessage()));

        }

    }

    static class ReposicionRequest {
        public String articuloId;
        public Integer cantidad;

        // Getters y Setters (o usar Lombok @Data)
        public String getArticuloId() {
            return articuloId;
        }

        public void setArticuloId(String articuloId) {
            this.articuloId = articuloId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }

    @PostMapping("/crear-reposicion-manual")
    @ResponseBody // Para devolver JSON
    public ResponseEntity<?> crearReposicionManual(@RequestBody ReposicionRequest request) {
        log.info("Recibida solicitud para crear reposición manual para artículo ID: {}, cantidad: {}", request.articuloId, request.cantidad);

        if (request.articuloId == null || request.cantidad == null || request.cantidad <= 0 || request.cantidad > 150) {
            return ResponseEntity.badRequest().body(Map.of("error", "Datos de solicitud inválidos. Asegúrate de proporcionar un ID de artículo y una cantidad entre 1 y 150."));
        }

        try {
            // 1. Obtener el Artículo
            ArticuloEntity articulo = articuloService.findById(request.articuloId);
            if (articulo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Artículo no encontrado con ID: " + request.articuloId));
            }

            // 2. Obtener el Tipo de Pedido "reposicion-stock-almacen"
            TipoPedidoEntity tipoPedido = tipoPedidoRepository.findByDescripcionTipo(Constants.TIPO_REPOSICION_STOCK_ALMACEN)
                    .orElseThrow(() -> new IllegalStateException("Tipo de pedido '" + Constants.TIPO_REPOSICION_STOCK_ALMACEN + "' no encontrado."));

            // 3. Obtener el Estado de Pedido "Pendiente"
            EstadoPedidoEntity estadoPendiente = estadoPedidoRepository.findByDescripcionEstado(Constants.ESTADO_PENDIENTE)
                    .orElseThrow(() -> new IllegalStateException("Estado de pedido '" + Constants.ESTADO_PENDIENTE + "' no encontrado."));

            // 4. Obtener el Almacén de Origen/Destino (asumo que es el mismo para reposición de stock de almacén)
            // Aquí necesitarás una lógica para obtener el almacén. Podría ser un almacén por defecto o el único que tienes.
            // Ejemplo: obtener el primer almacén, o un almacén con un ID conocido.
            AlmacenEntity almacenOrigenDestino = almacenRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("No se encontraron almacenes en la base de datos para crear el pedido."));


            // 5. Crear el PedidoEntity
            PedidoEntity nuevoPedido = new PedidoEntity();
            nuevoPedido.setTipo(tipoPedido);
            nuevoPedido.setEstado(estadoPendiente);
            nuevoPedido.setOrigenAlmacenEntity(almacenOrigenDestino); // Origen es el almacén
            nuevoPedido.setDestinoAlmacenEntity(almacenOrigenDestino); // Destino también es el almacén
            nuevoPedido.setOrigenTiendaEntity(null);
            nuevoPedido.setDestinoTiendaEntity(null);
            nuevoPedido.setFechaSolicitud(LocalDate.now());
            // FechaRecepcion y FechaEnvio serán null inicialmente, se llenarán al progresar el pedido

            // 6. Guardar el Pedido (esto lo haces con PedidoService.save)
            PedidoEntity pedidoGuardado = pedidoService.save(nuevoPedido);

            // 7. Añadir el artículo al pedido (reutilizando tu método existente)
            pedidoService.addArticuloToPedido(pedidoGuardado.getId(), request.articuloId, request.cantidad);

            log.info("Pedido de reposición manual creado con ID: {} para artículo ID: {} y cantidad: {}", pedidoGuardado.getId(), request.articuloId, request.cantidad);
            return ResponseEntity.ok().body(Map.of("message", "Pedido de reposición creado con éxito. ID de pedido: " + pedidoGuardado.getId()));

        } catch (Exception e) {
            log.error("Error al crear el pedido de reposición manual: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al crear el pedido de reposición: " + e.getMessage()));
        }
    }

}

