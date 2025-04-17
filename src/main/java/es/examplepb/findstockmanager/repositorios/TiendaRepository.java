package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TiendaRepository extends JpaRepository<Tienda, Integer> {}
