package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoArticuloRepository extends JpaRepository<PedidoArticuloEntity, Integer> {
    List<PedidoArticuloEntity> findById_Pedido(Integer id);
}
