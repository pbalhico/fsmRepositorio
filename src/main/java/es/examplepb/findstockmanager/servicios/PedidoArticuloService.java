package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import es.examplepb.findstockmanager.repositorios.PedidoArticuloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoArticuloService {

    private final PedidoArticuloRepository pedidoArticuloRepository;

    public List<PedidoArticuloEntity> findByPedidoId(Integer pedidoId) {
        return pedidoArticuloRepository.findById_PedidoId(pedidoId);
    }
}