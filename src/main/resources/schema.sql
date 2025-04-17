-- Eliminar primero tablas dependientes
DROP TABLE IF EXISTS PEDIDO_ARTICULO;
DROP TABLE IF EXISTS PEDIDO;
DROP TABLE IF EXISTS ARTICULO;
DROP TABLE IF EXISTS USUARIO;
DROP TABLE IF EXISTS TIENDA;
DROP TABLE IF EXISTS ALMACEN;
DROP TABLE IF EXISTS TIPO_PEDIDO;
DROP TABLE IF EXISTS ESTADO_PEDIDO;
DROP TABLE IF EXISTS SECCION;
DROP TABLE IF EXISTS ROL;

-- Crear tablas desde cero
CREATE TABLE ROL (
                     id INT AUTO_INCREMENT PRIMARY KEY,
                     tipo_rol VARCHAR(10)
);

CREATE TABLE SECCION (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         categoria_seccion VARCHAR(50) UNIQUE
);

CREATE TABLE ALMACEN (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         seccion_id INT,
                         nombre_almacen VARCHAR(50),
                         almacen_direccion VARCHAR(120),
                         FOREIGN KEY (seccion_id) REFERENCES SECCION(id)
);

CREATE TABLE TIENDA (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nombre_tienda VARCHAR(50),
                        tienda_direccion VARCHAR(120)
);

CREATE TABLE USUARIO (
                         id INT PRIMARY KEY,
                         almacen_id INT,
                         rol_id INT,
                         nombre VARCHAR(50),
                         apellido VARCHAR(50),
                         nif VARCHAR(9) UNIQUE,
                         email VARCHAR(50) UNIQUE,
                         fotografia VARCHAR(200),
                         password VARCHAR(15),
                         FOREIGN KEY (almacen_id) REFERENCES ALMACEN(id),
                         FOREIGN KEY (rol_id) REFERENCES ROL(id)
);

CREATE TABLE ARTICULO (
                          id VARCHAR(10) PRIMARY KEY,
                          seccion_id INT,
                          color VARCHAR(20),
                          precio DOUBLE,
                          talla VARCHAR(3),
                          nombre_articulo VARCHAR(50),
                          stock INT,
                          fotografiaArt VARCHAR(200),
                          FOREIGN KEY (seccion_id) REFERENCES SECCION(id)
);

CREATE TABLE ESTADO_PEDIDO (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               descripcion_estado VARCHAR(100)
);

CREATE TABLE TIPO_PEDIDO (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             descripcion_tipo VARCHAR(40)
);

CREATE TABLE PEDIDO (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        tipo_id INT,
                        estado_id INT,
                        origen_tienda_id INT NULL,
                        destino_tienda_id INT NULL,
                        origen_almacen_id INT NULL,
                        destino_almacen_id INT NULL,
                        usuario_id INT,
                        fecha_solicitud DATE,
                        fecha_recepcion DATE,
                        fecha_envio DATE,
                        FOREIGN KEY (tipo_id) REFERENCES TIPO_PEDIDO(id),
                        FOREIGN KEY (estado_id) REFERENCES ESTADO_PEDIDO(id),
                        FOREIGN KEY (origen_tienda_id) REFERENCES TIENDA(id),
                        FOREIGN KEY (destino_tienda_id) REFERENCES TIENDA(id),
                        FOREIGN KEY (origen_almacen_id) REFERENCES ALMACEN(id),
                        FOREIGN KEY (destino_almacen_id) REFERENCES ALMACEN(id),
                        FOREIGN KEY (usuario_id) REFERENCES USUARIO(id)
);

CREATE TABLE PEDIDO_ARTICULO (
                                 articulo_id VARCHAR(10),
                                 pedido_id INT,
                                 cantidad_pedido_articulo INT,
                                 importe_total DOUBLE,
                                 PRIMARY KEY (articulo_id, pedido_id),
                                 FOREIGN KEY (articulo_id) REFERENCES ARTICULO(id),
                                 FOREIGN KEY (pedido_id) REFERENCES PEDIDO(id)
);
