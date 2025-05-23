package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.dto.UsuarioDto;
import es.examplepb.findstockmanager.entidades.AlmacenEntity;
import es.examplepb.findstockmanager.entidades.RolEntity;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import es.examplepb.findstockmanager.repositorios.AlmacenRepository;
import es.examplepb.findstockmanager.repositorios.RolRepository;
import es.examplepb.findstockmanager.repositorios.UsuarioRepository;
import es.examplepb.findstockmanager.mappers.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Lombok para inyección de dependencias del repositorio
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UsuarioRepository usuarioRepository;
    private final AlmacenRepository almacenRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    // Directorio donde guardar las fotos (configúralo en tus application.properties)
    //@Value("${upload.dir:src/main/resources/static/images/}") // Puedes inyectar esto si lo configuras en properties
    private String uploadDir = "src/main/resources/static/images/";

    @Transactional
    @Override
    public void save(final UsuarioDto usuarioDto) {
        //AlmacenEntity almacenEntity = almacenRepository.findByNombreAlmacen(usuarioDto.getNombreAlmacen());
        UsuarioEntity usuarioEntity = usuarioMapper.toEntity(usuarioDto);

        String rawPassword = usuarioDto.getPassword();
        // Codificar la contraseña usando el PasswordEncoder (BCryptPasswordEncoder)
        String encodedPassword = passwordEncoder.encode(rawPassword);
        // Asignar la contraseña codificada a la entidad
        usuarioEntity.setPassword(encodedPassword);

        // 2. Buscar y asignar la entidad Almacen usando el ID del DTO
        if (usuarioDto.getAlmacenId() != null) {
            // Usa findById que retorna Optional
            Optional<AlmacenEntity> almacenOptional = almacenRepository.findById(usuarioDto.getAlmacenId());
            if (almacenOptional.isPresent()) {
                usuarioEntity.setAlmacen(almacenOptional.get());
            } else {
                log.error("Almacen con ID {} no encontrado", usuarioDto.getAlmacenId());
                throw new RuntimeException("Almacen no encontrado con ID: " + usuarioDto.getAlmacenId());
            }
        } else {
            usuarioEntity.setAlmacen(null);
        }

        // 3. Buscar y asignar la entidad Rol usando el ID del DTO
        if (usuarioDto.getRolId() != null) {
            // Usa findById que retorna Optional (asumiendo RolRepository extiende JpaRepository<Rol, Integer>)
            Optional<RolEntity> rolOptional = rolRepository.findById(usuarioDto.getRolId());
            if (rolOptional.isPresent()) {
                usuarioEntity.setRolEntity(rolOptional.get());
            } else {
                // Manejar caso si el Rol con ese ID no existe
                log.error("Rol con ID {} no encontrado", usuarioDto.getRolId());
                throw new RuntimeException("Rol no encontrado con ID: " + usuarioDto.getRolId());
            }
        } else {
            usuarioEntity.setRolEntity(null); // O manejar según si el rol es obligatorio
        }

        // 4. Manejar la subida del archivo
        MultipartFile file = usuarioDto.getFotografiaUsuarioFile();
        if (file != null && !file.isEmpty()) {
            try {
                // Generar un nombre único para el archivo para evitar colisiones
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                // Crear la ruta completa donde se guardará el archivo
                Path uploadPath = Paths.get(uploadDir);
                // Asegurarse de que el directorio de subida existe
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                // Guardar el archivo en el sistema de archivos
                Files.copy(file.getInputStream(), filePath);
                // Guardar el nombre del archivo (o la ruta completa) en la entidad
                usuarioEntity.setFotografiaUsuario(fileName); // O filePath.toString() si guardas la ruta completa

            } catch (IOException e) {
                log.error("Error al guardar el archivo de fotografía", e);
                // Considera lanzar una excepción para que el controlador pueda manejarla
                throw new RuntimeException("Error al guardar el archivo de fotografía", e);
            }
        } else {
            usuarioEntity.setFotografiaUsuario(null);
        }

        log.info("UsuarioEntity ANTES de guardar: {}", usuarioEntity);

        // 5. Guardar la entidad UsuarioEntity en la base de datos
        usuarioRepository.save(usuarioEntity);
        log.info("Usuario guardado con éxito con ID: {}", usuarioEntity.getId());
    }
}
