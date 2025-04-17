package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {}
