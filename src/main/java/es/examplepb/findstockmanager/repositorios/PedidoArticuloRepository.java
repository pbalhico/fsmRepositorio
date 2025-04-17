package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.PedidoArticulo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoArticuloRepository extends JpaRepository<PedidoArticulo, Integer> {}
