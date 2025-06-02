package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.dto.ArticuloDto;
import es.examplepb.findstockmanager.entidades.ArticuloEntity;
import es.examplepb.findstockmanager.entidades.SeccionEntity;
import es.examplepb.findstockmanager.mappers.ArticuloMapper;
import es.examplepb.findstockmanager.repositorios.ArticuloRepository;
import es.examplepb.findstockmanager.repositorios.SeccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;


import java.util.ArrayList;
import java.util.List;

@Service // ¡Esta anotación va aquí para que Spring la gestione como un bean!
@RequiredArgsConstructor // Para la inyección de dependencias en el constructor
@Slf4j // Para el logging
public class ArticuloServiceImpl implements ArticuloService { // ¡Ahora implementa la interfaz!

    private final ArticuloRepository articuloRepository;
    private final ArticuloMapper articuloMapper;
    private final SeccionRepository seccionRepository;

    @Override // Indica que este método implementa uno de la interfaz
    public List<ArticuloEntity> findAll() {
        return articuloRepository.findAll();
    }

    @Override
    public ArticuloEntity findById(String id) {
        log.info("Buscando artículo con ID: {}", id);
        return articuloRepository.findById(id).orElse(null);
    }

    @Override
    public List<ArticuloEntity> findAllFilteredAndSorted(String seccionId, String idArticulo, String sortByStock) {
        log.info("Filtrando y ordenando artículos: seccionId={}, idArticulo={}, sortByStock={}", seccionId, idArticulo, sortByStock);

        Specification<ArticuloEntity> spec = buildSpecification(seccionId, idArticulo);
        Sort sort = buildSort(sortByStock);

        return articuloRepository.findAll(spec, sort);
    }

    @Override
    @Transactional // Aplica la transaccionalidad aquí en la implementación
    public ArticuloEntity saveNewArticulo(ArticuloDto articuloDto) {
        log.info("Guardando nuevo artículo con ID: {}", articuloDto.getId());

        ArticuloEntity articuloEntity = articuloMapper.toEntity(articuloDto);

        seccionRepository.findById(articuloDto.getSeccionId())
                .ifPresentOrElse(
                        articuloEntity::setSeccionEntity,
                        () -> {
                            throw new IllegalArgumentException("Sección con ID " + articuloDto.getSeccionId() + " no encontrada.");
                        }
                );

        return articuloRepository.save(articuloEntity);
    }

    private Specification<ArticuloEntity> buildSpecification(String seccionId, String idArticulo) {
        return new Specification<ArticuloEntity>() {
            @Override
            public Predicate toPredicate(Root<ArticuloEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

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