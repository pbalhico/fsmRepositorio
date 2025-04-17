package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "almacen")
@Data
@NoArgsConstructor
public class Almacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;

    @Column(name = "nombre_almacen")
    private String nombreAlmacen;
    @Column(name = "almacen_direccion")
    private String almacenDireccion;
}
