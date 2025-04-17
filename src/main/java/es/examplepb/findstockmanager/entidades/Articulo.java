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
    private String Id;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;

    @Column(name = "color")
    private String color;
    @Column(name = "precio")
    private Double precio;
    @Column(name = "talla")
    private String talla;
    @Column(name = "nombre_articulo")
    private String nombreArticulo;
    @Column(name = "fotografia_art")
    private String fotografiaArt;
    @Column(name = "stock")
    private Integer stock;

}
