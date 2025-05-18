package es.examplepb.findstockmanager.config; // Puedes ajustar el paquete

import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import es.examplepb.findstockmanager.repositorios.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional; // Importar Transaccional

import java.util.List;

@Configuration // Indica que esta clase contiene beans de configuración
public class InitialUserDataEncoder {

    private static final Logger log = LoggerFactory.getLogger(InitialUserDataEncoder.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Inyección de dependencias del repositorio de usuarios y el PasswordEncoder
    public InitialUserDataEncoder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean // Registra este método como un bean que se ejecutará al inicio
    public CommandLineRunner encodeInitialUserPasswords() {
        return args -> {
            log.info("Iniciando la codificación de contraseñas de usuarios iniciales...");

            // Obtener todos los usuarios que no tienen una contraseña codificada (ej. las de data.sql)
            // Esto asume que las contraseñas en texto plano NO empiezan con {bcrypt} o similar
            // Podrías necesitar ajustar la lógica si tienes otros formatos
            List<UsuarioEntity> usersToEncode = usuarioRepository.findAll().stream()
                    .filter(user -> user.getPassword() != null && !user.getPassword().startsWith("$2a$")) // Filtra por contraseñas que no parecen BCrypt
                    .toList();

            if (usersToEncode.isEmpty()) {
                log.info("No se encontraron usuarios con contraseñas en texto plano para codificar.");
                return;
            }

            log.info("Se encontraron {} usuarios con contraseñas en texto plano. Codificando...", usersToEncode.size());

            // Codificar y guardar cada usuario
            // Usamos @Transactional para asegurar que las actualizaciones se guarden
            encodeAndSaveUsers(usersToEncode);

            log.info("Codificación de contraseñas de usuarios iniciales completada.");
        };
    }

    // Método transaccional para codificar y guardar usuarios
    @Transactional
    public void encodeAndSaveUsers(List<UsuarioEntity> users) {
        for (UsuarioEntity user : users) {
            // Codifica la contraseña en texto plano
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            // Establece la contraseña codificada en la entidad
            user.setPassword(encodedPassword);
            // Guarda la entidad actualizada en la base de datos
            usuarioRepository.save(user);
            log.debug("Contraseña codificada para el usuario con email: {}", user.getEmail());
        }
    }
}
