# Sistema de reserva de canchas deportivas

## Rama woshtsu: responsabilidad 4 (F13–F16)

Esta rama parte de `main` y corresponde al desarrollo de las siguientes funcionalidades:

| Código | Funcionalidad | Alcance |
| --- | --- | --- |
| F13 | Consultar reservas del cliente | Mostrar únicamente las reservas del cliente que inició sesión. |
| F14 | Consultar detalle y estado de reserva | Mostrar cancha, fecha, horario, duración, importe y datos del pago de una reserva propia. |
| F15 | Cancelar reserva | Validar propiedad y plazo; cambiar el estado conservando el historial. |
| F16 | Consultar reservas administrativamente | Permitir al administrador consultar y filtrar las reservas. |

F13–F16 están implementadas con Java, Spring MVC, JPA y Thymeleaf. Los controladores atienden las rutas `/f1316/...`, los servicios aplican las reglas de negocio y los repositorios consultan las entidades. Las pantallas se encuentran en `src/main/resources/templates/f1316/`.

## Acuerdos de negocio que afectan a otras responsabilidades

1. **Una reserva existe únicamente si ya se registró su pago correctamente.** Si no se paga, no existe reserva. Una selección de cancha y horario previa al pago no debe presentarse como una reserva existente.
2. **El cliente puede cancelar hasta 24 horas antes del inicio.** Se permite cancelar cuando faltan exactamente 24 horas o más; con menos de 24 horas se rechaza. El cálculo considera la fecha y hora completas del inicio.
3. La cancelación conserva la reserva y el registro de pago para mantener el historial; no elimina físicamente esos registros.
4. **Cancelar una reserva no significa que el dinero ya haya sido devuelto.** El tratamiento administrativo de la devolución corresponde a F17. La política concreta de devolución aún debe acordarse con su responsable.

### Flujo de pago

La selección previa al pago se conserva como borrador en la sesión. Al confirmar el pago, el servicio registra reserva y pago en una transacción. Así, solo existe una reserva registrada cuando el pago se guarda correctamente.

La versión inicial del documento y del código contemplaba reservas `PENDIENTE` antes del pago. Esta rama corrige esa diferencia según el acuerdo del equipo comunicado por el responsable de esta rama.

### Cambios que debe revisar el responsable de F9–F12

- `POST /reservas/guardar` prepara un `ReservaBorrador` en sesión; no inserta una reserva.
- `GET /reservas/pago` muestra el resumen y `POST /reservas/pago/procesar` registra el pago simulado y la reserva. Las antiguas rutas de pago con identificador se sustituyen por estas rutas sin identificador.
- `ReservaF1316Service.registrarReservaPagada` realiza ambas escrituras en una transacción, vuelve a comprobar disponibilidad y bloquea la cancha durante esa operación para evitar reservas simultáneas superpuestas.
- El método antiguo `ReservaService.registrarReserva` rechaza su uso para impedir reservas sin pago; `confirmarPago` devuelve `false`. Los compañeros deben migrar sus llamadas al flujo acordado antes del merge.
- Las entidades compartidas incorporan anotaciones JPA; los DAO JDBC del resto del proyecto se conservan. La migración completa de otras responsabilidades no está incluida.

## Ejecución y comprobaciones

Desde esta carpeta, ejecutar `./mvnw.cmd spring-boot:run` y abrir `http://localhost:8086/usuario/login`. Para ejecutar las pruebas: `./mvnw.cmd test`.

Se verificaron **11 pruebas automatizadas sin fallos**, incluyendo el límite exacto de cancelación, acceso a reservas propias, filtros, pago y reserva conjuntos, reversión ante un fallo al guardar el pago y liberación de horarios tras cancelar. También se comprobó por HTTP real el inicio de sesión, las vistas, el CSS y el recorrido borrador → pago simulado → detalle → cancelación.

## Coordinación con los compañeros

| Responsabilidad | Qué necesita F13–F16 | Qué debemos acordar antes del merge |
| --- | --- | --- |
| 1. Clientes | Identidad del usuario autenticado, su rol y su relación con las reservas. | Mantener nombres y tipos compatibles para el identificador, roles y sesión. Cada consulta o cancelación del cliente debe comprobar que la reserva le pertenece. |
| 2. Canchas | Cancha asociada a cada reserva y sus datos para mostrar el detalle. | Conservar las relaciones y el historial aunque la cancha se inactive. Coordinar cualquier cambio de nombres o tipos en la entidad. |
| 3. F9–F12: disponibilidad, reserva y pago | Reservas pagadas, sus horarios, importes y datos del pago. | Registrar reserva y pago de forma conjunta y consistente. Acordar entidades JPA, estados y métodos compartidos. Las reservas canceladas deben dejar de bloquear el horario. |
| 5. F17–F20 | Compatibilidad entre la consulta administrativa F16 y la cancelación administrativa F17. | F15 aplica el plazo del cliente; las excepciones administrativas y devoluciones pertenecen a F17. Acordar cómo registrar y mostrar una devolución sin afirmar que ocurrió por el solo hecho de cancelar. |
| 6. Categorías | Compatibilidad con la relación entre categoría y cancha. | Coordinar cambios en entidades compartidas que afecten la carga o visualización de canchas. |

Estos puntos son una lista de coordinación; no implican que los compañeros ya hayan confirmado los contratos técnicos ni que hayan sido contactados.

## Integración técnica pendiente de acordar

- F13–F16 usan **JPA y Thymeleaf**. El repositorio todavía contiene pantallas y DAOs JSP/JDBC de otras funciones; al integrar ramas, coordinar las entidades compartidas y su configuración.
- Evitar crear versiones distintas de las mismas entidades `Usuario`, `Cancha`, `Reserva` y pago. Acordar sus relaciones, identificadores y campos antes de integrar.
- Supabase es una propuesta todavía no aprobada por el grupo. No es un requisito de esta rama. Se puede trabajar inicialmente con H2 y JPA y acordar posteriormente la configuración de PostgreSQL.
- Acordar una misma zona horaria para calcular el límite de cancelación y mostrar los horarios.
- Los cambios en archivos compartidos, especialmente servicios de reservas, entidades, configuración y dependencias, deben explicarse en la solicitud de integración.

## Flujo de trabajo y documentación

El desarrollo se realizará en `woshtsu`. Al finalizar, se presentarán los cambios para revisión; el responsable del repositorio realizará la revisión y el merge a `main` conforme al reparto del grupo.

La guía de aprendizaje, el guion de exposición y las pruebas manuales están fuera del repositorio, en `../documentacion-woshtsu`. Empieza por [GUIA_APRENDIZAJE.md](../documentacion-woshtsu/GUIA_APRENDIZAJE.md). El pago es una simulación local; no se conecta con una pasarela. H2 está en memoria y se reinicia con la aplicación. Las pantallas JSP/DAO de otras funciones todavía coexisten con Thymeleaf/JPA.




