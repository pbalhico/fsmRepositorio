package es.examplepb.findstockmanager.util;

import es.examplepb.findstockmanager.entidades.AlmacenEntity;
import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import es.examplepb.findstockmanager.entidades.EstadoPedidoEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloIdEntity;
import es.examplepb.findstockmanager.entidades.PedidoEntity;
import es.examplepb.findstockmanager.entidades.RolEntity;
import es.examplepb.findstockmanager.entidades.SeccionEntity;
import es.examplepb.findstockmanager.entidades.TiendaEntity;
import es.examplepb.findstockmanager.entidades.TipoPedidoEntity;
import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import es.examplepb.findstockmanager.repositorios.AlmacenRepository;
import es.examplepb.findstockmanager.repositorios.ArticuloRepository;
import es.examplepb.findstockmanager.repositorios.EstadoPedidoRepository;
import es.examplepb.findstockmanager.repositorios.PedidoArticuloRepository;
import es.examplepb.findstockmanager.repositorios.PedidoRepository;
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
import java.util.Optional;

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
    private final PedidoRepository pedidoRepository;
    private final PedidoArticuloRepository pedidoArticuloRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Iniciando carga de datos iniciales...");

        // 1. Roles
        RolEntity managerRol = createRolIfNotFound("manager");
        RolEntity empleadoRol = createRolIfNotFound("empleado");
        RolEntity systemRol = createRolIfNotFound(Constants.ROL_SYSTEM);

        // 2. Secciones (Los IDs se auto-generan)
        SeccionEntity pantalonesSeccion = createSeccionIfNotFound("Pantalones");
        SeccionEntity camisetasSeccion = createSeccionIfNotFound("Camisetas");

        // 3. Almacenes (IDs auto-generados, se buscarán por nombre)
        AlmacenEntity almacenPrincipal = createDefaultAlmacenIfNotFound("Almacén Principal", "Dirección del Almacén Principal", pantalonesSeccion);

        // 4. Tiendas (IDs auto-generados, se buscarán por nombre)
        TiendaEntity tiendaA = createDefaultTiendaIfNotFound("Tienda A", "Calle Comercio 789");
        TiendaEntity tiendaB = createDefaultTiendaIfNotFound("Tienda B", "Plaza Central 101");

        // 5. Usuarios (IDs auto-generados, se buscan por email)
        UsuarioEntity systemUser = createSystemUserIfNotFound(systemRol, almacenPrincipal);
        UsuarioEntity juanPerez = createUsuarioIfNotFound(managerRol, almacenPrincipal, "Juan", "Pérez", "12345678A", "juan.perez@example.com", "images/hombre1.png", "password123");
        UsuarioEntity anaGarcia = createUsuarioIfNotFound(empleadoRol, almacenPrincipal, "Ana", "García", "87654321B", "ana.garcia@example.com", "images/mujer1.png", "password456");

        // 6. Artículos (dependen de secciones)
        createEssentialArticulos(pantalonesSeccion, camisetasSeccion);

        // 7. Estados de Pedido
        EstadoPedidoEntity pendienteEstado = createEstadoPedidoIfNotFound("Pendiente");
        EstadoPedidoEntity enTramiteEstado = createEstadoPedidoIfNotFound("En tramite");
        EstadoPedidoEntity completadoEstado = createEstadoPedidoIfNotFound("Completado");

        // 8. Tipos de Pedido (Solo los 2 escenarios clave)
        TipoPedidoEntity pedidoDeTiendaTipo = createTipoPedidoIfNotFound(Constants.TIPO_ALMACEN_TIENDA);
        TipoPedidoEntity reposicionStockAlmacenTipo = createTipoPedidoIfNotFound(Constants.TIPO_REPOSICION_STOCK_ALMACEN);

        // 9. Pedidos y Pedido_Articulo (IDs de Pedido auto-generados)
        createPedidosAndPedidoArticulosIfNotFound(
                pedidoDeTiendaTipo,
                reposicionStockAlmacenTipo,
                pendienteEstado, enTramiteEstado, completadoEstado,
                tiendaA, almacenPrincipal
        );

        log.info("Carga de datos iniciales finalizada.");
    }

    private RolEntity createRolIfNotFound(String tipoRol) {
        return rolRepository.findByTipoRol(tipoRol).orElseGet(() -> {
            log.info("Creando rol: {}", tipoRol);
            return rolRepository.save(new RolEntity(tipoRol));
        });
    }

    private SeccionEntity createSeccionIfNotFound(String categoriaSeccion) {
        return seccionRepository.findByCategoriaSeccion(categoriaSeccion).orElseGet(() -> {
            log.info("Creando sección: {}", categoriaSeccion);
            return seccionRepository.save(new SeccionEntity(null, categoriaSeccion));
        });
    }

    private EstadoPedidoEntity createEstadoPedidoIfNotFound(String descripcion) {
        return estadoPedidoRepository.findByDescripcionEstado(descripcion).orElseGet(() -> {
            log.info("Creando estado de pedido: {}", descripcion);
            return estadoPedidoRepository.save(new EstadoPedidoEntity(descripcion));
        });
    }

    private TipoPedidoEntity createTipoPedidoIfNotFound(String descripcionTipo) {
        return tipoPedidoRepository.findByDescripcionTipo(descripcionTipo).orElseGet(() -> {
            log.info("Creando tipo de pedido: {}", descripcionTipo);
            return tipoPedidoRepository.save(new TipoPedidoEntity(descripcionTipo));
        });
    }

    private UsuarioEntity createSystemUserIfNotFound(RolEntity systemRol, AlmacenEntity almacen) {
        return usuarioRepository.findByEmail(Constants.SYSTEM_USER_EMAIL).orElseGet(() -> {
            log.info("Creando usuario del sistema...");
            UsuarioEntity systemUser = UsuarioEntity.builder()
                    .rolEntity(systemRol)
                    .almacen(almacen)
                    .nombre("Sistema")
                    .apellido("Automático")
                    .nif(null)
                    .email(Constants.SYSTEM_USER_EMAIL)
                    .password(passwordEncoder.encode("systempassword"))
                    .fotografiaUsuario("images/system.jpg")
                    .build();
            return usuarioRepository.save(systemUser);
        });
    }

    private UsuarioEntity createUsuarioIfNotFound(RolEntity rol, AlmacenEntity almacen, String nombre, String apellido, String nif, String email, String fotografia, String rawPassword) {
        return usuarioRepository.findByEmail(email).orElseGet(() -> {
            log.info("Creando usuario: {}", email);
            UsuarioEntity usuario = UsuarioEntity.builder()
                    .rolEntity(rol)
                    .almacen(almacen)
                    .nombre(nombre)
                    .apellido(apellido)
                    .nif(nif)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .fotografiaUsuario(fotografia)
                    .build();
            return usuarioRepository.save(usuario);
        });
    }

    private AlmacenEntity createDefaultAlmacenIfNotFound(String nombreAlmacen, String direccion, SeccionEntity seccion) {
        return almacenRepository.findByNombreAlmacen(nombreAlmacen).orElseGet(() -> {
            log.info("Creando almacén por defecto '{}' (ID auto-generado)...", nombreAlmacen);
            AlmacenEntity defaultAlmacen = new AlmacenEntity(null, seccion, nombreAlmacen, direccion);
            AlmacenEntity savedAlmacen = almacenRepository.save(defaultAlmacen);
            log.info("Almacén por defecto creado con ID: {}", savedAlmacen.getId());
            return savedAlmacen;
        });
    }

    private TiendaEntity createDefaultTiendaIfNotFound(String nombreTienda, String direccion) {
        return tiendaRepository.findByNombreTienda(nombreTienda).orElseGet(() -> {
            log.info("Creando tienda por defecto '{}' (ID auto-generado)...", nombreTienda);
            TiendaEntity defaultTienda = new TiendaEntity(null, nombreTienda, direccion);
            TiendaEntity savedTienda = tiendaRepository.save(defaultTienda);
            log.info("Tienda por defecto creada con ID: {}", savedTienda.getId());
            return savedTienda;
        });
    }

    private void createEssentialArticulos(SeccionEntity pantalonesSeccion, SeccionEntity camisetasSeccion) {
        log.info("Asegurando que los artículos esenciales existan o creándolos...");

        // Artículos de Camisetas (resto del código igual)
        createArticuloIfNotFound("CAMT001001", camisetasSeccion, "Blanco", 19.99, "XXS", "Camiseta", "camiseta_blanca.png", 100);
        createArticuloIfNotFound("CAMT001002", camisetasSeccion, "Blanco", 19.99, "XS", "Camiseta", "camiseta_blanca.png", 90);
        createArticuloIfNotFound("CAMT001003", camisetasSeccion, "Blanco", 19.99, "S", "Camiseta", "camiseta_blanca.png", 90);
        createArticuloIfNotFound("CAMT001004", camisetasSeccion, "Blanco", 19.99, "M", "Camiseta", "camiseta_blanca.png", 90);
        createArticuloIfNotFound("CAMT001005", camisetasSeccion, "Blanco", 19.99, "L", "Camiseta", "camiseta_blanca.png", 100);
        createArticuloIfNotFound("CAMT001006", camisetasSeccion, "Blanco", 19.99, "XL", "Camiseta", "camiseta_blanca.png", 40);

        createArticuloIfNotFound("CAMT002001", camisetasSeccion, "Azul", 19.99, "XXS", "Camiseta", "camiseta_azul.png", 70);
        createArticuloIfNotFound("CAMT002002", camisetasSeccion, "Azul", 19.99, "XS", "Camiseta", "camiseta_azul.png", 80);
        createArticuloIfNotFound("CAMT002003", camisetasSeccion, "Azul", 19.99, "S", "Camiseta", "camiseta_azul.png", 85);
        createArticuloIfNotFound("CAMT002004", camisetasSeccion, "Azul", 19.99, "M", "Camiseta", "camiseta_azul.png", 90);
        createArticuloIfNotFound("CAMT002005", camisetasSeccion, "Azul", 19.99, "L", "Camiseta", "camiseta_azul.png", 75);
        createArticuloIfNotFound("CAMT002006", camisetasSeccion, "Azul", 19.99, "XL", "Camiseta", "camiseta_azul.png", 35);

        createArticuloIfNotFound("CAMT003001", camisetasSeccion, "Negro", 19.99, "XXS", "Camiseta", "camiseta_negra.png", 80);
        createArticuloIfNotFound("CAMT003002", camisetasSeccion, "Negro", 19.99, "XS", "Camiseta", "camiseta_negra.png", 90);
        createArticuloIfNotFound("CAMT003003", camisetasSeccion, "Negro", 19.99, "S", "Camiseta", "camiseta_negra.png", 100);
        createArticuloIfNotFound("CAMT003004", camisetasSeccion, "Negro", 19.99, "M", "Camiseta", "camiseta_negra.png", 110);
        createArticuloIfNotFound("CAMT003005", camisetasSeccion, "Negro", 19.99, "L", "Camiseta", "camiseta_negra.png", 95);
        createArticuloIfNotFound("CAMT003006", camisetasSeccion, "Negro", 19.99, "XL", "Camiseta", "camiseta_negra.png", 40);

        createArticuloIfNotFound("CAMT004001", camisetasSeccion, "Gris", 19.99, "XXS", "Camiseta", "camiseta_gris.png", 60);
        createArticuloIfNotFound("CAMT004002", camisetasSeccion, "Gris", 19.99, "XS", "Camiseta", "camiseta_gris.png", 65);
        createArticuloIfNotFound("CAMT004003", camisetasSeccion, "Gris", 19.99, "S", "Camiseta", "camiseta_gris.png", 70);
        createArticuloIfNotFound("CAMT004004", camisetasSeccion, "Gris", 19.99, "M", "Camiseta", "camiseta_gris.png", 80);
        createArticuloIfNotFound("CAMT004005", camisetasSeccion, "Gris", 19.99, "L", "Camiseta", "camiseta_gris.png", 70);
        createArticuloIfNotFound("CAMT004006", camisetasSeccion, "Gris", 19.99, "XL", "Camiseta", "camiseta_gris.png", 25);

        createArticuloIfNotFound("CAMT005001", camisetasSeccion, "Rojo", 19.99, "XXS", "Camiseta", "camiseta_roja.png", 50);
        createArticuloIfNotFound("CAMT005002", camisetasSeccion, "Rojo", 19.99, "XS", "Camiseta", "camiseta_roja.png", 55);
        createArticuloIfNotFound("CAMT005003", camisetasSeccion, "Rojo", 19.99, "S", "Camiseta", "camiseta_roja.png", 60);
        createArticuloIfNotFound("CAMT005004", camisetasSeccion, "Rojo", 19.99, "M", "Camiseta", "camiseta_roja.png", 70);
        createArticuloIfNotFound("CAMT005005", camisetasSeccion, "Rojo", 19.99, "L", "Camiseta", "camiseta_roja.png", 60);
        createArticuloIfNotFound("CAMT005006", camisetasSeccion, "Rojo", 19.99, "XL", "Camiseta", "camiseta_roja.png", 20);

        // Artículos de Pantalones (resto del código igual)
        createArticuloIfNotFound("PANT002001", pantalonesSeccion, "Azul", 49.99, "XXS", "Pantalón", "pantalon_azul.png", 40);
        createArticuloIfNotFound("PANT002002", pantalonesSeccion, "Azul", 49.99, "XS", "Pantalón", "pantalon_azul.png", 45);
        createArticuloIfNotFound("PANT002003", pantalonesSeccion, "Azul", 49.99, "S", "Pantalón", "pantalon_azul.png", 50);
        createArticuloIfNotFound("PANT002004", pantalonesSeccion, "Azul", 49.99, "M", "Pantalón", "pantalon_azul.png", 55);
        createArticuloIfNotFound("PANT002005", pantalonesSeccion, "Azul", 49.99, "L", "Pantalón", "pantalon_azul.png", 48);
        createArticuloIfNotFound("PANT002006", pantalonesSeccion, "Azul", 49.99, "XL", "Pantalón", "pantalon_azul.png", 25);

        createArticuloIfNotFound("PANT003001", pantalonesSeccion, "Negro", 49.99, "XXS", "Pantalón", "pantalon_negro.png", 50);
        createArticuloIfNotFound("PANT003002", pantalonesSeccion, "Negro", 49.99, "XS", "Pantalón", "pantalon_negro.png", 55);
        createArticuloIfNotFound("PANT003003", pantalonesSeccion, "Negro", 49.99, "S", "Pantalón", "pantalon_negro.png", 60);
        createArticuloIfNotFound("PANT003004", pantalonesSeccion, "Negro", 49.99, "M", "Pantalón", "pantalon_negro.png", 65);
        createArticuloIfNotFound("PANT003005", pantalonesSeccion, "Negro", 49.99, "L", "Pantalón", "pantalon_negro.png", 58);
        createArticuloIfNotFound("PANT003006", pantalonesSeccion, "Negro", 49.99, "XL", "Pantalón", "pantalon_negro.png", 30);

        createArticuloIfNotFound("PANT004001", pantalonesSeccion, "Gris", 49.99, "XXS", "Pantalón", "pantalon_gris.png", 35);
        createArticuloIfNotFound("PANT004002", pantalonesSeccion, "Gris", 49.99, "XS", "Pantalón", "pantalon_gris.png", 40);
        createArticuloIfNotFound("PANT004003", pantalonesSeccion, "Gris", 49.99, "S", "Pantalón", "pantalon_gris.png", 45);
        createArticuloIfNotFound("PANT004004", pantalonesSeccion, "Gris", 49.99, "M", "Pantalón", "pantalon_gris.png", 50);
        createArticuloIfNotFound("PANT004005", pantalonesSeccion, "Gris", 49.99, "L", "Pantalón", "pantalon_gris.png", 43);
        createArticuloIfNotFound("PANT004006", pantalonesSeccion, "Gris", 49.99, "XL", "Pantalón", "pantalon_gris.png", 20);

        log.info("Artículos esenciales comprobados/creados.");
    }

    private ArticuloEntity createArticuloIfNotFound(String idArticulo, SeccionEntity seccion, String color, Double precio, String talla, String tipo, String imagen, Integer stock) {
        return articuloRepository.findById(idArticulo).orElseGet(() -> {
            log.info("Creando artículo: {}", idArticulo);
            return articuloRepository.save(new ArticuloEntity(idArticulo, seccion, color, precio, talla, tipo, imagen, stock));
        });
    }

    private void createPedidosAndPedidoArticulosIfNotFound(
            TipoPedidoEntity pedidoDeTiendaTipo, // Tipo para pedidos que involucren tiendas
            TipoPedidoEntity reposicionStockAlmacenTipo, // Tipo para pedidos solo entre almacenes
            EstadoPedidoEntity pendienteEstado, EstadoPedidoEntity enTramiteEstado, EstadoPedidoEntity completadoEstado,
            TiendaEntity tiendaA, AlmacenEntity almacenPrincipal) {

        if (pedidoRepository.count() == 0) {
            log.info("Creando Pedido 1 (Ejemplo: Tienda a Tienda, Completado). Tipo de Pedido: DE TIENDA.");
            PedidoEntity pedido1 = new PedidoEntity();
            pedido1.setTipo(pedidoDeTiendaTipo);
            pedido1.setEstado(completadoEstado);

            // Si es un pedido que involucra tiendas, los campos de ALMACEN deben ser NULL.
            pedido1.setOrigenTiendaEntity(tiendaA);
            pedido1.setDestinoTiendaEntity(tiendaA);
            pedido1.setOrigenAlmacenEntity(null);
            pedido1.setDestinoAlmacenEntity(null);

            pedido1.setFechaSolicitud(LocalDate.of(2023, 1, 1));
            pedido1.setFechaRecepcion(LocalDate.of(2023, 1, 1));
            pedido1.setFechaEnvio(null);

            PedidoEntity savedPedido1 = pedidoRepository.save(pedido1);

            ArticuloEntity camt001001 = articuloRepository.findById("CAMT001001").orElseThrow(() -> new IllegalStateException("Artículo CAMT001001 no encontrado para Pedido 1"));
            PedidoArticuloIdEntity paId1 = new PedidoArticuloIdEntity(camt001001.getId(), savedPedido1.getId());
            PedidoArticuloEntity pa1 = new PedidoArticuloEntity(paId1, savedPedido1, camt001001, 10, 199.90, true);
            pedidoArticuloRepository.save(pa1);

            ArticuloEntity pant002001 = articuloRepository.findById("PANT002001").orElseThrow(() -> new IllegalStateException("Artículo PANT002001 no encontrado para Pedido 1"));
            PedidoArticuloIdEntity paId1_2 = new PedidoArticuloIdEntity(pant002001.getId(), savedPedido1.getId());
            PedidoArticuloEntity pa1_2 = new PedidoArticuloEntity(paId1_2, savedPedido1, pant002001, 5, 249.95, true);
            pedidoArticuloRepository.save(pa1_2);


            log.info("Pedido 1 y sus líneas creados con ID: {}", savedPedido1.getId());


            log.info("Creando Pedido 2 (Ejemplo: Reposición Stock - Almacén a Almacén, En Tramite). Tipo de Pedido: REPOSICION STOCK.");
            PedidoEntity pedido2 = new PedidoEntity();
            pedido2.setTipo(reposicionStockAlmacenTipo);
            pedido2.setEstado(enTramiteEstado);

            // Si es un pedido de reposición de stock (solo almacenes), los campos de TIENDA deben ser NULL.
            pedido2.setOrigenTiendaEntity(null);
            pedido2.setDestinoTiendaEntity(null);
            pedido2.setOrigenAlmacenEntity(almacenPrincipal);
            pedido2.setDestinoAlmacenEntity(almacenPrincipal);

            pedido2.setFechaSolicitud(LocalDate.of(2023, 2, 1));
            pedido2.setFechaRecepcion(LocalDate.of(2023, 2, 1));
            pedido2.setFechaEnvio(LocalDate.of(2023, 2, 3));

            PedidoEntity savedPedido2 = pedidoRepository.save(pedido2);

            ArticuloEntity pant002002 = articuloRepository.findById("PANT002002").orElseThrow(() -> new IllegalStateException("Artículo PANT002002 no encontrado para Pedido 2"));
            PedidoArticuloIdEntity paId2 = new PedidoArticuloIdEntity(pant002002.getId(), savedPedido2.getId());
            PedidoArticuloEntity pa2 = new PedidoArticuloEntity(paId2, savedPedido2, pant002002, 5, 249.95, true);
            pedidoArticuloRepository.save(pa2);

            ArticuloEntity camt003001 = articuloRepository.findById("CAMT003001").orElseThrow(() -> new IllegalStateException("Artículo CAMT003001 no encontrado para Pedido 2"));
            PedidoArticuloIdEntity paId2_2 = new PedidoArticuloIdEntity(camt003001.getId(), savedPedido2.getId());
            PedidoArticuloEntity pa2_2 = new PedidoArticuloEntity(paId2_2, savedPedido2, camt003001, 3, 59.97, false);
            pedidoArticuloRepository.save(pa2_2);

            log.info("Pedido 2 y sus líneas creados con ID: {}", savedPedido2.getId());
        }
    }
}