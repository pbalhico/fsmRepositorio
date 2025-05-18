package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoArticuloRepository extends JpaRepository<PedidoArticuloEntity, Integer> {
    List<PedidoArticuloEntity> findById_Pedido(Integer id);
}
