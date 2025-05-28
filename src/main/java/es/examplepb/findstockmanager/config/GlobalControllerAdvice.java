// src/main/java/es/examplepb/findstockmanager/config/GlobalControllerAdvice.java
package es.examplepb.findstockmanager.config; // O donde tengas tus clases de configuración

import es.examplepb.findstockmanager.entidades.EstadoPedidoEntity;
import es.examplepb.findstockmanager.entidades.TipoPedidoEntity;
import es.examplepb.findstockmanager.repositorios.EstadoPedidoRepository;
import es.examplepb.findstockmanager.repositorios.PedidoRepository;
import es.examplepb.findstockmanager.repositorios.TipoPedidoRepository;
import es.examplepb.findstockmanager.util.Constants; // Asegúrate de que tus constantes estén definidas aquí
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice {

    private final PedidoRepository pedidoRepository;
    private final TipoPedidoRepository tipoPedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    @ModelAttribute("conteoPedidosTiendaPendientes")
    public long getConteoPedidosTiendaPendientes() {
        TipoPedidoEntity tipoTienda = tipoPedidoRepository.findByDescripcionTipo(Constants.TIPO_ALMACEN_TIENDA)
                .orElseGet(() -> {
                    log.warn("Tipo de pedido '{}' no encontrado. Esto afectará el conteo en el menú.", Constants.TIPO_ALMACEN_TIENDA);
                    return null;
                });
        if (tipoTienda != null) {
            return pedidoRepository.countPedidosPendientesEnTramiteByTipo(tipoTienda);
        }
        return 0;
    }

    @ModelAttribute("conteoPedidosRestockPendientes")
    public long getConteoPedidosRestockPendientes() {
        TipoPedidoEntity tipoReposicion = tipoPedidoRepository.findByDescripcionTipo(Constants.TIPO_REPOSICION_STOCK_ALMACEN)
                .orElseGet(() -> {
                    log.warn("Tipo de pedido '{}' no encontrado. Esto afectará el conteo en el menú.", Constants.TIPO_REPOSICION_STOCK_ALMACEN);
                    return null;
                });
        if (tipoReposicion != null) {
            return pedidoRepository.countPedidosPendientesEnTramiteByTipo(tipoReposicion);
        }
        return 0;
    }
}
