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
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tipo_id")
    private TipoPedidoEntity tipo;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoPedidoEntity estado;

    @ManyToOne(optional = true)
    @JoinColumn(name = "origen_tienda_id")
    private TiendaEntity origenTiendaEntity;

    @ManyToOne(optional = true)
    @JoinColumn(name = "destino_tienda_id")
    private TiendaEntity destinoTiendaEntity;

    @ManyToOne(optional = true)
    @JoinColumn(name = "origen_almacen_id")
    private AlmacenEntity origenAlmacenEntity;

    @ManyToOne(optional = true)
    @JoinColumn(name = "destino_almacen_id")
    private AlmacenEntity destinoAlmacenEntity;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuarioEntity;

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud;
    @Column(name = "fecha_recepcion")
    private LocalDate fechaRecepcion;
    @Column(name = "fecha_envio")
    private LocalDate fechaEnvio;

}
