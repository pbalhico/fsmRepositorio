package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.EstadoPedidoEntity;
import es.examplepb.findstockmanager.entidades.PedidoEntity;
import es.examplepb.findstockmanager.entidades.TipoPedidoEntity;
import org.springframework.data.jpa.repository.EntityGraph; // Import this!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Integer> {

    // THIS IS CRUCIAL FOR LOADING RELATED ENTITIES
    @EntityGraph(attributePaths = {"tipo", "estado", "origenTiendaEntity", "destinoTiendaEntity", "origenAlmacenEntity", "destinoAlmacenEntity"})
    Optional<PedidoEntity> findById(Integer id);

    // Your other methods (no EntityGraph needed on these unless you're loading for a specific view):
    List<PedidoEntity> findByFechaSolicitud(LocalDate fechaSolicitud);

    List<PedidoEntity> findByEstado_DescripcionEstadoIn(List<String> descripcionEstados);

    List<PedidoEntity> findByEstado_DescripcionEstadoInAndFechaSolicitud(List<String> descripcionEstados, LocalDate fechaSolicitud);

    List<PedidoEntity> findByDestinoTiendaEntityIsNotNull();

    List<PedidoEntity> findByDestinoAlmacenEntityIsNotNull();

    List<PedidoEntity> findByEstado_DescripcionEstadoInAndDestinoTiendaEntityIsNotNull(List<String> descripcionEstados);

    List<PedidoEntity> findByFechaSolicitudAndDestinoTiendaEntityIsNotNull(LocalDate fechaSolicitud);

    List<PedidoEntity> findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoTiendaEntityIsNotNull(List<String> descripcionEstados, LocalDate fechaSolicitud);

    List<PedidoEntity> findByEstado_DescripcionEstadoInAndDestinoAlmacenEntityIsNotNull(List<String> descripcionEstados);

    List<PedidoEntity> findByFechaSolicitudAndDestinoAlmacenEntityIsNotNull(LocalDate fechaSolicitud);

    List<PedidoEntity> findByEstado_DescripcionEstadoInAndFechaSolicitudAndDestinoAlmacenEntityIsNotNull(List<String> descripcionEstados, LocalDate fechaSolicitud);

    Optional<PedidoEntity> findTopByOrderByFechaSolicitudDesc();

    // Contar pedidos por tipo y estado (PENDIENTE o EN TRAMITE)
    @Query("SELECT COUNT(p) FROM PedidoEntity p WHERE p.tipo = :tipo AND (p.estado.descripcionEstado = 'Pendiente' OR p.estado.descripcionEstado = 'En Tramite')")
    long countPedidosPendientesEnTramiteByTipo(@Param("tipo") TipoPedidoEntity tipo);

    // Si quieres contar solo por estado (útil para el general, aunque no lo necesites coloreado)
    long countByEstado(EstadoPedidoEntity estado);

    // Puedes necesitar también encontrar tipos de pedido por su descripción
    // Optional<TipoPedidoEntity> findByDescripcionTipo(String descripcion);
}