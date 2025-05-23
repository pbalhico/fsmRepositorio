package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.EstadoPedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedidoEntity, Integer> {
    Optional<EstadoPedidoEntity> findByDescripcionEstado(String descripcionEstado);
}
