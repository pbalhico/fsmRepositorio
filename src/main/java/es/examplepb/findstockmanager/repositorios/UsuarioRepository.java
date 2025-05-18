package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
    //Usuario findByEmail(String email);
    UsuarioEntity findByEmailAndPassword(String email, String password);

    Optional<UsuarioEntity> findByEmail(String email);
}
