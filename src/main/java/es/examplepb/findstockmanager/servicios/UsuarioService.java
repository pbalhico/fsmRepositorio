package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.Usuario;
import es.examplepb.findstockmanager.repositorios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
     private final UsuarioRepository usuarioRepository;

     public UsuarioService(UsuarioRepository usuarioRepository) {
         this.usuarioRepository = usuarioRepository;
     }

     public List<Usuario> findAll() {
         return usuarioRepository.findAll();
     }

    public Usuario validarCredenciales(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        return null;
    }
}
