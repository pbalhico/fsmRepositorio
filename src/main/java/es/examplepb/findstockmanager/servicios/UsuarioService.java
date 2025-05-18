package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import es.examplepb.findstockmanager.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- Importar PasswordEncoder

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // <-- Inyectar PasswordEncoder

    // Inyección de dependencias del repositorio de usuarios y el PasswordEncoder
    @Autowired // @Autowired es opcional si solo hay un constructor, pero lo dejamos por claridad
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) { // <-- Añadir PasswordEncoder al constructor
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder; // <-- Asignar el PasswordEncoder inyectado
    }

    public List<UsuarioEntity> findAll() {
        return usuarioRepository.findAll();
    }

    public UsuarioEntity validarCredenciales(String email, String password) {
        // Usamos findByEmail que devuelve Optional
        Optional<UsuarioEntity> usuarioOptional = usuarioRepository.findByEmail(email);

        // Verificamos si el Optional contiene un usuario
        if (usuarioOptional.isPresent()) {
            UsuarioEntity usuarioEntity = usuarioOptional.get(); // Obtenemos el usuario del Optional

            // ** CAMBIO AQUÍ: Usar passwordEncoder.matches() para verificar la contraseña **
            // Compara la contraseña en texto plano (introducida por el usuario) con el hash almacenado en la DB
            if (passwordEncoder.matches(password, usuarioEntity.getPassword())) {
                return usuarioEntity; // Retorna el usuario si las credenciales son válidas
            }
        }
        return null; // Retorna null si no se encuentra el usuario o la contraseña no coincide
    }

    public Optional<UsuarioEntity> findByEmail(String email) {
        // El repositorio ya devuelve Optional, simplemente lo retornamos
        return usuarioRepository.findByEmail(email);
    }

    // Podrías añadir un método para guardar usuarios aquí si no usas AdminService para eso,
    // pero si usas AdminService, asegúrate de que ese método también codifica la contraseña.
    // (Ya hemos hecho ese cambio en AdminServiceImpl)
}
