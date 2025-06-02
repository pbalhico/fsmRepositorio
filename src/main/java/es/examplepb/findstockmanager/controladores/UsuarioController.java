package es.examplepb.findstockmanager.controladores;

import es.examplepb.findstockmanager.servicios.UsuarioService;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Optional;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Si tienes otros métodos relacionados con usuarios, puedes moverlos aquí del MainController
    // Por ejemplo, si tenías un método para "mis pedidos" o "mis direcciones"

    @GetMapping("/perfil")
    public String mostrarPerfilUsuario(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // El email es el username en tu caso

        Optional<UsuarioEntity> usuarioOptional = usuarioService.findByEmail(username);

        if (usuarioOptional.isPresent()) {
            UsuarioEntity usuario = usuarioOptional.get();
            model.addAttribute("usuario", usuario);
            return "perfilUsuario"; // Asegúrate de que esta ruta de plantilla sea correcta
        } else {
            return "redirect:/error?message=Usuario no encontrado";
        }
    }
}

