# Sistema de reserva de canchas deportivas

## Rama `woshtsu`: responsabilidad 4 (F13–F16)

Esta rama cubre las siguientes funcionalidades:

| Código | Funcionalidad | Alcance |
| --- | --- | --- |
| F13 | Consultar reservas del cliente | Mostrar las reservas del cliente autenticado. |
| F14 | Consultar detalle y estado | Mostrar cancha, fecha, horario, importe y pago de una reserva propia. |
| F15 | Cancelar reserva | Verificar propietario y límite; conservar el historial. |
| F16 | Consultar reservas como administrador | Buscar y filtrar reservas. |

## Arquitectura actual

La aplicación utiliza Java, Spring MVC, JSP, JSTL y JDBC con `JdbcTemplate`. El recorrido principal es:

`Controller → Service → DAO/Repository → JdbcTemplate/SQL → base de datos`

Los objetos Java son POJOs sin mapeo JPA. Las relaciones se representan con llaves foráneas en SQL y consultas `JOIN`; `RowMapper` convierte cada fila en objetos Java. El servicio agrupa las escrituras de reserva y pago con `@Transactional`.

Las vistas F13–F16 están en `src/main/webapp/WEB-INF/views/f1316/`. Otros módulos mantienen sus páginas JSP en `WEB-INF/views/`.

## Reglas de negocio

1. Una reserva registrada requiere un pago guardado correctamente. Antes del pago, la selección es un borrador temporal en la sesión.
2. El cliente puede cancelar si faltan exactamente 24 horas o más para el inicio; con menos de 24 horas se rechaza.
3. Cancelar conserva la reserva y el pago para mantener el historial.
4. La cancelación no confirma que el dinero haya sido devuelto. Las devoluciones corresponden al proceso administrativo acordado con el responsable de F17–F20.
5. El pago de esta demostración es simulado: registra datos localmente, sin pasarela bancaria ni cobro de tarjeta.

## Flujo de pago

- `POST /reservas/guardar` guarda un borrador de cancha, fecha y horario en la sesión.
- `GET /reservas/pago` muestra el resumen antes de registrar la reserva.
- `POST /reservas/pago/procesar` verifica de nuevo disponibilidad y registra reserva y pago en una transacción.
- Las rutas de pago reciben el borrador de la sesión; ya no reciben un ID de reserva porque aún no existe una fila antes del pago.
- Para integrar F9–F12, usar `ReservaF1316Service.registrarReservaPagada`. `ReservaService.registrarReserva` queda obsoleto y rechaza crear reservas sin pago; `confirmarPago` devuelve `false`.

## Ejecución local

Desde esta carpeta, ejecutar `.\mvnw.cmd spring-boot:run` y abrir `http://localhost:8086/usuario/login`. La configuración local usa H2 en memoria y carga el esquema y los datos de ejemplo al iniciar. Los cambios se pierden al detener la aplicación.

## Verificaciones

El backend reportó 11 pruebas automatizadas sin errores ni fallos. También se verificó por HTTP el inicio de sesión, la lista y el detalle JSP, CSS, borrador, pago simulado, cancelación con estado `CANCELADA`, filtros administrativos y validación de rango de fechas.

## Coordinación con los compañeros

| Responsabilidad | Acuerdos para integrar |
| --- | --- |
| 1. Clientes | Mantener compatibles el usuario en sesión, ID y roles. Las operaciones del cliente toman el ID de esa sesión. |
| 2. Canchas | Mantener los nombres de columnas y contratos DAO usados para buscar la cancha; conservar registros históricos. |
| F9–F12 | Acordar firmas DAO/Service, disponibilidad, estados y el flujo que registra pago y reserva juntos. Una reserva cancelada deja libre el horario. |
| F17–F20 | Coordinar consulta administrativa con cancelaciones excepcionales y devoluciones; la cancelación del cliente no registra un reembolso. |
| Categorías | Mantener compatibles las llaves foráneas y consultas que conectan categoría con cancha. |

Los cambios de esquema, POJO y firmas DAO deben coordinarse antes de integrar. La guía de aprendizaje, el guion de exposición y los escenarios manuales están en `../documentacion-woshtsu`; empieza por [GUIA_APRENDIZAJE.md](../documentacion-woshtsu/GUIA_APRENDIZAJE.md).


