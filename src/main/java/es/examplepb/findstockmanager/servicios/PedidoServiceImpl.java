package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.Pedido;
import es.examplepb.findstockmanager.repositorios.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    @Override
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public List<Pedido> findAllFiltered(List<String> estados, LocalDate fechaSolicitud) {
        // Lógica para decidir qué método del repositorio llamar

        // Si solo hay filtro de fecha
        if ((estados == null || estados.isEmpty()) && fechaSolicitud != null) {
            // Llama al repositorio para buscar por fecha exacta
            return pedidoRepository.findByFechaSolicitud(fechaSolicitud); // Necesitas este método en el Repositorio
        }
        // Si solo hay filtro(s) de estado
        else if (estados != null && !estados.isEmpty() && fechaSolicitud == null) {
            // Llama al repositorio para buscar por estado(s)
            return pedidoRepository.findByEstado_DescripcionEstadoIn(estados); // Necesitas este método en el Repositorio
        }
        // Si hay filtro(s) de estado Y filtro de fecha
        else if (estados != null && !estados.isEmpty() && fechaSolicitud != null) {
            // Llama al repositorio para buscar por estado(s) Y fecha
            return pedidoRepository.findByEstado_DescripcionEstadoInAndFechaSolicitud(estados, fechaSolicitud); // Necesitas este método en el Repositorio
        }
        // Este caso (ningún filtro) debería ser manejado por el controlador llamando a findAll(),
        // pero como seguro adicional en el servicio, devolvemos una lista vacía.
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public Pedido findById(Integer id) {
        return pedidoRepository.findById(id).orElse(null);
    }
}
