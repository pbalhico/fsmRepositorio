package es.examplepb.findstockmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto {
    private Integer id;
    private String tipoPedido;
    private String estadoPedido;
    private String nombreOrigenTienda;
    private String nombreDestinoTienda;
    private String nombreOrigenAlmacen;
    private String nombreDestinoAlmacen;
    private String nombreUsuario;
    private LocalDate fechaSolicitud;
    private LocalDate fechaRecepcion;
    private LocalDate fechaEnvio;
    private List<PedidoArticuloDto> articulos;
}
