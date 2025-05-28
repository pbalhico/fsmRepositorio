package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.ArticuloEntity; // Importa la entidad Articulo
import es.examplepb.findstockmanager.entidades.SeccionEntity; // Importa la entidad Seccion
import es.examplepb.findstockmanager.repositorios.ArticuloRepository; // Importa el repositorio de Articulo
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification; // Importa Specification
import org.springframework.data.domain.Sort; // Importa Sort
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // Utilidad para chequear Strings

import jakarta.persistence.criteria.Join; // Importa Join
import jakarta.persistence.criteria.Predicate; // Importa Predicate de JPA
import jakarta.persistence.criteria.Root; // Importa Root de JPA
import jakarta.persistence.criteria.CriteriaQuery; // Importa CriteriaQuery de JPA
import jakarta.persistence.criteria.CriteriaBuilder; // Importa CriteriaBuilder de JPA


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok para inyección de dependencias del repositorio
@Slf4j // Lombok para logging
public class ArticuloServiceImpl implements ArticuloService {

    private final ArticuloRepository articuloRepository;

    @Override
    public List<ArticuloEntity> findAll() {
        return articuloRepository.findAll(); // Implementación simple
    }

    @Override
    public ArticuloEntity findById(String id) {
        log.info("Buscando artículo con ID: {}", id);
        // Usa el repositorio para buscar por ID. JpaRepository ya proporciona findById.
        return articuloRepository.findById(id).orElse(null); // Usa orElse(null) si el método de servicio retorna null
    }

    @Override
    public List<ArticuloEntity> findAllFilteredAndSorted(String seccionId, String idArticulo, String sortByStock) {
        log.info("Filtrando y ordenando artículos: seccionId={}, idArticulo={}, sortByStock={}", seccionId, idArticulo, sortByStock);

        Specification<ArticuloEntity> spec = buildSpecification(seccionId, idArticulo);
        Sort sort = buildSort(sortByStock);

        return articuloRepository.findAll(spec, sort);
    }

    private Specification<ArticuloEntity> buildSpecification(String seccionId, String idArticulo) {
        return new Specification<ArticuloEntity>() {
            @Override
            public Predicate toPredicate(Root<ArticuloEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                // Filtro por Sección
                if (StringUtils.hasText(seccionId)) {
                    Join<ArticuloEntity, SeccionEntity> seccionJoin = root.join("seccionEntity");
                    try {
                        Integer seccionIdInt = Integer.valueOf(seccionId);
                        predicates.add(criteriaBuilder.equal(seccionJoin.get("id"), seccionIdInt));
                    } catch (NumberFormatException e) {
                        log.error("Error al convertir seccionId a Integer: {}", seccionId, e);
                    }
                }

                if (StringUtils.hasText(idArticulo)) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("id")), "%" + idArticulo.toLowerCase() + "%"));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    private Sort buildSort(String sortByStock) {
        if ("asc".equals(sortByStock)) {
            return Sort.by(Sort.Direction.ASC, "stock");
        } else if ("desc".equals(sortByStock)) {
            return Sort.by(Sort.Direction.DESC, "stock");
        } else {
            return Sort.unsorted();
        }
    }
}
