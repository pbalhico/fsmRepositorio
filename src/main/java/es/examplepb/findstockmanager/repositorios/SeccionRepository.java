package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.SeccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeccionRepository extends JpaRepository<SeccionEntity, Integer> {
}
