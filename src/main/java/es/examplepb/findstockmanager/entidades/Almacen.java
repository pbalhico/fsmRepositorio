package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "almacen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Almacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer almacenId;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;

    private String nombreAlmacen;
    private String almacenDireccion;
}
