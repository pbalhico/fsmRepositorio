package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.SeccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeccionRepository extends JpaRepository<SeccionEntity, Integer> {
    Optional<SeccionEntity> findByCategoriaSeccion(String categoriaSeccion);
}
