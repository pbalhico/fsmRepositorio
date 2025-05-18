package es.examplepb.findstockmanager.controladores;

import es.examplepb.findstockmanager.entidades.PedidoEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import es.examplepb.findstockmanager.servicios.PedidoService;
import es.examplepb.findstockmanager.servicios.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller //intermediaria, la parte c del mvc
@Slf4j
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService; // Inyecta el UsuarioService

    // Método para mostrar la página de listado de pedidos
    // Mapea la petición GET a /pedidos/lista
    @GetMapping("/lista")
    public String listarPedidos(
            @RequestParam(required = false) List<String> estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSolicitud,
            @RequestParam(required = false) String destinoTipo, Model model) {

        log.info("Accediendo a la lista de pedidos. Filtros: estados={}, fechaSolicitud={}, destinoTipo={}", estado, fechaSolicitud, destinoTipo);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Obtiene el username (que es el email)

        Optional<UsuarioEntity> usuarioAutenticadoOptional = usuarioService.findByEmail(username);

        // Si el usuario se encuentra, lo añadimos al modelo
        usuarioAutenticadoOptional.ifPresent(usuario -> model.addAttribute("usuarioAutenticado", usuario));

        List<PedidoEntity> listaDePedidoEntities;

        // Determina los filtros efectivos a usar para la llamada al servicio
        List<String> effectiveEstados = (estado != null && !estado.isEmpty()) ? estado : null;
        LocalDate effectiveFechaSolicitud = fechaSolicitud;
        String effectiveDestinoTipo = destinoTipo;


        // Llama al método filtrado en el servicio con TODOS los posibles filtros
        // El servicio se encargará de la lógica de combinación.
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
        model.addAttribute("selectedDestinoTipo", destinoTipo); // Añade el tipo de destino seleccionado

        // Define el título de la página basándose en el tipo de destino
        String pageTitle = "Listado General de Pedidos"; // Título por defecto
        if ("tienda".equals(destinoTipo)) {
            pageTitle = "Listado de Pedidos a Tienda";
        } else if ("almacen".equals(destinoTipo)) {
            pageTitle = "Listado de Pedidos a Almacén (Restock)";
        }
        // Añade el título de la página al modelo
        model.addAttribute("pageTitle", pageTitle);


        // Devuelve el nombre de la plantilla (usamos la misma para ambos tipos de listado)
        return "listaPedidos"; // O "listaPedidos" si renombraste el archivo HTML
    }


    // Método para mostrar los detalles de un pedido específico por su ID
    // Mapea GET a /pedidos/{id}
    @GetMapping("/{id}")
    public String verDetallePedido(@PathVariable Integer id, Model model) {
        log.info("Accediendo a detalles del pedido con ID: {}", id);
        PedidoEntity pedidoEntity = pedidoService.findById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Obtiene el username (que es el email)

        Optional<UsuarioEntity> usuarioAutenticadoOptional = usuarioService.findByEmail(username);

        usuarioAutenticadoOptional.ifPresent(usuario -> model.addAttribute("usuarioAutenticado", usuario));

        if (pedidoEntity != null) {
            model.addAttribute("pedido", pedidoEntity);

            // --- INICIO: Código para obtener los artículos del pedido y añadirlos al modelo ---
            // Asumiendo que PedidoService tiene un método para encontrar PedidoArticulo por ID de pedido
            List<PedidoArticuloEntity> articulosPedido = pedidoService.findArticulosByPedidoId(id);

            // Añade la lista de PedidoArticulo al modelo
            model.addAttribute("articulosPedido", articulosPedido);
            // --- FIN: Código para obtener los artículos del pedido ---


            // Retorna el nombre de la plantilla para mostrar los detalles de un pedido
            return "detallePedido";
        } else {
            log.warn("Pedido con ID {} no encontrado", id);
            model.addAttribute("errorMessage", "Pedido no encontrado.");
            return "error"; // Asume que tienes una plantilla de error
        }
    }
}
