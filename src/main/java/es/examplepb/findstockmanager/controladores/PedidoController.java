package es.examplepb.findstockmanager.controladores;

import es.examplepb.findstockmanager.entidades.Pedido;
import es.examplepb.findstockmanager.servicios.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Controller //intermediaria, la parte c del mvc
@Slf4j
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    // Método para mostrar la página de listado de pedidos
    // Mapea la petición GET a /pedidos/lista
    @GetMapping("/lista")
    public String listarPedidos(
            @RequestParam(required = false) List<String> estado, // Recibe los valores de los checkboxes 'estado'
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSolicitud, // Recibe la fecha y la convierte a LocalDate
            @RequestParam(required = false) String destinoTipo, // *** NUEVO: Recibe el tipo de destino ('tienda', 'almacen', o null) ***
            Model model) {

        log.info("Accediendo a la lista de pedidos. Filtros: estados={}, fechaSolicitud={}, destinoTipo={}", estado, fechaSolicitud, destinoTipo);

        List<Pedido> listaDePedidos;

        // Determina los filtros efectivos a usar para la llamada al servicio
        List<String> effectiveEstados = (estado != null && !estado.isEmpty()) ? estado : null;
        LocalDate effectiveFechaSolicitud = fechaSolicitud;
        String effectiveDestinoTipo = destinoTipo;


        // --- MODIFICACIÓN: Llama al método filtrado en el servicio con TODOS los posibles filtros ---
        // El servicio se encargará de la lógica de combinación.
        List<Pedido> fetchedPedidos;
        try {
            fetchedPedidos = pedidoService.findAllFiltered(effectiveEstados, effectiveFechaSolicitud, effectiveDestinoTipo);
        } catch (Exception e) {
            log.error("Error obteniendo lista de pedidos filtrada", e);
            fetchedPedidos = Collections.emptyList();
            model.addAttribute("errorMessage", "Hubo un error al cargar los pedidos.");
        }
        // --- FIN MODIFICACIÓN ---


        // --- ASEGURA que la lista añadida al modelo NUNCA es null ---
        listaDePedidos = (fetchedPedidos != null) ? fetchedPedidos : Collections.emptyList();
        // --- FIN ASEGURAMIENTO ---


        // Añade la lista de pedidos obtenida al modelo bajo el nombre "pedidos"
        model.addAttribute("pedidos", listaDePedidos);


        // Añade los valores de filtro recibidos de vuelta al modelo para que el formulario los muestre
        model.addAttribute("selectedEstados", (estado != null) ? estado : Collections.emptyList()); // Asegura que selectedEstados no sea null
        model.addAttribute("selectedFechaSolicitud", fechaSolicitud);
        model.addAttribute("selectedDestinoTipo", destinoTipo); // *** NUEVO: Añade el tipo de destino seleccionado ***

        // Define el título de la página basándose en el tipo de destino
        String pageTitle = "Listado General de Pedidos"; // Título por defecto
        if ("tienda".equals(destinoTipo)) {
            pageTitle = "Listado de Pedidos a Tienda";
        } else if ("almacen".equals(destinoTipo)) {
            pageTitle = "Listado de Pedidos a Almacén (Restock)";
        }
        // *** NUEVO: Añade el título de la página al modelo ***
        model.addAttribute("pageTitle", pageTitle);


        // Devuelve el nombre de la plantilla (usamos la misma para ambos tipos de listado)
        return "listaPedidos"; // O "listaPedidos" si renombraste el archivo HTML
    }


    // Método para mostrar los detalles de un pedido específico por su ID
    // Mapea GET a /pedidos/{id}
    @GetMapping("/{id}")
    public String verDetallePedido(@PathVariable Integer id, Model model) {
        log.info("Accediendo a detalles del pedido con ID: {}", id);
        // Necesitarás añadir un método findById en tu PedidoService e implementarlo
        Pedido pedido = pedidoService.findById(id); // Asume que tienes este método en tu servicio

        if (pedido != null) {
            model.addAttribute("pedido", pedido);
            // Retorna el nombre de la plantilla para mostrar los detalles de un pedido
            // Deberás crear esta plantilla (ej: detallePedido.html)
            return "detallePedido";
        } else {
            log.warn("Pedido con ID {} no encontrado", id);
            // Manejar caso donde el pedido no existe (ej: mostrar mensaje de error o redirigir)
            model.addAttribute("errorMessage", "Pedido no encontrado.");
            // Podrías redirigir a la lista de pedidos o a una página de error específica
            return "error"; // Asume que tienes una plantilla de error
        }
    }
}
