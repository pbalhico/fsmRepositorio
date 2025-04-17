package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "estado_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer estadoId;

    private String descripcionEstado;
}
