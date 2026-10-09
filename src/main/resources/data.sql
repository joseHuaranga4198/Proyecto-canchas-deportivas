-- 1. Usuarios de prueba (Admin y Cliente)
INSERT INTO usuario (nombres, apellidos, documento, telefono, correo, contrasena, rol, estado)
VALUES ('Administrador', 'General', '00000001', '999888777', 'admin@canchas.pe', 'admin123', 'ADMINISTRADOR', 'ACTIVO');

INSERT INTO usuario (nombres, apellidos, documento, telefono, correo, contrasena, rol, estado)
VALUES ('Carlos', 'Mendoza', '74859612', '987654321', 'carlos@gmail.com', '123456', 'CLIENTE', 'ACTIVO');

INSERT INTO usuario (nombres, apellidos, documento, telefono, correo, contrasena, rol, estado)
VALUES ('Juan', 'Perez', '45871236', '912345678', 'juan@gmail.com', '123456', 'CLIENTE', 'ACTIVO');

-- 2. Categorías deportivas
INSERT INTO categoria (nombre, descripcion, estado)
VALUES ('Fútbol 5', 'Cancha de césped sintético ideal para 10 jugadores.', 'ACTIVA');

INSERT INTO categoria (nombre, descripcion, estado)
VALUES ('Fútbol 7', 'Cancha de fútbol 7 con medidas reglamentarias.', 'ACTIVA');

INSERT INTO categoria (nombre, descripcion, estado)
VALUES ('Pádel', 'Pista panorámica de vidrio templado.', 'ACTIVA');

INSERT INTO categoria (nombre, descripcion, estado)
VALUES ('Básquet', 'Cancha multideportiva techada con piso pulido.', 'ACTIVA');

-- 3. Canchas
INSERT INTO cancha (id_categoria, nombre, caracteristicas, precio_hora, estado)
VALUES (1, 'Cancha Sintética 1', 'Grass Pro con iluminación nocturna', 50.00, 'ACTIVA');

INSERT INTO cancha (id_categoria, nombre, caracteristicas, precio_hora, estado)
VALUES (1, 'Cancha Sintética 2', 'Grass techado, incluye chalecos', 40.00, 'ACTIVA');

INSERT INTO cancha (id_categoria, nombre, caracteristicas, precio_hora, estado)
VALUES (2, 'Cancha Monumental 7', 'Medidas reglamentarias y vestuarios', 80.00, 'ACTIVA');

INSERT INTO cancha (id_categoria, nombre, caracteristicas, precio_hora, estado)
VALUES (3, 'Cancha Pádel Central', 'Vidrio templado y césped azul texturado', 60.00, 'ACTIVA');

-- 4. Reservas iniciales de demostración
INSERT INTO reserva (id_usuario, id_cancha, fecha, hora_inicio, hora_fin, importe, estado)
VALUES (2, 1, CURRENT_DATE(), '18:00:00', '19:00:00', 50.00, 'CONFIRMADA');

INSERT INTO reserva (id_usuario, id_cancha, fecha, hora_inicio, hora_fin, importe, estado)
VALUES (3, 2, CURRENT_DATE(), '20:00:00', '21:00:00', 40.00, 'PENDIENTE');

-- 5. Pagos iniciales
INSERT INTO pago (id_reserva, importe, estado)
VALUES (1, 50.00, 'REGISTRADO');