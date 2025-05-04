package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

/**
 * Almacen entity class representing the 'almacen' table in the database.
 * This class is used to map the database table to a Java object.
 * It includes fields for id, seccion, nombreAlmacen, and almacenDireccion.
 * The class uses Lombok annotations for boilerplate code generation.
 */
@Entity
@Table(name = "almacen")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
