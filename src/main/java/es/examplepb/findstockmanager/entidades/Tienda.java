package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "tienda")
@Data
@NoArgsConstructor
public class Tienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_tienda", length = 50)
    private String nombreTienda;
    @Column(name = "tienda_direccion", length = 120)
    private String tiendaDireccion;
}
