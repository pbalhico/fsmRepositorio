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
    public List<ArticuloEntity> findAllFilteredAndSorted(String seccionId, String nombre, String sortByStock) {
        log.info("Filtrando y ordenando artículos: seccionId={}, nombre={}, sortByStock={}", seccionId, nombre, sortByStock);

        // 1. Construir la Specification para los filtros
        Specification<ArticuloEntity> spec = buildSpecification(seccionId, nombre);

        // 2. Construir el objeto Sort para la ordenación
        Sort sort = buildSort(sortByStock);

        // 3. Llamar al repositorio con la Specification y el Sort
        return articuloRepository.findAll(spec, sort);
    }

    private Specification<ArticuloEntity> buildSpecification(String seccionId, String nombre) {
        // Implementación de la interfaz Specification usando una clase anónima
        return new Specification<ArticuloEntity>() {
            @Override
            public Predicate toPredicate(Root<ArticuloEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                // Filtro por Sección
                if (StringUtils.hasText(seccionId)) {
                    // Unirse a la entidad Seccion. 'seccion' es el nombre de la propiedad ManyToOne en Articulo
                    Join<ArticuloEntity, SeccionEntity> seccionJoin = root.join("seccion");
                    // 'id' es la propiedad ID en Seccion. Asegúrate de que el tipo de seccionId coincide con el tipo del ID de Seccion (Integer)
                    // Si el ID de Seccion es Integer y seccionId es String, necesitarás convertir seccionId a Integer aquí.
                    try {
                        Integer seccionIdInt = Integer.valueOf(seccionId);
                        predicates.add(criteriaBuilder.equal(seccionJoin.get("id"), seccionIdInt));
                    } catch (NumberFormatException e) {
                        log.error("Error al convertir seccionId a Integer: {}", seccionId, e);
                        // Decide cómo manejar un seccionId inválido: ignorar el filtro, lanzar excepción, etc.
                        // Por ahora, simplemente logueamos el error y no añadimos el predicado.
                    }
                }

                // Filtro por Nombre (ignorando mayúsculas/minúsculas y buscando si contiene)
                if (StringUtils.hasText(nombre)) {
                    // 'nombreArticulo' es la propiedad en Articulo
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombreArticulo")), "%" + nombre.toLowerCase() + "%"));
                }

                // Combinar todos los predicados con AND
                // criteriaBuilder.and() espera un array de jakarta.persistence.criteria.Predicate
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    private Sort buildSort(String sortByStock) {
        if ("asc".equals(sortByStock)) {
            // Ordenar por la propiedad 'stock' de forma ascendente
            return Sort.by(Sort.Direction.ASC, "stock");
        } else if ("desc".equals(sortByStock)) {
            // Ordenar por la propiedad 'stock' de forma descendente
            return Sort.by(Sort.Direction.DESC, "stock");
        } else {
            // Sin ordenación si el parámetro no es 'asc' o 'desc'
            return Sort.unsorted();
        }
    }
}
