package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloRepository extends JpaRepository<ArticuloEntity, String>, JpaSpecificationExecutor<ArticuloEntity> {
}
