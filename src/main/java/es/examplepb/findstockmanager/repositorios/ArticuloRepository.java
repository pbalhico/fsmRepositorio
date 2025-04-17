package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {}
