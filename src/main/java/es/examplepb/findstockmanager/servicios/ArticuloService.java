package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.dto.ArticuloDto;
import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import es.examplepb.findstockmanager.mappers.ArticuloMapper;
import es.examplepb.findstockmanager.repositorios.ArticuloRepository;
import es.examplepb.findstockmanager.repositorios.SeccionRepository; // Necesario para buscar la Sección por ID
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticuloService {

    List<ArticuloEntity> findAll();

    List<ArticuloEntity> findAllFilteredAndSorted(String seccionId, String idArticulo, String sortByStock);

    ArticuloEntity findById(String id);

    ArticuloEntity saveNewArticulo(ArticuloDto articuloDto);
}
