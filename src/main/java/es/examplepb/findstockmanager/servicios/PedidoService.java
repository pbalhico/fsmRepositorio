package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.PedidoEntity;
import es.examplepb.findstockmanager.entidades.PedidoArticuloEntity;
import es.examplepb.findstockmanager.entidades.ArticuloEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    List<PedidoEntity> findAll();

    Optional<PedidoEntity> findById(Integer id);

    // --- NUEVO MÉTODO ---
    Optional<PedidoEntity> findByIdAndSetFechaRecepcion(Integer id);

    // -------------------
    List<PedidoEntity> findAllFiltered(List<String> estados, LocalDate fechaSolicitud);

    List<PedidoEntity> findAllFiltered(List<String> estados, LocalDate fechaSolicitud, String destinoTipo);

    boolean finalizarPedido(Integer pedidoId);

    PedidoEntity save(PedidoEntity pedidoEntity);

    List<PedidoArticuloEntity> findArticulosByPedidoId(Integer pedidoId);

    PedidoEntity addArticuloToPedido(Integer pedidoId, String articuloId, Integer cantidad);

    List<ArticuloEntity> findAllArticulos();
}