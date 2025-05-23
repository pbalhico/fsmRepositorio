// src/main/java/es/examplepb/findstockmanager/util/DataLoader.java
package es.examplepb.findstockmanager.util;

import es.examplepb.findstockmanager.entidades.AlmacenEntity;
import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import es.examplepb.findstockmanager.entidades.EstadoPedidoEntity;
import es.examplepb.findstockmanager.entidades.RolEntity;
import es.examplepb.findstockmanager.entidades.SeccionEntity;
import es.examplepb.findstockmanager.entidades.TiendaEntity;
import es.examplepb.findstockmanager.entidades.TipoPedidoEntity;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import es.examplepb.findstockmanager.repositorios.AlmacenRepository;
import es.examplepb.findstockmanager.repositorios.ArticuloRepository;
import es.examplepb.findstockmanager.repositorios.EstadoPedidoRepository;
import es.examplepb.findstockmanager.repositorios.RolRepository;
import es.examplepb.findstockmanager.repositorios.SeccionRepository;
import es.examplepb.findstockmanager.repositorios.TiendaRepository;
import es.examplepb.findstockmanager.repositorios.TipoPedidoRepository;
import es.examplepb.findstockmanager.repositorios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * DataLoader para inicializar datos en la base de datos al inicio de la aplicación.
 * Esto es útil para asegurar que los datos base (roles, estados, tipos de pedido,
 * usuario del sistema, etc.) estén siempre presentes, especialmente en entornos de desarrollo
 * o cuando se usa una base de datos en memoria como H2.
 * <p>
 * NOTA: Para H2 en modo archivo con `spring.sql.init.mode=always`, este DataLoader
 * complementa `data.sql`. `data.sql` se encarga de la mayoría de los inserts,
 * pero este DataLoader puede añadir lógica para asegurar la existencia de elementos
 * críticos como el usuario del sistema o para pre-poblar datos si no se usa `data.sql`.
 * En este caso, se usa para asegurar la existencia del usuario 'system' y los roles/estados/tipos
 * si por alguna razón data.sql no los crea o para validarlos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final TipoPedidoRepository tipoPedidoRepository;
    private final AlmacenRepository almacenRepository;
    private final TiendaRepository tiendaRepository;
    private final ArticuloRepository articuloRepository;
    private final SeccionRepository seccionRepository;
    private final PasswordEncoder passwordEncoder; // Para encriptar contraseñas

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Iniciando carga de datos iniciales...");

        // Asegurar que los roles existan
        createRolIfNotFound(Constants.ROL_SYSTEM);
        createRolIfNotFound("manager");
        createRolIfNotFound("empleado");

        // Asegurar que los estados de pedido existan
        createEstadoPedidoIfNotFound(Constants.ESTADO_PENDIENTE);
        createEstadoPedidoIfNotFound(Constants.ESTADO_COMPLETADO);
        createEstadoPedidoIfNotFound(Constants.ESTADO_ENVIADO);
        createEstadoPedidoIfNotFound(Constants.ESTADO_CANCELADO);
        createEstadoPedidoIfNotFound(Constants.ESTADO_EN_PROCESO);

        // Asegurar que los tipos de pedido existan
        createTipoPedidoIfNotFound(Constants.TIPO_TIENDA_ALMACEN);
        createTipoPedidoIfNotFound(Constants.TIPO_ALMACEN_TIENDA);
        createTipoPedidoIfNotFound(Constants.TIPO_REPOSICION_STOCK_ALMACEN);

        // Asegurar que el usuario del sistema exista
        createSystemUserIfNotFound();

        // Asegurar que el almacén y la tienda por defecto existan (si no están en data.sql o si se usa un ID fijo)
        createDefaultAlmacenIfNotFound();
        createDefaultTiendaIfNotFound();

        log.info("Carga de datos iniciales finalizada.");
    }

    private void createRolIfNotFound(String tipoRol) {
        rolRepository.findByTipoRol(tipoRol).orElseGet(() -> {
            log.info("Creando rol: {}", tipoRol);
            return rolRepository.save(new RolEntity(tipoRol));
        });
    }

    private void createEstadoPedidoIfNotFound(String descripcion) {
        estadoPedidoRepository.findByDescripcionEstado(descripcion).orElseGet(() -> {
            log.info("Creando estado de pedido: {}", descripcion);
            return estadoPedidoRepository.save(new EstadoPedidoEntity(descripcion));
        });
    }

    private void createTipoPedidoIfNotFound(String descripcionTipo) {
        tipoPedidoRepository.findByDescripcionTipo(descripcionTipo).orElseGet(() -> {
            log.info("Creando tipo de pedido: {}", descripcionTipo);
            return tipoPedidoRepository.save(new TipoPedidoEntity(descripcionTipo));
        });
    }

    private void createSystemUserIfNotFound() {
        // CORRECCIÓN: Buscar usuario por email en lugar de ID fijo para evitar conflictos con auto-generación de IDs
        usuarioRepository.findByEmail("system@findstock.com").orElseGet(() -> {
            log.info("Creando usuario del sistema...");
            RolEntity systemRol = rolRepository.findByTipoRol(Constants.ROL_SYSTEM)
                    .orElseThrow(() -> new IllegalStateException("Rol 'system' no encontrado."));

            // Usar el patrón builder para crear el usuario, permitiendo que el ID sea auto-generado
            UsuarioEntity systemUser = UsuarioEntity.builder()
                    .rolEntity(systemRol)
                    .nombre("Sistema")
                    .apellido("Automático") // Añadido un apellido para completar la entidad
                    .email("system@findstock.com")
                    .password(passwordEncoder.encode("systempassword")) // Contraseña encriptada
                    .fotografiaUsuario("images/system.jpg") // Opcional: ruta a una imagen por defecto
                    .build();
            return usuarioRepository.save(systemUser);
        });
    }

    private void createDefaultAlmacenIfNotFound() {
        almacenRepository.findById(Constants.ALMACEN_ID).orElseGet(() -> {
            log.info("Creando almacén por defecto con ID: {}", Constants.ALMACEN_ID);
            // Necesitarás una Sección para el almacén. Si no existe, crea una genérica.
            SeccionEntity seccionGenerica = articuloRepository.findById("CAMS001001") // Usar un artículo existente para obtener una sección
                    .map(ArticuloEntity::getSeccionEntity)
                    .orElseGet(() -> {
                        log.warn("No se encontró una sección existente para el almacén por defecto. Creando una nueva.");
                        return seccionRepository.save(new SeccionEntity(null, "Seccion por Defecto"));
                    });

            AlmacenEntity defaultAlmacen = new AlmacenEntity(Constants.ALMACEN_ID, seccionGenerica, "Almacén Principal", "Dirección del Almacén");
            return almacenRepository.save(defaultAlmacen);
        });
    }

    private void createDefaultTiendaIfNotFound() {
        tiendaRepository.findById(Constants.TIENDA_ID).orElseGet(() -> {
            log.info("Creando tienda por defecto con ID: {}", Constants.TIENDA_ID);
            TiendaEntity defaultTienda = new TiendaEntity(Constants.TIENDA_ID, "Tienda Principal", "Dirección de la Tienda");
            return tiendaRepository.save(defaultTienda);
        });
    }
}
