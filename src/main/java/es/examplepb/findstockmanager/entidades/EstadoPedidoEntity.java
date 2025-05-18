package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

/**
 * Clase que representa la entidad EstadoPedido en la base de datos.
 * Esta entidad se utiliza para almacenar los estados de los pedidos.
 */
@Entity
@Table(name = "estado_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoPedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "descripcion_estado", length = 100)
    private String descripcionEstado;

}
