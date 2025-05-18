package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Integer> {
    // Buscar Pedidos por FechaSolicitud exacta
    List<PedidoEntity> findByFechaSolicitud(LocalDate fechaSolicitud);

    // Buscar Pedidos donde la descripción del estado (a través de la relación 'estado') está en una lista de strings
    List<PedidoEntity> findByEstado_DescripcionEstadoIn(List<String> descripcionEstados);

    // Buscar Pedidos donde la descripción del estado está en una lista Y la FechaSolicitud coincide exactamente
    List<PedidoEntity> findByEstado_DescripcionEstadoInAndFechaSolicitud(List<String> descripcionEstados, LocalDate fechaSolicitud);

    // Si quisieras filtrar por rangos de fecha o combinaciones diferentes, añadirías métodos como:
    // List<Pedido> findByFechaSolicitudAfter(LocalDate date);
    // List<Pedido> findByFechaSolicitudBetween(LocalDate startDate, LocalDate endDate);
    // List<Pedido> findByEstado_DescripcionEstadoInAndFechaSolicitudAfter(List<String> descripcionEstados, LocalDate date);;

    // --- Nuevos métodos para filtrar por destino ---
    List<PedidoEntity> findByDestinoTiendaEntityIsNotNull(); // Pedidos con destino a tienda (destinoAlmacen será null por cómo insertas)

    List<PedidoEntity> findByDestinoAlmacenEntityIsNotNull(); // Pedidos con destino a almacén (destinoTienda será null)

    // --- Nuevos métodos para combinaciones con Destino = Tienda ---
    List<PedidoEntity> findByEstado_DescripcionEstadoInAndDestinoTiendaEntityIsNotNull(List<String> descripcionEstados);

    List<PedidoEntity> findByFechaSolicitudAndDestinoTiendaEntityIsNotNull(LocalDate fechaSolicitud);

    List<PedidoEntity> findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoTiendaEntityIsNotNull(List<String> descripcionEstados, LocalDate fechaSolicitud);

    // --- Nuevos métodos para combinaciones con Destino = Almacen ---
    List<PedidoEntity> findByEstado_DescripcionEstadoInAndDestinoAlmacenEntityIsNotNull(List<String> descripcionEstados);

    List<PedidoEntity> findByFechaSolicitudAndDestinoAlmacenEntityIsNotNull(LocalDate fechaSolicitud);

    List<PedidoEntity> findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoAlmacenEntityIsNotNull(List<String> descripcionEstados, LocalDate fechaSolicitud);
}
