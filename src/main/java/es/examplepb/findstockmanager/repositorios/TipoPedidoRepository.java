package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.TipoPedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoPedidoRepository extends JpaRepository<TipoPedidoEntity, Integer> {
    Optional<TipoPedidoEntity> findByDescripcionTipo(String descripcionTipo);
}
