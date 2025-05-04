package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.Pedido;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface PedidoService {
    List<Pedido> findAll();

    Pedido findById(Integer id);

    // Método para obtener pedidos filtrados por estado y/o fecha de solicitud
    List<Pedido> findAllFiltered(List<String> estados, LocalDate fechaSolicitud);
}
