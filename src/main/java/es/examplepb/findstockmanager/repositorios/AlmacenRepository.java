package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.AlmacenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlmacenRepository extends JpaRepository<AlmacenEntity, Integer> {
    AlmacenEntity findByNombreAlmacen(String nombreAlmacen);
}
