package es.examplepb.findstockmanager;

import es.examplepb.findstockmanager.entidades.*;
import es.examplepb.findstockmanager.repositorios.UsuarioRepository;
import es.examplepb.findstockmanager.repositorios.PedidoRepository;
import es.examplepb.findstockmanager.repositorios.EstadoPedidoRepository;
import es.examplepb.findstockmanager.repositorios.AlmacenRepository;
import es.examplepb.findstockmanager.repositorios.TiendaRepository;
import es.examplepb.findstockmanager.servicios.PedidoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc // Configura MockMvc para simular peticiones web
@Transactional // Cada método de test se ejecuta en una transacción que se revierte al finalizar
class FindStockManagerApplicationTests {

    @Test
    void contextLoads() {
    }

//    @Autowired
//    private MockMvc mockMvc; // Para simular peticiones HTTP a controladores
//
//    // Repositorios y Servicios necesarios para preparar datos de prueba y probar la lógica
//    @Autowired
//    private UsuarioRepository usuarioRepository;
//
//    @Autowired
//    private PedidoService pedidoService;
//
//    @Autowired
//    private PedidoRepository pedidoRepository;
//
//    @Autowired
//    private EstadoPedidoRepository estadoPedidoRepository;
//    @Autowired
//    private AlmacenRepository almacenRepository;
//    @Autowired
//    private TiendaRepository tiendaRepository;
//
//    // Datos de prueba que se usarán en múltiples tests
//    private Usuario usuarioValido;
//    private EstadoPedido estadoPendiente;
//    private EstadoPedido estadoCompletado;
//    private Almacen almacenPrincipal;
//    private Tienda tiendaCentro;
//    private Pedido pedidoPendienteTiendaHoy;
//    private Pedido pedidoCompletadoAlmacenAyer;
//    private Pedido pedidoPendienteAlmacenHoy;
//
//    @BeforeEach
//        // Este método se ejecutará ANTES de cada método de test (@Test) en esta clase
//    void setUpTestData() {
//        // Configuración de Datos de Prueba para Autenticación
//        usuarioValido = new Usuario();
//        usuarioValido.setEmail("test@example.com");
//        usuarioValido.setPassword("password123"); // Si usas Spring Security con PasswordEncoder, aquí iría la contraseña codificada
//        usuarioValido.setNombre("Test User");
//        // setear otros campos necesarios para Usuario
//        usuarioRepository.save(usuarioValido);
//        // Configuración de Datos de Prueba para Pedidos
//        // Crear y guardar entidades relacionadas si es necesario
//        estadoPendiente = new EstadoPedido();
//        estadoPendiente.setDescripcionEstado("Pendiente");
//        estadoCompletado = new EstadoPedido();
//        estadoCompletado.setDescripcionEstado("Completado");
//        estadoPedidoRepository.saveAll(Arrays.asList(estadoPendiente, estadoCompletado));
//
//        almacenPrincipal = new Almacen();
//        almacenPrincipal.setNombreAlmacen("Almacen Central");
//        tiendaCentro = new Tienda();
//        tiendaCentro.setNombreTienda("Tienda Centro");
//        almacenRepository.save(almacenPrincipal);
//        tiendaRepository.save(tiendaCentro);
//
//        // Crear y guardar pedidos con diferentes combinaciones de filtros
//        pedidoPendienteTiendaHoy = new Pedido();
//        pedidoPendienteTiendaHoy.setFechaSolicitud(LocalDate.now());
//        pedidoPendienteTiendaHoy.setEstado(estadoPendiente);
//        pedidoPendienteTiendaHoy.setDestinoTienda(tiendaCentro);
//        // pedidoPendienteTiendaHoy.setDestinoAlmacen(null); // Asegurar que uno es null
//
//        pedidoCompletadoAlmacenAyer = new Pedido();
//        pedidoCompletadoAlmacenAyer.setFechaSolicitud(LocalDate.now().minusDays(1));
//        pedidoCompletadoAlmacenAyer.setEstado(estadoCompletado);
//        // pedidoCompletadoAlmacenAyer.setDestinoTienda(null);
//        pedidoCompletadoAlmacenAyer.setDestinoAlmacen(almacenPrincipal);
//
//
//        pedidoPendienteAlmacenHoy = new Pedido();
//        pedidoPendienteAlmacenHoy.setFechaSolicitud(LocalDate.now());
//        pedidoPendienteAlmacenHoy.setEstado(estadoPendiente);
//        // pedidoPendienteAlmacenHoy.setDestinoTienda(null);
//        pedidoPendienteAlmacenHoy.setDestinoAlmacen(almacenPrincipal);
//
//        // Guardar todos los pedidos de prueba
//        pedidoRepository.saveAll(Arrays.asList(pedidoPendienteTiendaHoy, pedidoCompletadoAlmacenAyer, pedidoPendienteAlmacenHoy));
//    }
//
//    // Pruebas de Integración de Autenticación
//
//    @Test
//    void testLoginSuccessWithValidCredentials() throws Exception {
//        // Simula una petición POST al endpoint /login con credenciales válidas
//        mockMvc.perform(post("/login")
//                        .param("email", "test@example.com")
//                        .param("password", "password123"))
//                // Verifica que la respuesta es una redirección a la URL raíz "/"
//                .andExpect(redirectedUrl("/"));
//    }
//
//    @Test
//    void testLoginFailureWithInvalidPassword() throws Exception {
//        // Simula una petición POST al endpoint /login con una contraseña incorrecta
//        mockMvc.perform(post("/login")
//                        .param("email", "test@example.com")
//                        .param("password", "wrongpassword"))
//                // Verifica que la vista retornada es "login" (el formulario de login)
//                .andExpect(view().name("login"))
//                // Verifica que se ha añadido un atributo llamado "error" al modelo (usado en la vista para mostrar el mensaje)
//                .andExpect(model().attributeExists("error"));
//    }
//
//    @Test
//    void testLoginFailureWithNonExistentEmail() throws Exception {
//        // Simula una petición POST al endpoint /login con un email que no existe en la BD
//        mockMvc.perform(post("/login")
//                        .param("email", "nonexistent@example.com")
//                        .param("password", "anypassword"))
//                // Verifica que la vista retornada es "login"
//                .andExpect(view().name("login"))
//                // Verifica que se ha añadido un atributo "error" al modelo
//                .andExpect(model().attributeExists("error"));
//    }
//
//
//    // Pruebas de Integración de Filtrado de Pedidos
//
//    @Test
//    void testFindAllFilteredNoFilters() {
//        // Llama al método del servicio sin proporcionar ningún filtro
//        List<Pedido> pedidos = pedidoService.findAllFiltered(null, null, null);
//        // Verifica que se devuelven todos los pedidos de prueba
//        assertEquals(3, pedidos.size(), "Debería retornar todos los pedidos sin filtros.");
//        assertTrue(pedidos.contains(pedidoPendienteTiendaHoy));
//        assertTrue(pedidos.contains(pedidoCompletadoAlmacenAyer));
//        assertTrue(pedidos.contains(pedidoPendienteAlmacenHoy));
//    }
//
//    @Test
//    void testFindAllFilteredByEstadoPendiente() {
//        // Llama al servicio filtrando solo por estado "Pendiente"
//        List<String> estados = Arrays.asList("Pendiente");
//        List<Pedido> pedidos = pedidoService.findAllFiltered(estados, null, null);
//        // Verifica que solo se devuelven los pedidos con ese estado
//        assertEquals(2, pedidos.size(), "Debería retornar pedidos con estado Pendiente.");
//        assertTrue(pedidos.contains(pedidoPendienteTiendaHoy));
//        assertTrue(pedidos.contains(pedidoPendienteAlmacenHoy));
//    }
//
//    @Test
//    void testFindAllFilteredByFechaHoy() {
//        // Llama al servicio filtrando solo por la fecha de hoy
//        List<Pedido> pedidos = pedidoService.findAllFiltered(null, LocalDate.now(), null);
//        // Verifica que solo se devuelven los pedidos con esa fecha
//        assertEquals(2, pedidos.size(), "Debería retornar pedidos con fecha de hoy.");
//        assertTrue(pedidos.contains(pedidoPendienteTiendaHoy));
//        assertTrue(pedidos.contains(pedidoPendienteAlmacenHoy));
//    }
//
//    @Test
//    void testFindAllFilteredByDestinoTienda() {
//        // Llama al servicio filtrando solo por destino "tienda"
//        List<Pedido> pedidos = pedidoService.findAllFiltered(null, null, "tienda");
//        // Verifica que solo se devuelven los pedidos con destino a tienda
//        assertEquals(1, pedidos.size(), "Debería retornar pedidos con destino a tienda.");
//        assertTrue(pedidos.contains(pedidoPendienteTiendaHoy));
//    }
//
//    @Test
//    void testFindAllFilteredByDestinoAlmacen() {
//        // Llama al servicio filtrando solo por destino "almacen"
//        List<Pedido> pedidos = pedidoService.findAllFiltered(null, null, "almacen");
//        // Verifica que solo se devuelven los pedidos con destino a almacén
//        assertEquals(2, pedidos.size(), "Debería retornar pedidos con destino a almacén.");
//        assertTrue(pedidos.contains(pedidoCompletadoAlmacenAyer));
//        assertTrue(pedidos.contains(pedidoPendienteAlmacenHoy));
//    }
//
//    @Test
//    void testFindAllFilteredByEstadoCompletadoAndFechaAyer() {
//        // Llama al servicio filtrando por estado "Completado" y fecha de ayer
//        List<String> estados = Arrays.asList("Completado");
//        List<Pedido> pedidos = pedidoService.findAllFiltered(estados, LocalDate.now().minusDays(1), null);
//        // Verifica que solo se devuelve el pedido que cumple ambos criterios
//        assertEquals(1, pedidos.size(), "Debería retornar pedidos Completados de ayer.");
//        assertTrue(pedidos.contains(pedidoCompletadoAlmacenAyer));
//    }
//
//
//    @Test
//    void testFindAllFilteredByEstadoPendienteAndDestinoTienda() {
//        // Llama al servicio filtrando por estado "Pendiente" y destino "tienda"
//        List<String> estados = Arrays.asList("Pendiente");
//        List<Pedido> pedidos = pedidoService.findAllFiltered(estados, null, "tienda");
//        assertEquals(1, pedidos.size(), "Debería retornar pedidos Pendientes con destino a tienda.");
//        assertTrue(pedidos.contains(pedidoPendienteTiendaHoy));
//    }
//
//    @Test
//    void testFindAllFilteredByFechaHoyAndDestinoAlmacen() {
//        // Llama al servicio filtrando por fecha de hoy y destino "almacen"
//        List<Pedido> pedidos = pedidoService.findAllFiltered(null, LocalDate.now(), "almacen");
//        assertEquals(1, pedidos.size(), "Debería retornar pedidos de hoy con destino a almacén.");
//        assertTrue(pedidos.contains(pedidoPendienteAlmacenHoy));
//    }
//
//
//    @Test
//    void testFindAllFilteredByEstadoPendienteAndFechaHoyAndDestinoTienda() {
//        // Llama al servicio filtrando por estado "Pendiente", fecha de hoy, y destino "tienda"
//        List<String> estados = Arrays.asList("Pendiente");
//        List<Pedido> pedidos = pedidoService.findAllFiltered(estados, LocalDate.now(), "tienda");
//        assertEquals(1, pedidos.size(), "Debería retornar pedidos Pendientes de hoy con destino a tienda.");
//        assertTrue(pedidos.contains(pedidoPendienteTiendaHoy));
//    }
//
//    @Test
//    void testFindAllFilteredByEstadoCompletadoAndFechaAyerAndDestinoAlmacen() {
//        // Llama al servicio filtrando por estado "Completado", fecha de ayer, y destino "almacen"
//        List<String> estados = Arrays.asList("Completado");
//        List<Pedido> pedidos = pedidoService.findAllFiltered(estados, LocalDate.now().minusDays(1), "almacen");
//        assertEquals(1, pedidos.size(), "Debería retornar pedidos Completados de ayer con destino a almacén.");
//        assertTrue(pedidos.contains(pedidoCompletadoAlmacenAyer));
//    }
}
