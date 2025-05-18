package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.TipoPedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoPedidoRepository extends JpaRepository<TipoPedidoEntity, Integer> {
}
