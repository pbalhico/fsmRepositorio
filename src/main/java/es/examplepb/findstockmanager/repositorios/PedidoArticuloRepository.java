package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloIdEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoArticuloRepository extends JpaRepository<PedidoArticuloEntity, PedidoArticuloIdEntity> {
    // @EntityGraph para asegurar que articuloEntity se carga EAGERLY
    @EntityGraph(attributePaths = {"articuloEntity"})
    // El path debe ser el nombre del campo en PedidoArticuloEntity
    List<PedidoArticuloEntity> findById_PedidoId(Integer pedidoId);
}
