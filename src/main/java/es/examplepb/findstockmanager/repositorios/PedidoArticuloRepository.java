package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoArticuloRepository extends JpaRepository<PedidoArticuloEntity, PedidoArticuloIdEntity> {
    List<PedidoArticuloEntity> findById_PedidoId(Integer pedidoId);

    // Si en algún momento necesitas buscar por el ID de artículo, sería:
    // List<PedidoArticuloEntity> findById_Articulo(String articuloId);
}
