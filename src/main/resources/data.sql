INSERT INTO ROL (tipo_rol)
VALUES ('manager');
INSERT INTO ROL (tipo_rol)
VALUES ('empleado');

INSERT INTO SECCION (categoria_seccion)
VALUES ('Pantalones');
INSERT INTO SECCION (categoria_seccion)
VALUES ('Camisas');

INSERT INTO ALMACEN (seccion_id, nombre_almacen, almacen_direccion)
VALUES (1, 'Almacén Central', 'Calle Principal 123');

INSERT INTO TIENDA (nombre_tienda, tienda_direccion)
VALUES ('Tienda A', 'Calle Comercio 789');
INSERT INTO TIENDA (nombre_tienda, tienda_direccion)
VALUES ('Tienda B', 'Plaza Central 101');

INSERT INTO USUARIO (almacen_id, rol_id, nombre, apellido, nif, email, fotografia_usuario, password)
VALUES (1, 1, 'Juan', 'Pérez', '12345678A', 'juan.perez@example.com', 'images/persona2.jpg', 'password123');
INSERT INTO USUARIO (almacen_id, rol_id, nombre, apellido, nif, email, fotografia_usuario, password)
VALUES (1, 2, 'Ana', 'García', '87654321B', 'ana.garcia@example.com', 'images/persona1.jpg', 'password456');

INSERT INTO ARTICULO (id, seccion_id, color, precio, talla, nombre_articulo, stock, fotografia_articulo)
VALUES ('CAMS001001', 2, 'Blanco', 19.99, 'XXS', 'Camiseta', 100, 'camiseta.jpg');
INSERT INTO ARTICULO (id, seccion_id, color, precio, talla, nombre_articulo, stock, fotografia_articulo)
VALUES ('PANT002002', 1, 'Negro', 49.99, 'S', 'Pantalón', 50, 'pantalon.jpg');

INSERT INTO ESTADO_PEDIDO (descripcion_estado)
VALUES ('Pendiente');
INSERT INTO ESTADO_PEDIDO (descripcion_estado)
VALUES ('Completado');

INSERT INTO TIPO_PEDIDO (descripcion_tipo)
VALUES ('tienda-almacen');
INSERT INTO TIPO_PEDIDO (descripcion_tipo)
VALUES ('almacen-tienda');
INSERT INTO TIPO_PEDIDO (descripcion_tipo)
VALUES ('reposicion stock almacen');

INSERT INTO PEDIDO (tipo_id, estado_id, origen_tienda_id, destino_tienda_id, origen_almacen_id, destino_almacen_id,
                    usuario_id, fecha_solicitud, fecha_recepcion, fecha_envio)
VALUES (1, 1, 1, 1, null, null, 1, '2023-01-01', '2023-01-05', null);
INSERT INTO PEDIDO (tipo_id, estado_id, origen_tienda_id, destino_tienda_id, origen_almacen_id, destino_almacen_id,
                    usuario_id, fecha_solicitud, fecha_recepcion, fecha_envio)
VALUES (3, 2, null, null, 1, 1, 2, '2023-02-01', '2023-02-05', '2023-02-03');

INSERT INTO PEDIDO_ARTICULO (articulo_id, pedido_id, cantidad_pedido_articulo, importe_total)
VALUES ('CAMS001001', 1, 10, 199.90);
INSERT INTO PEDIDO_ARTICULO (articulo_id, pedido_id, cantidad_pedido_articulo, importe_total)
VALUES ('PANT002002', 2, 5, 249.95);