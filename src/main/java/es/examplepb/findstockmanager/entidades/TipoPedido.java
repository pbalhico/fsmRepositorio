package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "tipo_pedido")
@Data
@NoArgsConstructor
public class TipoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tipoId;

    private String descripcionTipo;
}
