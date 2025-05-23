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
public class TiendaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_tienda", length = 50)
    private String nombreTienda;
    @Column(name = "tienda_direccion", length = 120)
    private String tiendaDireccion;

    // Constructor para facilidad de inicialización (si solo necesitas el ID y nombre)
    public TiendaEntity(Long id, String nombreTienda) {
        this.id = id;
        this.nombreTienda = nombreTienda;
    }

    // Constructor mínimo para pruebas o casos donde solo se conoce el ID
    public TiendaEntity(Long id) {
        this.id = id;
    }
}
