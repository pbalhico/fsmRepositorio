package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "articulo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Articulo {
    @Id
    private String articuloId;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;

    private String color;
    private Double precio;
    private String talla;
    private String nombreArticulo;
    private String fotografiaArt;
    private Integer stock;
}
