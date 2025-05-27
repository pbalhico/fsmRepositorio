package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.AlmacenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlmacenRepository extends JpaRepository<AlmacenEntity, Long> {
    Optional<AlmacenEntity> findByNombreAlmacen(String nombreAlmacen);
}
