package es.examplepb.findstockmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la entidad Articulo.
 * Utilizado para transferir datos de Articulo de forma más ligera,
 * evitando la exposición de toda la entidad y las relaciones cíclicas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloDto {
    private String id;
    private String nombreArticulo;
    private String color;
    private Double precio;
    private String talla;
    private Integer stock;
    private String fotografiaArt;
    private String categoriaSeccion; // Nombre de la sección para mostrar
    private Integer seccionId; // ID de la sección
}
