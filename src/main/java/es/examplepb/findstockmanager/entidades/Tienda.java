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
    private Integer tiendaId;

    private String nombreTienda;
    private String tiendaDireccion;
}
