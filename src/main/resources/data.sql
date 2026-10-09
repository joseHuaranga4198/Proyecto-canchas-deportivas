-- Usuarios (Admin y Cliente)
INSERT INTO usuario (nombres, apellidos, documento, telefono, correo, contrasena, rol, estado) VALUES
                                                                                                   ('Roberto', 'Zarate', '10203040', '987654321', 'admin@canchas.pe', 'admin123', 'ADMINISTRADOR', 'ACTIVO'),
                                                                                                   ('Carlos', 'Escobar', '72345678', '912345678', 'carlos@gmail.com', '123456', 'CLIENTE', 'ACTIVO'),
                                                                                                   ('Juan', 'Perez', '45678901', '998877665', 'juan@gmail.com', '123456', 'CLIENTE', 'BLOQUEADO');

-- Categorías
INSERT INTO categoria (nombre, descripcion, estado) VALUES
                                                        ('Fútbol 5', 'Grass sintético para 10 personas', 'ACTIVA'),
                                                        ('Fútbol 7', 'Grass sintético reglamentario', 'ACTIVA'),
                                                        ('Pádel', 'Canchas panorámicas de cristal', 'ACTIVA');

-- Canchas
INSERT INTO cancha (id_categoria, nombre, caracteristicas, precio_hora, estado) VALUES
                                                                                    (1, 'Cancha Sintética 1', 'Grass Pro con iluminación nocturna', 50.00, 'ACTIVA'),
                                                                                    (1, 'Cancha Sintética 2', 'Grass techado, incluye chalecos', 40.00, 'ACTIVA'),
                                                                                    (2, 'Cancha Monumental 7', 'Medidas reglamentarias y vestuarios', 80.00, 'ACTIVA'),
                                                                                    (3, 'Cancha Pádel Central', 'Vidrio templado y césped azul texturado', 60.00, 'INACTIVA');

-- Reservas y Pagos Semilla
INSERT INTO reserva (id_usuario, id_cancha, fecha, hora_inicio, hora_fin, importe, estado, fecha_registro) VALUES
                                                                                                               (2, 1, CURRENT_DATE(), '18:00:00', '19:00:00', 50.00, 'CONFIRMADA', CURRENT_TIMESTAMP()),
                                                                                                               (2, 2, CURRENT_DATE(), '20:00:00', '22:00:00', 80.00, 'PENDIENTE', CURRENT_TIMESTAMP());

INSERT INTO pago (id_reserva, importe, fecha_pago, estado) VALUES
    (1, 50.00, CURRENT_TIMESTAMP(), 'REGISTRADO');