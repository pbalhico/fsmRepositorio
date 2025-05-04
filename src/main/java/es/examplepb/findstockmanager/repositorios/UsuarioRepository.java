package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByEmail(String email);
    Usuario findByEmailAndPassword(String email, String password);

}
