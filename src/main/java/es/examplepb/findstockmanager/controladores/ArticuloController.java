package es.examplepb.findstockmanager.controladores;

import es.examplepb.findstockmanager.dto.ArticuloDto;
import es.examplepb.findstockmanager.entidades.ArticuloEntity; // Importa tu entidad Articulo
import es.examplepb.findstockmanager.entidades.SeccionEntity; // Importa tu entidad Seccion
import es.examplepb.findstockmanager.entidades.UsuarioEntity; // Importa tu entidad Usuario
import es.examplepb.findstockmanager.servicios.ArticuloService; // Importa tu ArticuloService
import es.examplepb.findstockmanager.servicios.FileStorageService;
import es.examplepb.findstockmanager.servicios.SeccionService; // Importa tu SeccionService (asumiendo que tienes uno)
import es.examplepb.findstockmanager.servicios.UsuarioService; // Importa tu UsuarioService
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Importa Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Importa SecurityContextHolder
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional; // Importa Optional si findByEmail devuelve Optional
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@Slf4j // Lombok para logging
@RequestMapping("/articulos") // Mapeo base para las URLs de artículos
public class ArticuloController {

    private final ArticuloService articuloService;
    private final UsuarioService usuarioService;
    private final SeccionService seccionService;
    private final FileStorageService fileStorageService; // Asegúrate de tener este servicio para manejar archivos

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

        List<SeccionEntity> listaDeSecciones = seccionService.findAll(); // Necesitas un seccionService.findAll()

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

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoArticulo(Model model) {
        // ELIMINAMOS LA LÓGICA DE COMPROBACIÓN MANUAL DEL ROL
        /*
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            log.warn("Intento de acceso no autorizado al formulario de nuevo artículo por: {}", authentication != null ? authentication.getName() : "Anónimo");
            return "redirect:/error/acceso-denegado";
        }
        */
        log.info("Mostrando formulario para nuevo artículo (acceso para todos).");
        model.addAttribute("articuloDto", new ArticuloDto());
        List<SeccionEntity> listaDeSecciones = seccionService.findAll();
        model.addAttribute("listaSecciones", listaDeSecciones);

        return "nuevoArticulo";
    }
    
    @PostMapping("/nuevo")
    public String procesarFormularioNuevoArticulo(
            @Valid @ModelAttribute("articuloDto") ArticuloDto articuloDto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            log.warn("Errores de validación en el formulario de nuevo artículo: {}", result.getAllErrors());
            List<SeccionEntity> listaDeSecciones = seccionService.findAll();
            model.addAttribute("listaSecciones", listaDeSecciones);
            return "nuevoArticulo";
        }

        // Lógica de subida de la imagen
        String nombreFotoGuardada = null;
        if (articuloDto.getFotografiaArtFile() != null && !articuloDto.getFotografiaArtFile().isEmpty()) {
            try {
                // Si tienes un FileStorageService, úsalo:
                nombreFotoGuardada = fileStorageService.saveImage(articuloDto.getFotografiaArtFile());
                // Si NO tienes FileStorageService y mantienes la lógica aquí, sería así:
                /*
                String originalFilename = articuloDto.getFotografiaArtFile().getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                nombreFotoGuardada = UUID.randomUUID().toString() + extension;
                Path path = Paths.get(uploadPath + nombreFotoGuardada); // Necesitarías @Value("${upload.path.images}") uploadPath;
                Files.write(path, articuloDto.getFotografiaArtFile().getBytes());
                log.info("Imagen subida con éxito: {}", nombreFotoGuardada);
                */
            } catch (IOException e) {
                log.error("Error al guardar la fotografía del artículo: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar la fotografía del artículo.");
                return "redirect:/articulos/nuevo";
            }
        } else {
            // Si no se sube ninguna imagen, puedes asignar un valor por defecto o null
            articuloDto.setFotografiaArt("default.png"); // O el nombre de tu imagen por defecto, o null
        }
        articuloDto.setFotografiaArt(nombreFotoGuardada); // Establece el nombre del archivo en el DTO

        try {
            articuloService.saveNewArticulo(articuloDto);
            log.info("Artículo {} guardado con éxito.", articuloDto.getId());
            redirectAttributes.addFlashAttribute("articuloAgregadoExito", true);
            redirectAttributes.addFlashAttribute("mensajeExito", "Artículo '" + articuloDto.getNombreArticulo() + "' agregado correctamente.");
        } catch (Exception e) {
            log.error("Error al guardar el artículo {}: {}", articuloDto.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("articuloAgregadoExito", false);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al agregar el artículo: " + e.getMessage());
            return "redirect:/articulos/nuevo";
        }

        return "redirect:/articulos/nuevo";
    }
}

