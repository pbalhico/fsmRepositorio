package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.AlmacenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlmacenRepository extends JpaRepository<AlmacenEntity, Long> {
    AlmacenEntity findByNombreAlmacen(String nombreAlmacen);
}
