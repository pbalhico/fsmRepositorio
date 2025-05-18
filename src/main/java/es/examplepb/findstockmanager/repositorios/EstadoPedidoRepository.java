package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.EstadoPedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedidoEntity, Integer> {
}
