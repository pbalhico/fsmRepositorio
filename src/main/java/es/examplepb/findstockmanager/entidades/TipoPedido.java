package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "tipo_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tipoId;

    private String descripcionTipo;
}
