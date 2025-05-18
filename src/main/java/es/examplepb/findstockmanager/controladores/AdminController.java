package es.examplepb.findstockmanager.controladores;

import es.examplepb.findstockmanager.dto.UsuarioDto;
import es.examplepb.findstockmanager.entidades.AlmacenEntity;
import es.examplepb.findstockmanager.entidades.RolEntity;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import es.examplepb.findstockmanager.repositorios.AlmacenRepository;
import es.examplepb.findstockmanager.repositorios.RolRepository;
import es.examplepb.findstockmanager.repositorios.UsuarioRepository;
import es.examplepb.findstockmanager.servicios.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        // 1. Obtener todos los roles de la base de datos
        List<RolEntity> listaDeTodosLosRoles = rolRepository.findAll();
        log.info("Roles encontrados para el desplegable: {}", listaDeTodosLosRoles.size());


        // 2. Obtener todos los almacenes de la base de datos (para el otro desplegable)
        List<AlmacenEntity> listaDeTodosLosAlmacenes = almacenRepository.findAll();
        log.info("Almacenes encontrados para el desplegable: {}", listaDeTodosLosAlmacenes.size());


        // 3. Crear un objeto UsuarioDto vacío para enlazar con el formulario
        // La plantilla espera un objeto llamado 'usuario' (th:object="${usuario}")
        UsuarioDto nuevoUsuarioDto = new UsuarioDto();


        // 4. Añadir las listas de roles, almacenes y el objeto usuario vacío al modelo
        model.addAttribute("listaRoles", listaDeTodosLosRoles);
        model.addAttribute("listaAlmacenes", listaDeTodosLosAlmacenes);
        model.addAttribute("usuarioDto", nuevoUsuarioDto); // Añade el objeto usuario vacío


        // 5. Retornar el nombre de la plantilla HTML
        return "nuevoUser"; // Retorna la vista del formulario de nuevo usuario
    }

    // Aquí irían otros métodos del AdminController, por ejemplo, para guardar el nuevo usuario (POST /admin/usuarios)
    @PostMapping("/usuarios")
    public String guardarNuevoUsuario(UsuarioDto nuevoUsuarioDto) {
        log.info("Guardando nuevo usuario: {}", nuevoUsuarioDto);

        // Guardar el nuevo usuario en la base de datos
        adminService.save(nuevoUsuarioDto);

        // Redirigir a la lista de usuarios o a otra página
        //return "redirect:/admin/usuarios";
        log.info("Nuevo usuario guardado correctamente: {}", nuevoUsuarioDto);
        return "redirect:/index";
    }

}
