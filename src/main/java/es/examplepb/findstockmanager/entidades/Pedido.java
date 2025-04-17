package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pedidoId;

    @ManyToOne
    @JoinColumn(name = "tipo_id")
    private TipoPedido tipo;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoPedido estado;

    @ManyToOne
    @JoinColumn(name = "origen_tienda_id")
    private Tienda origenTienda;

    @ManyToOne
    @JoinColumn(name = "destino_tienda_id")
    private Tienda destinoTienda;

    @ManyToOne
    @JoinColumn(name = "origen_almacen_id")
    private Almacen origenAlmacen;

    @ManyToOne
    @JoinColumn(name = "destino_almacen_id")
    private Almacen destinoAlmacen;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDate fechaSolicitud;
    private LocalDate fechaRecepcion;
    private LocalDate fechaEnvio;
}
