package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    // Buscar Pedidos por FechaSolicitud exacta
    List<Pedido> findByFechaSolicitud(LocalDate fechaSolicitud);

    // Buscar Pedidos donde la descripción del estado (a través de la relación 'estado') está en una lista de strings
    List<Pedido> findByEstado_DescripcionEstadoIn(List<String> descripcionEstados);

    // Buscar Pedidos donde la descripción del estado está en una lista Y la FechaSolicitud coincide exactamente
    List<Pedido> findByEstado_DescripcionEstadoInAndFechaSolicitud(List<String> descripcionEstados, LocalDate fechaSolicitud);

    // Si quisieras filtrar por rangos de fecha o combinaciones diferentes, añadirías métodos como:
    // List<Pedido> findByFechaSolicitudAfter(LocalDate date);
    // List<Pedido> findByFechaSolicitudBetween(LocalDate startDate, LocalDate endDate);
    // List<Pedido> findByEstado_DescripcionEstadoInAndFechaSolicitudAfter(List<String> descripcionEstados, LocalDate date);;

    // --- Nuevos métodos para filtrar por destino ---
    List<Pedido> findByDestinoTiendaIsNotNull(); // Pedidos con destino a tienda (destinoAlmacen será null por cómo insertas)

    List<Pedido> findByDestinoAlmacenIsNotNull(); // Pedidos con destino a almacén (destinoTienda será null)

    // --- Nuevos métodos para combinaciones con Destino = Tienda ---
    List<Pedido> findByEstado_DescripcionEstadoInAndDestinoTiendaIsNotNull(List<String> descripcionEstados);

    List<Pedido> findByFechaSolicitudAndDestinoTiendaIsNotNull(LocalDate fechaSolicitud);

    List<Pedido> findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoTiendaIsNotNull(List<String> descripcionEstados, LocalDate fechaSolicitud);

    // --- Nuevos métodos para combinaciones con Destino = Almacen ---
    List<Pedido> findByEstado_DescripcionEstadoInAndDestinoAlmacenIsNotNull(List<String> descripcionEstados);

    List<Pedido> findByFechaSolicitudAndDestinoAlmacenIsNotNull(LocalDate fechaSolicitud);

    List<Pedido> findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoAlmacenIsNotNull(List<String> descripcionEstados, LocalDate fechaSolicitud);
}
