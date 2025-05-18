package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.PedidoEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface PedidoService {
    List<PedidoEntity> findAll();

    PedidoEntity findById(Integer id);

    // Método para obtener pedidos filtrados por estado y/o fecha de solicitud
    List<PedidoEntity> findAllFiltered(List<String> estados, LocalDate fechaSolicitud);

    List<PedidoEntity> findAllFiltered(List<String> estados, LocalDate fechaSolicitud, String destinoTipo);

    List<PedidoArticuloEntity> findArticulosByPedidoId(Integer pedidoId);
}
