INSERT INTO usuario(nombres,apellidos,documento,telefono,correo,contrasena,rol,estado)
SELECT v.nombres,v.apellidos,v.documento,v.telefono,v.correo,v.contrasena,v.rol,v.estado
FROM (VALUES
 ('Cliente','Demostración','70000001','999111222','cliente@canchaya.pe','cliente123','CLIENTE','ACTIVO'),
 ('Administrador','CanchaYa','00000001',NULL,'admin@canchas.pe','admin123','ADMINISTRADOR','ACTIVO'),
 ('Ana','Torres Quispe','70000002','988222333','ana@canchaya.pe','cliente123','CLIENTE','ACTIVO'),
 ('Luis','Ramos Huamán','70000003','977333444','luis@canchaya.pe','cliente123','CLIENTE','SUSPENDIDO')
) AS v(nombres,apellidos,documento,telefono,correo,contrasena,rol,estado)
WHERE NOT EXISTS (SELECT 1 FROM usuario u WHERE LOWER(u.correo) = LOWER(v.correo) OR u.documento = v.documento);

INSERT INTO categoria(nombre,descripcion,estado)
SELECT v.nombre,v.descripcion,v.estado
FROM (VALUES
 ('Fútbol en grass','Espacios para fútbol recreativo','ACTIVA'),
 ('Vóley','Espacios para vóley','ACTIVA'),
 ('Básquet','Categoría en evaluación, sin canchas publicadas','INACTIVA')
) AS v(nombre,descripcion,estado)
WHERE NOT EXISTS (SELECT 1 FROM categoria c WHERE c.nombre = v.nombre);

INSERT INTO cancha(id_categoria,nombre,caracteristicas,precio_hora,estado)
SELECT (SELECT id_categoria FROM categoria WHERE nombre = v.categoria),v.nombre,v.caracteristicas,v.precio,'ACTIVA'
FROM (VALUES
 ('Fútbol en grass','Grass Central','Grass sintético, iluminación y acceso a vestuarios',50),
 ('Fútbol en grass','Grass Norte','Espacio para tu partido con amigos',60),
 ('Vóley','Vóley Azul','Superficie deportiva y red de vóley',35),
 ('Vóley','Vóley Verde','Cancha exterior para encuentros recreativos',40)
) AS v(categoria,nombre,caracteristicas,precio)
WHERE NOT EXISTS (SELECT 1 FROM cancha);

INSERT INTO reserva(id_usuario,id_cancha,fecha,hora_inicio,hora_fin,importe,estado,fecha_registro)
SELECT (SELECT id_usuario FROM usuario WHERE correo = 'cliente@canchaya.pe'),
 (SELECT id_cancha FROM cancha WHERE nombre = CASE MOD(X,4) WHEN 0 THEN 'Grass Central' WHEN 1 THEN 'Grass Norte' WHEN 2 THEN 'Vóley Azul' ELSE 'Vóley Verde' END),
 DATEADD('DAY',-X,CURRENT_DATE),TIME '10:00:00',TIME '11:00:00',
 CASE MOD(X,4) WHEN 0 THEN 50 WHEN 1 THEN 60 WHEN 2 THEN 35 ELSE 40 END,
 CASE WHEN MOD(X,7)=0 THEN 'CANCELADA' ELSE 'CONFIRMADA' END,DATEADD('DAY',-X,CURRENT_TIMESTAMP)
FROM SYSTEM_RANGE(1,28)
WHERE NOT EXISTS (SELECT 1 FROM reserva)
  AND (SELECT COUNT(*) FROM cancha WHERE nombre IN ('Grass Central','Grass Norte','Vóley Azul','Vóley Verde')) = 4;

INSERT INTO reserva(id_usuario,id_cancha,fecha,hora_inicio,hora_fin,importe,estado,fecha_registro)
SELECT (SELECT id_usuario FROM usuario WHERE correo = v.correo),(SELECT id_cancha FROM cancha WHERE nombre = v.cancha),
 DATEADD('DAY',v.dias,CURRENT_DATE),v.inicio,v.fin,v.importe,'CONFIRMADA',CURRENT_TIMESTAMP
FROM (VALUES
 ('ana@canchaya.pe','Grass Central',0,TIME '20:00:00',TIME '22:00:00',100),
 ('cliente@canchaya.pe','Vóley Azul',0,TIME '21:00:00',TIME '22:00:00',35),
 ('cliente@canchaya.pe','Grass Central',3,TIME '18:00:00',TIME '19:00:00',50),
 ('ana@canchaya.pe','Grass Norte',5,TIME '19:00:00',TIME '21:00:00',120)
) AS v(correo,cancha,dias,inicio,fin,importe)
WHERE (SELECT COUNT(*) FROM reserva) = 28 AND NOT EXISTS (SELECT 1 FROM pago)
  AND (SELECT COUNT(*) FROM cancha WHERE nombre IN ('Grass Central','Grass Norte','Vóley Azul','Vóley Verde')) = 4;

INSERT INTO pago(id_reserva,importe,estado,fecha_pago)
SELECT r.id_reserva,r.importe,'REGISTRADO',r.fecha_registro FROM reserva r
JOIN usuario u ON u.id_usuario = r.id_usuario AND u.correo IN ('cliente@canchaya.pe','ana@canchaya.pe')
WHERE NOT EXISTS (SELECT 1 FROM pago p WHERE p.id_reserva = r.id_reserva);

INSERT INTO devolucion(id_pago,importe,motivo,id_usuario,fecha_devolucion)
SELECT p.id_pago,p.importe,'Cancelación de demostración (simulada)',r.id_usuario,DATEADD('HOUR',1,p.fecha_pago)
FROM pago p JOIN reserva r ON r.id_reserva = p.id_reserva
JOIN usuario u ON u.id_usuario = r.id_usuario AND u.correo IN ('cliente@canchaya.pe','ana@canchaya.pe')
WHERE r.estado = 'CANCELADA' AND NOT EXISTS (SELECT 1 FROM devolucion d WHERE d.id_pago = p.id_pago);

INSERT INTO bloqueo_cancha(id_cancha,fecha,hora_inicio,hora_fin,motivo,estado,id_administrador)
SELECT (SELECT id_cancha FROM cancha WHERE nombre = 'Grass Norte'),DATEADD('DAY',2,CURRENT_DATE),TIME '08:00:00',TIME '10:00:00',
 'Cambio de redes y nivelación del grass','ACTIVO',(SELECT MIN(id_usuario) FROM usuario WHERE rol = 'ADMINISTRADOR')
WHERE NOT EXISTS (SELECT 1 FROM bloqueo_cancha) AND EXISTS (SELECT 1 FROM cancha WHERE nombre = 'Grass Norte');
