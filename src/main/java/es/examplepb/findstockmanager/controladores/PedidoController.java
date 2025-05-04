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

    @GetMapping("/lista")
    public String listarPedidos(
            @RequestParam(required = false) List<String> estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSolicitud,
            Model model) {

        log.info("Accediendo a la lista de pedidos. Filtros: estados={}, fechaSolicitud={}", estado, fechaSolicitud);

        List<Pedido> listaDePedidos;

        // Determina los filtros efectivos a usar para la llamada al servicio
        List<String> effectiveEstados = (estado != null && !estado.isEmpty()) ? estado : null;
        LocalDate effectiveFechaSolicitud = fechaSolicitud;


        // Llama al método del servicio con los filtros efectivos
        if (effectiveEstados == null && effectiveFechaSolicitud == null) {
            listaDePedidos = pedidoService.findAll(); // Obtiene todos si no hay filtros
        } else {
            listaDePedidos = pedidoService.findAllFiltered(effectiveEstados, effectiveFechaSolicitud); // Llama al método filtrado
        }

        // Añade la lista de pedidos obtenida al modelo
        model.addAttribute("pedidos", listaDePedidos);

        // --- FIX ---
        // Añade los valores de filtro recibidos de vuelta al modelo para que el formulario los muestre.
        // ASEGURA que selectedEstados siempre sea una List (vacía si el parámetro 'estado' fue null o vacío).
        model.addAttribute("selectedEstados", (estado != null) ? estado : Collections.emptyList());
        // --- FIN FIX ---

        model.addAttribute("selectedFechaSolicitud", fechaSolicitud);

        // Devuelve el nombre de la plantilla
        return "listaPedidos";
    }


    @GetMapping("/{id}")
    public String verDetallePedido(@PathVariable Integer id, Model model) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido != null) {
            model.addAttribute("pedido", pedido);
            return "detallePedido";
        } else {
            // Manejar caso donde el pedido no existe
            return "error"; // O redirigir a lista
        }
    }
}
