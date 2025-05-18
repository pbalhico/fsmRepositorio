package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.TiendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TiendaRepository extends JpaRepository<TiendaEntity, Integer> {
}
