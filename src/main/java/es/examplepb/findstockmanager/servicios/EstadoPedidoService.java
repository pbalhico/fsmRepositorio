package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.EstadoPedidoEntity;
import es.examplepb.findstockmanager.repositorios.EstadoPedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstadoPedidoService {
    private final EstadoPedidoRepository estadoPedidoRepository;

    public EstadoPedidoEntity findByDescripcionEstado(String descripcionEstado) {
        return estadoPedidoRepository.findByDescripcionEstado(descripcionEstado).orElse(null);
    }
}
