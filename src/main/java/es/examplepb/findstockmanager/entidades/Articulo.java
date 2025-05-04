package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

/**
 * Clase que representa la entidad Articulo en la base de datos.
 * Esta clase es utilizada para mapear la tabla "articulo" en la base de datos.
 * Contiene información sobre los artículos disponibles en el sistema.
 */
@Entity
@Table(name = "articulo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Articulo {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;

    @Column(name = "color", length = 20)
    private String color;
    @Column(name = "precio")
    private Double precio;
    @Column(name = "talla", length = 3)
    private String talla;
    @Column(name = "nombre_articulo", length = 50)
    private String nombreArticulo;
    @Column(name = "fotografia_art", length = 200)
    private String fotografiaArt;
    @Column(name = "stock")
    private Integer stock;

}
