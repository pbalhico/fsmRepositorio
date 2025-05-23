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

    @Column(name = "descripcion_tipo", length = 40, unique = true, nullable = false)
    private String descripcionTipo;

    // Constructor para facilidad de inicialización
    public TipoPedidoEntity(String descripcionTipo) {
        this.descripcionTipo = descripcionTipo;
    }
}
