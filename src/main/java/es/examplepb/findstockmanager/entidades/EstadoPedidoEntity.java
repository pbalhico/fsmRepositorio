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
    @Column(name = "descripcion_estado", length = 100, unique = true, nullable = false)
    private String descripcionEstado;

    /* 3 tipos de estados:
    1. Pendiente
    2. En tramite
    3. Completado
     */

    public EstadoPedidoEntity(String descripcionEstado) {
        this.descripcionEstado = descripcionEstado;
    }
}
