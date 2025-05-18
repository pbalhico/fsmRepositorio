package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.ArticuloEntity;

import java.util.List;

public interface ArticuloService {
    List<ArticuloEntity> findAllFilteredAndSorted(String seccionId, String nombre, String sortByStock);

    ArticuloEntity findById(String id);
}
