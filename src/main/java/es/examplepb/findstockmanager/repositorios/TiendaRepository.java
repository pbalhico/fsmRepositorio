package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.TiendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TiendaRepository extends JpaRepository<TiendaEntity, Long> {
    Optional<TiendaEntity> findByNombreTienda(String nombreTienda);
}
