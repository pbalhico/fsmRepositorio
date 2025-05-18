package es.examplepb.findstockmanager.controladores;

import es.examplepb.findstockmanager.entidades.ArticuloEntity; // Importa tu entidad Articulo
import es.examplepb.findstockmanager.entidades.SeccionEntity; // Importa tu entidad Seccion
import es.examplepb.findstockmanager.entidades.UsuarioEntity; // Importa tu entidad Usuario
import es.examplepb.findstockmanager.servicios.ArticuloService; // Importa tu ArticuloService
import es.examplepb.findstockmanager.servicios.SeccionService; // Importa tu SeccionService (asumiendo que tienes uno)
import es.examplepb.findstockmanager.servicios.UsuarioService; // Importa tu UsuarioService
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication; // Importa Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Importa SecurityContextHolder
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional; // Importa Optional si findByEmail devuelve Optional

@RequiredArgsConstructor // Lombok generará el constructor con los campos 'final' para inyección de dependencias
@Controller
@Slf4j // Lombok para logging
@RequestMapping("/articulos") // Mapeo base para las URLs de artículos
public class ArticuloController {

    private final ArticuloService articuloService;
    private final UsuarioService usuarioService; // Para obtener el usuario autenticado
    private final SeccionService seccionService; // Para obtener la lista de secciones para el filtro

    @GetMapping("/lista")
    public String listarArticulos(
            @RequestParam(value = "seccionId", required = false) String seccionId,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "sortByStock", required = false) String sortByStock,
            Model model) {

        log.info("Accediendo a la lista de artículos. Filtros: seccionId={}, nombre={}, sortByStock={}", seccionId, nombre, sortByStock);

        // --- Código para obtener el usuario autenticado y añadirlo al modelo ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Obtiene el username (que es el email)

        // Busca la entidad Usuario completa usando el email
        // Asumiendo que usuarioService.findByEmail devuelve Optional<Usuario>
        Optional<UsuarioEntity> usuarioAutenticadoOptional = usuarioService.findByEmail(username);

        // Si el usuario se encuentra, lo añadimos al modelo
        usuarioAutenticadoOptional.ifPresent(usuario -> model.addAttribute("usuarioAutenticado", usuario));
        // --- Fin del código para obtener el usuario ---


        // Llama al servicio para obtener la lista de artículos filtrada y ordenada
        // Necesitarás implementar este método en tu ArticuloService
        List<ArticuloEntity> listaDeArticuloEntities = articuloService.findAllFilteredAndSorted(seccionId, nombre, sortByStock);

        // Obtiene la lista de secciones para poblar el dropdown de filtro
        // Necesitarás implementar este método en tu SeccionService
        List<SeccionEntity> listaDeSecciones = seccionService.findAll(); // Asumiendo que SeccionService tiene findAll()


        // Añade los datos al modelo para que la plantilla los muestre
        model.addAttribute("articulos", listaDeArticuloEntities);
        model.addAttribute("secciones", listaDeSecciones); // Añade la lista de secciones
        model.addAttribute("selectedSeccionId", seccionId); // Para mantener el filtro de sección seleccionado
        model.addAttribute("selectedNombre", nombre);       // Para mantener el filtro de nombre seleccionado
        model.addAttribute("selectedSortByStock", sortByStock); // Para mantener la opción de ordenación seleccionada


        // Devuelve el nombre de la plantilla Thymeleaf
        return "listaArticulos"; // Asegúrate de que este es el nombre correcto de tu archivo HTML
    }

    @GetMapping("/{id}")
    public String verDetalleArticulo(@PathVariable String id, Model model) {
        log.info("Accediendo a detalles del artículo con ID: {}", id);

        // --- Código para obtener el usuario autenticado y añadirlo al modelo ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<UsuarioEntity> usuarioAutenticadoOptional = usuarioService.findByEmail(username);
        usuarioAutenticadoOptional.ifPresent(usuario -> model.addAttribute("usuarioAutenticado", usuario));
        // --- Fin del código ---

        // Llama al servicio para obtener el artículo por su ID
        // Necesitarás añadir un método findById en tu ArticuloService e implementarlo
        ArticuloEntity articuloEntity = articuloService.findById(id); // Asume que tienes este método

        if (articuloEntity != null) {
            model.addAttribute("articulo", articuloEntity);
            // Retorna el nombre de la plantilla para mostrar los detalles del artículo
            return "articulo";
        } else {
            log.warn("Artículo con ID {} no encontrado", id);
            // Manejar caso donde el artículo no existe (ej: mostrar mensaje de error o redirigir)
            model.addAttribute("errorMessage", "Artículo no encontrado.");
            return "error"; // Asume que tienes una plantilla de error
        }
    }
}

