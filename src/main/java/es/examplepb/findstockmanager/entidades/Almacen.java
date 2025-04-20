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
    @Column(name = "id") //no deberia hacer falta añadir el column si se va a llamar igual que en la base de datos
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;

    @Column(name = "nombre_almacen", length = 50)
    private String nombreAlmacen;
    @Column(name = "almacen_direccion", length = 120)
    private String almacenDireccion;
}
