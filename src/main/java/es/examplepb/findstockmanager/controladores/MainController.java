package es.examplepb.findstockmanager.controladores;

import es.examplepb.findstockmanager.entidades.Usuario;
import es.examplepb.findstockmanager.servicios.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller //intermediaria, la parte c del mvc
@Slf4j
@RequestMapping("/")
public class MainController {
    private final UsuarioService usuarioService;

    @GetMapping({"", "/index"})
    public String bienvenida(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String usuario = authentication.getName();
            log.info("Bienvenido, {}", usuario);
            model.addAttribute("mensaje", "Bienvenido, " + usuario + "!");
        } else {
            model.addAttribute("mensaje", "Por favor, inicia sesión.");
        }
        return "index";
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login"; // Devuelve la vista del formulario de login
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                                @RequestParam String password,
                                Model model) {
        Usuario usuario = usuarioService.validarCredenciales(email, password);
        if (usuario != null) {
            log.info("Inicio de sesión exitoso para el usuario: {}", usuario.getEmail());
            model.addAttribute("usuario", usuario);
            return "redirect:/"; // Redirige a la página principal
        } else {
            log.warn("Credenciales inválidas para el email: {}", email);
            model.addAttribute("error", "Credenciales inválidas. Inténtalo de nuevo.");
            return "login"; // Vuelve al formulario de login con un mensaje de error
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Opcional: Obtener la autenticación actual (no estrictamente necesario solo para invalidar)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            // Invalidar la sesión HTTP
            request.getSession().invalidate();
            // Limpiar el contexto de seguridad (aunque invalidar la sesión a menudo lo hace)
            SecurityContextHolder.clearContext();
        }
        // Redirigir a la página de login con un parámetro para indicar logout
        return "redirect:/login?logout";
    }

}
