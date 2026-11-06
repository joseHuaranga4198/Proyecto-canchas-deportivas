-- Solo se ejecuta con el perfil demo, en una base en memoria independiente.
-- Identificadores generados por H2 en orden de inserción; no se fijan los IDs.
INSERT INTO usuario (nombres, apellidos, documento, telefono, correo, contrasena, rol, estado) VALUES
('Valeria', 'Rojas', '90000001', '900000001', 'admin@demo.test', 'Demo2026!', 'ADMINISTRADOR', 'ACTIVO'),
('Camila', 'Torres', '90000002', '900000002', 'camila@demo.test', 'Demo2026!', 'CLIENTE', 'ACTIVO'),
('Diego', 'Salazar', '90000003', '900000003', 'diego@demo.test', 'Demo2026!', 'CLIENTE', 'ACTIVO'),
('Lucía', 'Mendoza', '90000004', '900000004', 'lucia@demo.test', 'Demo2026!', 'CLIENTE', 'ACTIVO'),
('Marco', 'Vargas', '90000005', '900000005', 'bloqueado@demo.test', 'Demo2026!', 'CLIENTE', 'BLOQUEADO');

INSERT INTO categoria (nombre, descripcion, estado) VALUES
('Fútbol 5', 'Partidos rápidos en césped sintético para equipos de cinco.', 'ACTIVA'),
('Fútbol 7', 'Espacios amplios para torneos y encuentros entre amigos.', 'ACTIVA'),
('Pádel', 'Pistas para parejas, con iluminación y cerramiento.', 'ACTIVA'),
('Básquet', 'Canchas para entrenamientos y partidos de equipo.', 'ACTIVA'),
('Vóley', 'Categoría disponible próximamente.', 'INACTIVA');

INSERT INTO cancha (id_categoria, nombre, caracteristicas, precio_hora, estado) VALUES
(1, 'Arena Central', 'Césped sintético, iluminación nocturna y vestuarios.', 50.00, 'ACTIVA'),
(1, 'Arena Techada', 'Cancha cubierta para jugar con lluvia; incluye chalecos.', 60.00, 'ACTIVA'),
(2, 'Estadio del Valle', 'Espacio para fútbol 7, graderías y estacionamiento.', 90.00, 'ACTIVA'),
(3, 'Pádel Panorámico', 'Paredes de vidrio, césped azul y luz LED.', 70.00, 'ACTIVA'),
(4, 'Coliseo Norte', 'Piso deportivo, tableros y marcación reglamentaria.', 40.00, 'ACTIVA'),
(1, 'Arena Sur', 'Fuera de servicio por renovación del césped.', 45.00, 'INACTIVA');

-- Fechas relativas al inicio: permiten repetir la exposición otro día.
-- 1: futura cancelable; 2: próxima medianoche (menos de 24 h), no cancelable por cliente.
INSERT INTO reserva (id_usuario, id_cancha, fecha, hora_inicio, hora_fin, importe, estado) VALUES
(2, 1, DATEADD('DAY', 3, CURRENT_DATE), '18:00', '19:00', 50.00, 'CONFIRMADA'),
(2, 2, DATEADD('DAY', 1, CURRENT_DATE), '00:00', '01:00', 60.00, 'CONFIRMADA'),
(2, 4, DATEADD('DAY', 4, CURRENT_DATE), '10:00', '11:30', 105.00, 'CANCELADA'),
(2, 5, DATEADD('DAY', -2, CURRENT_DATE), '16:00', '17:00', 40.00, 'CONFIRMADA'),
(3, 1, DATEADD('DAY', 3, CURRENT_DATE), '19:00', '20:00', 50.00, 'CONFIRMADA'),
(3, 3, DATEADD('DAY', 5, CURRENT_DATE), '15:00', '17:00', 180.00, 'CONFIRMADA'),
(3, 4, DATEADD('DAY', 4, CURRENT_DATE), '12:00', '13:00', 70.00, 'CONFIRMADA'),
(3, 6, DATEADD('DAY', -5, CURRENT_DATE), '18:00', '19:00', 45.00, 'CONFIRMADA');

-- Un pago por cada reserva, incluidas las canceladas; no simula reembolso.
INSERT INTO pago (id_reserva, importe, estado)
SELECT id_reserva, importe, 'REGISTRADO' FROM reserva;

