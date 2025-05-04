package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

/**
 * Clase que representa la entidad Tienda en la base de datos.
 * Esta entidad se utiliza para almacenar información sobre las tiendas.
 */
@Entity
@Table(name = "tienda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_tienda", length = 50)
    private String nombreTienda;
    @Column(name = "tienda_direccion", length = 120)
    private String tiendaDireccion;
}
