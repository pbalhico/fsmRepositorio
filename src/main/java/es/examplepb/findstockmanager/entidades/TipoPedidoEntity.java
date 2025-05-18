package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

/**
 * Clase que representa la entidad TipoPedido en la base de datos.
 * Esta entidad se utiliza para almacenar los tipos de pedidos.
 */
@Entity
@Table(name = "tipo_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoPedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "descripcion_tipo", length = 40)
    private String descripcionTipo;
}
