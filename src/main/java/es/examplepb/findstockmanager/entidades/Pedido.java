package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

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

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud;
    @Column(name = "fecha_recepcion")
    private LocalDate fechaRecepcion;
    @Column(name = "fecha_envio")
    private LocalDate fechaEnvio;

}
