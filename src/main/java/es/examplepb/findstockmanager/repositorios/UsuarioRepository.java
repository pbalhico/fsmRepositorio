package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
    //Usuario findByEmail(String email);
    Optional<UsuarioEntity> findById(Long id);

    Optional<UsuarioEntity> findByEmail(String email);
}
