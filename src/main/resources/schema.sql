DROP TABLE IF EXISTS pago;
DROP TABLE IF EXISTS reserva;
DROP TABLE IF EXISTS cancha;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
                         id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nombres VARCHAR(100) NOT NULL,
                         apellidos VARCHAR(100) NOT NULL,
                         documento VARCHAR(20) NOT NULL UNIQUE,
                         telefono VARCHAR(20),
                         correo VARCHAR(100) NOT NULL UNIQUE,
                         contrasena VARCHAR(255) NOT NULL,
                         rol VARCHAR(20) NOT NULL,
                         estado VARCHAR(20) NOT NULL
);

CREATE TABLE categoria (
                           id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(50) NOT NULL UNIQUE,
                           descripcion VARCHAR(255),
                           estado VARCHAR(20) NOT NULL
);

CREATE TABLE cancha (
                        id_cancha BIGINT AUTO_INCREMENT PRIMARY KEY,
                        id_categoria BIGINT NOT NULL,
                        nombre VARCHAR(100) NOT NULL,
                        caracteristicas VARCHAR(500),
                        precio_hora DECIMAL(10,2) NOT NULL,
                        estado VARCHAR(20) NOT NULL,
                        CONSTRAINT fk_cancha_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE reserva (
                         id_reserva BIGINT AUTO_INCREMENT PRIMARY KEY,
                         id_usuario BIGINT NOT NULL,
                         id_cancha BIGINT NOT NULL,
                         fecha DATE NOT NULL,
                         hora_inicio TIME NOT NULL,
                         hora_fin TIME NOT NULL,
                         importe DECIMAL(10,2) NOT NULL,
                         estado VARCHAR(20) NOT NULL,
                         fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_reserva_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
                         CONSTRAINT fk_reserva_cancha FOREIGN KEY (id_cancha) REFERENCES cancha(id_cancha)
);

CREATE TABLE pago (
                      id_pago BIGINT AUTO_INCREMENT PRIMARY KEY,
                      id_reserva BIGINT NOT NULL UNIQUE,
                      importe DECIMAL(10,2) NOT NULL,
                      fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      estado VARCHAR(20) NOT NULL,
                      CONSTRAINT fk_pago_reserva FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva)
);