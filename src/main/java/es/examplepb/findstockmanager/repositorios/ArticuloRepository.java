package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloRepository extends JpaRepository<ArticuloEntity, String>, JpaSpecificationExecutor<ArticuloEntity> {
    // Método para encontrar artículos con stock por debajo de un mínimo.
    List<ArticuloEntity> findByStockLessThan(Integer stockMinimo);
}
