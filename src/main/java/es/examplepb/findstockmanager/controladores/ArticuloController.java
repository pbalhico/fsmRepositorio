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

@RequiredArgsConstructor
@Controller
@Slf4j // Lombok para logging
@RequestMapping("/articulos") // Mapeo base para las URLs de artículos
public class ArticuloController {

    private final ArticuloService articuloService;
    private final UsuarioService usuarioService;
    private final SeccionService seccionService;

    @GetMapping("/lista")
    public String listarArticulos(
            @RequestParam(value = "seccionId", required = false) String seccionId,
            @RequestParam(value = "idArticulo", required = false) String idArticulo,
            @RequestParam(value = "sortByStock", required = false) String sortByStock,
            Model model) {

        log.info("Accediendo a la lista de artículos. Filtros: seccionId={}, idArticulo={}, sortByStock={}", seccionId, idArticulo, sortByStock);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Obtiene el username (que es el email)

        Optional<UsuarioEntity> usuarioAutenticadoOptional = usuarioService.findByEmail(username);

        usuarioAutenticadoOptional.ifPresent(usuario -> model.addAttribute("usuarioAutenticado", usuario));

        List<ArticuloEntity> listaDeArticuloEntities = articuloService.findAllFilteredAndSorted(seccionId, idArticulo, sortByStock);

        List<SeccionEntity> listaDeSecciones = seccionService.findAll();

        model.addAttribute("articulos", listaDeArticuloEntities);
        model.addAttribute("secciones", listaDeSecciones);
        model.addAttribute("selectedSeccionId", seccionId);
        model.addAttribute("selectedIdArticulo", idArticulo);
        model.addAttribute("selectedSortByStock", sortByStock);

        return "listaArticulos";
    }

    @GetMapping("/{id}")
    public String verDetalleArticulo(@PathVariable String id, Model model) {
        log.info("Accediendo a detalles del artículo con ID: {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<UsuarioEntity> usuarioAutenticadoOptional = usuarioService.findByEmail(username);
        usuarioAutenticadoOptional.ifPresent(usuario -> model.addAttribute("usuarioAutenticado", usuario));

        ArticuloEntity articuloEntity = articuloService.findById(id);

        if (articuloEntity != null) {
            model.addAttribute("articuloEntity", articuloEntity);
            List<String> tallasDisponibles = List.of("XS", "S", "M", "L", "XL", "XXL"); // O cargarlas dinámicamente
            model.addAttribute("tallasDisponibles", tallasDisponibles);
            return "articulo";
        } else {
            log.warn("Artículo con ID {} no encontrado", id);
            model.addAttribute("errorMessage", "Artículo no encontrado.");
            return "error";
        }
    }
}

