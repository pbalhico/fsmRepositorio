package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "articulo")
@Data
@NoArgsConstructor
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
