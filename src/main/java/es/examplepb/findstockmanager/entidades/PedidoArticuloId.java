package es.examplepb.findstockmanager.entidades;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// como necesito identificar desde la tabla pedido_articulo mas de 1 campo primary, necesito una clase que tenga esa mezcla
public class PedidoArticuloId implements Serializable {
        private String articulo;
        private Integer pedido;

        // equals() y hashCode()
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PedidoArticuloId)) return false;
            PedidoArticuloId that = (PedidoArticuloId) o;
            return Objects.equals(articulo, that.articulo) &&
                    Objects.equals(pedido, that.pedido);
        }

        @Override
        public int hashCode() {
            return Objects.hash(articulo, pedido);
        }
}
