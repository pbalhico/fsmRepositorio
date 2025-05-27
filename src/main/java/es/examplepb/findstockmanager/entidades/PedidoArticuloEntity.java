package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pedido_articulo")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PedidoArticuloEntity {

    @EmbeddedId
    private PedidoArticuloIdEntity id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pedidoId")
    @JoinColumn(name = "pedido_id")
    private PedidoEntity pedidoEntity;

    @ManyToOne(fetch = FetchType.LAZY) // Considera FetchType.LAZY
    @MapsId("articulo") // 'articulo' es el nombre del campo en PedidoArticuloIdEntity
    @JoinColumn(name = "articulo_id", referencedColumnName = "id") // Asegúrate de referencedColumnName si es necesario
    private ArticuloEntity articuloEntity;

    @Column(name = "cantidad_pedido_articulo")
    private Integer cantidadPedidoArticulo;
    @Column(name = "importe_total")
    private Double importeTotal;

    @Column(name = "recibido", nullable = false)
    private Boolean recibido; // Indica si el artículo ha sido recibido o no
}
