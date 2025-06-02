package es.examplepb.findstockmanager.controladores;

import es.examplepb.findstockmanager.dto.UsuarioDto;
import es.examplepb.findstockmanager.entidades.AlmacenEntity;
import es.examplepb.findstockmanager.entidades.RolEntity;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import es.examplepb.findstockmanager.repositorios.AlmacenRepository;
import es.examplepb.findstockmanager.repositorios.RolRepository;
import es.examplepb.findstockmanager.servicios.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Importar RedirectAttributes

import java.util.List;

@RequiredArgsConstructor
@Controller //intermediaria, la parte c del mvc
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final RolRepository rolRepository;
    private final AlmacenRepository almacenRepository;
    private final AdminService adminService;

    @GetMapping("/usuarios")
    public String mostrarFormularioNuevoUsuario(Model model) {
        log.info("Accediendo al formulario de nuevo usuario.");

        List<RolEntity> listaDeTodosLosRoles = rolRepository.findAll();
        log.info("Roles encontrados para el desplegable: {}", listaDeTodosLosRoles.size());

        List<AlmacenEntity> listaDeTodosLosAlmacenes = almacenRepository.findAll();
        log.info("Almacenes encontrados para el desplegable: {}", listaDeTodosLosAlmacenes.size());

        UsuarioDto nuevoUsuarioDto = new UsuarioDto();

        model.addAttribute("listaRoles", listaDeTodosLosRoles);
        model.addAttribute("listaAlmacenes", listaDeTodosLosAlmacenes);
        model.addAttribute("usuarioDto", nuevoUsuarioDto);

        return "nuevoUser";
    }

    @PostMapping("/usuarios")
    // Añadimos RedirectAttributes para pasar atributos después de una redirección
    public String guardarNuevoUsuario(UsuarioDto nuevoUsuarioDto, RedirectAttributes redirectAttributes) {
        log.info("Guardando nuevo usuario: {}", nuevoUsuarioDto);

        // Guardar el nuevo usuario en la base de datos
        adminService.save(nuevoUsuarioDto);

        log.info("Nuevo usuario guardado correctamente: {}", nuevoUsuarioDto);

        // Añadimos un atributo flash para indicar éxito. Este atributo estará disponible
        // solo en la siguiente petición (después de la redirección).
        redirectAttributes.addFlashAttribute("usuarioAgregadoExito", true);
        redirectAttributes.addFlashAttribute("mensajeExito", "Usuario agregado correctamente a la plantilla");


        // Redirigimos a la página de nuevo usuario. Aquí es donde se mostrará el modal.
        // Si rediriges directamente a /index, el modal no se mostrará en la página /index,
        // sino que el atributo "usuarioAgregadoExito" estaría disponible en /index si la
        // manejaras para mostrar allí.
        // Para que el modal se muestre en 'nuevoUser.html' antes de redirigir a /index,
        // necesitamos redirigir a 'nuevoUser' y que allí el JavaScript dispare el modal
        // y luego haga la redirección final.
        return "redirect:/admin/usuarios"; // Redirige de vuelta al formulario para mostrar el modal
    }
}