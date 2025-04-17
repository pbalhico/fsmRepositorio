package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "pedido_articulo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PedidoArticuloId.class)
public class PedidoArticulo {
    @Id
    @ManyToOne
    @JoinColumn(name = "articulo_id")
    private Articulo articulo;

    @Id
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    private Integer cantidadPedidoArticulo;
    private Double importeTotal;
}
