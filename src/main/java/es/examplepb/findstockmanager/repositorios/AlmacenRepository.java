package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlmacenRepository extends JpaRepository<Almacen, Integer> {}
