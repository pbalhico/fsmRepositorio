package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Clase que representa la entidad Pedido en la base de datos.
 * Esta entidad se utiliza para almacenar los pedidos realizados por los usuarios.
 */
@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tipo_id")
    private TipoPedido tipo;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoPedido estado;

    @ManyToOne(optional = true)
    @JoinColumn(name = "origen_tienda_id")
    private Tienda origenTienda;

    @ManyToOne(optional = true)
    @JoinColumn(name = "destino_tienda_id")
    private Tienda destinoTienda;

    @ManyToOne(optional = true)
    @JoinColumn(name = "origen_almacen_id")
    private Almacen origenAlmacen;

    @ManyToOne(optional = true)
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
