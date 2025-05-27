package es.examplepb.findstockmanager.controladores;

import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import es.examplepb.findstockmanager.entidades.PedidoEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import es.examplepb.findstockmanager.servicios.ArticuloService;
import es.examplepb.findstockmanager.servicios.PedidoService;
import es.examplepb.findstockmanager.servicios.UsuarioService;
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
}