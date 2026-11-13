-- Solo se carga mediante el perfil demo, en una base H2 en memoria separada.
INSERT INTO usuario(nombres,apellidos,documento,telefono,correo,contrasena,rol,estado)
VALUES('Cliente','Demostración','70000001','999111222','cliente@canchaya.pe','cliente123','CLIENTE','ACTIVO');
INSERT INTO categoria(nombre,descripcion,estado) VALUES('Fútbol en grass','Espacios para fútbol recreativo','ACTIVA'),('Vóley','Espacios para vóley','ACTIVA');
INSERT INTO cancha(id_categoria,nombre,caracteristicas,precio_hora,estado) VALUES
(1,'Grass Central','Grass sintético, iluminación y acceso a vestuarios',50,'ACTIVA'),
(1,'Grass Norte','Espacio para tu partido con amigos',60,'ACTIVA'),
(2,'Vóley Azul','Superficie deportiva y red de vóley',35,'ACTIVA'),
(2,'Vóley Verde','Cancha exterior para encuentros recreativos',40,'ACTIVA');
INSERT INTO reserva(id_usuario,id_cancha,fecha,hora_inicio,hora_fin,importe,estado,fecha_registro)
SELECT 1,MOD(X,4)+1,DATEADD('DAY',-X,CURRENT_DATE),TIME '10:00:00',TIME '11:00:00',
CASE MOD(X,4) WHEN 0 THEN 50 WHEN 1 THEN 60 WHEN 2 THEN 35 ELSE 40 END,
CASE WHEN MOD(X,7)=0 THEN 'CANCELADA' ELSE 'CONFIRMADA' END,DATEADD('DAY',-X,CURRENT_TIMESTAMP)
FROM SYSTEM_RANGE(1,28);
INSERT INTO pago(id_reserva,importe,estado,fecha_pago) SELECT id_reserva,importe,'REGISTRADO',fecha_registro FROM reserva;
INSERT INTO devolucion(id_pago,importe,motivo,id_usuario,fecha_devolucion)
SELECT p.id_pago,p.importe,'Cancelación de demostración (simulada)',1,DATEADD('HOUR',1,p.fecha_pago)
FROM pago p JOIN reserva r ON r.id_reserva=p.id_reserva WHERE r.estado='CANCELADA';
