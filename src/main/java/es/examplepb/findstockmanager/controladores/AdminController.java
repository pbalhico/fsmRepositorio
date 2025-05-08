package es.examplepb.findstockmanager.controladores;

import es.examplepb.findstockmanager.entidades.Almacen;
import es.examplepb.findstockmanager.entidades.Rol;
import es.examplepb.findstockmanager.entidades.Usuario;
import es.examplepb.findstockmanager.repositorios.AlmacenRepository;
import es.examplepb.findstockmanager.repositorios.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller //intermediaria, la parte c del mvc
@Slf4j
@RequestMapping("/admin")
public class AdminController {
    private final RolRepository rolRepository;
    private final AlmacenRepository almacenRepository;

    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevoUsuario(Model model) {
        log.info("Accediendo al formulario de nuevo usuario.");

        // 1. Obtener todos los roles de la base de datos
        List<Rol> listaDeTodosLosRoles = rolRepository.findAll();
        log.info("Roles encontrados para el desplegable: {}", listaDeTodosLosRoles.size());


        // 2. Obtener todos los almacenes de la base de datos (para el otro desplegable)
        List<Almacen> listaDeTodosLosAlmacenes = almacenRepository.findAll();
        log.info("Almacenes encontrados para el desplegable: {}", listaDeTodosLosAlmacenes.size());


        // 3. Crear un objeto Usuario vacío para enlazar con el formulario
        // La plantilla espera un objeto llamado 'usuario' (th:object="${usuario}")
        Usuario nuevoUsuario = new Usuario();


        // 4. Añadir las listas de roles, almacenes y el objeto usuario vacío al modelo
        model.addAttribute("listaRoles", listaDeTodosLosRoles);
        model.addAttribute("listaAlmacenes", listaDeTodosLosAlmacenes);
        model.addAttribute("usuario", nuevoUsuario); // Añade el objeto usuario vacío


        // 5. Retornar el nombre de la plantilla HTML
        return "nuevoUser"; // Retorna la vista del formulario de nuevo usuario
    }

    // Aquí irían otros métodos del AdminController, por ejemplo, para guardar el nuevo usuario (POST /admin/usuarios)
    // @PostMapping("/usuarios")
    // public String guardarNuevoUsuario(@ModelAttribute Usuario usuario, Model model) { ... }

}
