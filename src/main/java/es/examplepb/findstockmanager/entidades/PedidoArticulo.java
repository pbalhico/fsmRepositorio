package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pedido_articulo")
@Data
@NoArgsConstructor
public class PedidoArticulo {

    @EmbeddedId
    private PedidoArticuloId id;

    @Column(name = "cantidad_pedido_articulo")
    private Integer cantidadPedidoArticulo;
    private Double importeTotal;

}
