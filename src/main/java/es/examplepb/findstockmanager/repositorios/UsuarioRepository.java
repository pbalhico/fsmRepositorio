package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {}
