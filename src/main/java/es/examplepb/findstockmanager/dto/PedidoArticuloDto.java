package es.examplepb.findstockmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la entidad PedidoArticulo.
 * Representa una línea de detalle de un pedido, con información simplificada del artículo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoArticuloDto {
    private String articuloId;
    private String nombreArticulo; // Añadido para mostrar el nombre del artículo
    private Integer cantidadPedidoArticulo;
    private Double importeTotal;
}
