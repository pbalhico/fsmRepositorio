package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Integer> {
    // Busca un RolEntity por su tipoRol (ej. "manager", "empleado", "system")
    Optional<RolEntity> findByTipoRol(String tipoRol);
}
