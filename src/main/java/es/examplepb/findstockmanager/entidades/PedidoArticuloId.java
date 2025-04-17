package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Builder
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
// como necesito identificar desde la tabla pedido_articulo mas de 1 campo primary, necesito una clase que tenga esa mezcla
public class PedidoArticuloId implements Serializable {

        private String articulo;
        private Integer pedido;

}
