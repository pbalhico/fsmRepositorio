-- Consulta para verificar los pedidos y sus detalles
SELECT p.id                  AS pedido_id,
       tp.descripcion_tipo   AS tipo_pedido,
       ep.descripcion_estado AS estado_pedido,
       t1.nombre_tienda      AS origen_tienda,
       t2.nombre_tienda      AS destino_tienda,
       a1.nombre_almacen     AS origen_almacen,
       a2.nombre_almacen     AS destino_almacen,
       u.nombre              AS usuarioEntity,
       p.fecha_solicitud,
       p.fecha_recepcion,
       p.fecha_envio
FROM PEDIDO p
         LEFT JOIN TIPO_PEDIDO tp ON p.tipo_id = tp.id
         LEFT JOIN ESTADO_PEDIDO ep ON p.estado_id = ep.id
         LEFT JOIN TIENDA t1 ON p.origen_tienda_id = t1.id
         LEFT JOIN TIENDA t2 ON p.destino_tienda_id = t2.id
         LEFT JOIN ALMACEN a1 ON p.origen_almacen_id = a1.id
         LEFT JOIN ALMACEN a2 ON p.destino_almacen_id = a2.id
         LEFT JOIN USUARIO u ON p.usuario_id = u.id;

-- Consulta para verificar los artículos de un pedidoEntity específico
SELECT pa.articulo_id,
       a.nombre_articulo,
       pa.cantidad_pedido_articulo,
       pa.importe_total
FROM PEDIDO_ARTICULO pa
         JOIN ARTICULO a ON pa.articulo_id = a.id;