package es.examplepb.findstockmanager.repositorios;

import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloRepository extends JpaRepository<ArticuloEntity, String>, JpaSpecificationExecutor<ArticuloEntity> {
    List<ArticuloEntity> findByStockLessThan(Integer stockMinimo);

    // *******************************************************************
    // NUEVA CONSULTA: Artículos con stock bajo Y sin pedidos de reposición pendientes/en trámite
    // *******************************************************************
    @Query("SELECT a FROM ArticuloEntity a " +
            "WHERE a.stock < :stockMinimo " +
            "AND NOT EXISTS ( " +
            "    SELECT pa FROM PedidoArticuloEntity pa " +
            "    JOIN pa.pedidoEntity p " +
            "    WHERE pa.articuloEntity = a " +
            "    AND p.tipo.descripcionTipo = :tipoReposicionDescripcion " +
            "    AND (p.estado.descripcionEstado = :estadoPendienteDescripcion OR p.estado.descripcionEstado = :estadoEnTramiteDescripcion) " +
            ")")
    List<ArticuloEntity> findArticulosParaReposicionSinPedidosActivos(
            @Param("stockMinimo") Integer stockMinimo,
            @Param("tipoReposicionDescripcion") String tipoReposicionDescripcion,
            @Param("estadoPendienteDescripcion") String estadoPendienteDescripcion,
            @Param("estadoEnTramiteDescripcion") String estadoEnTramiteDescripcion);
}
