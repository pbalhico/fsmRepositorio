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

    // Mapea la parte 'pedido' del EmbeddedId a la entidad Pedido
    @ManyToOne
    @MapsId("pedido") // 'pedido' es el nombre del campo en PedidoArticuloId
    @JoinColumn(name = "pedido_id") // Nombre de la columna FK en la tabla pedido_articulo
    private PedidoEntity pedidoEntity; // Propiedad que representa la entidad Pedido asociada

    // Mapea la parte 'articulo' del EmbeddedId a la entidad Articulo
    @ManyToOne
    @MapsId("articulo") // 'articulo' es el nombre del campo en PedidoArticuloId
    @JoinColumn(name = "articulo_id") // Nombre de la columna FK en la tabla pedido_articulo
    private ArticuloEntity articuloEntity; // Propiedad que representa la entidad Articulo asociada (nombre cambiado para evitar conflicto con el campo 'articulo' en PedidoArticuloId)

    @Column(name = "cantidad_pedido_articulo")
    private Integer cantidadPedidoArticulo;
    @Column(name = "importe_total")
    private Double importeTotal;

}
