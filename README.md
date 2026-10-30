# Sistema de reserva de canchas deportivas

## Rama `woshtsu`: F13–F16

| Código | Función |
| --- | --- |
| F13 | El cliente consulta sus reservas. |
| F14 | El cliente ve el detalle y el pago de una reserva propia. |
| F15 | El cliente cancela dentro del plazo permitido. |
| F16 | El administrador busca reservas y puede gestionar su cancelación administrativa. |

## Arquitectura

La aplicación usa Spring MVC, JSP/JSTL y JDBC con `JdbcTemplate`. Los controladores reciben las rutas, los servicios aplican reglas y los DAO ejecutan SQL. `RowMapper` convierte filas en POJO; las relaciones se obtienen por llaves foráneas y consultas `JOIN`. Las escrituras de una reserva y su pago se coordinan con `@Transactional`.

Las vistas de reserva F13–F16 están en `src/main/webapp/WEB-INF/views/f1316/`. Las reglas del cliente permiten cancelar con 24 horas exactas o más antes del inicio; una reserva cancelada conserva su pago en el historial. La cancelación no significa que se haya devuelto dinero. El pago mostrado por esta aplicación es una simulación local, sin pasarela.

## Arranque sin datos de demostración

La aplicación inicia con las tablas vacías. `src/main/resources/schema.sql` usa `CREATE TABLE IF NOT EXISTS` y no borra tablas ni filas. La base local H2 se guarda en `./data/canchas` y persiste después de cerrar la aplicación. El directorio `/data/` está ignorado por Git porque contiene datos locales.

Desde PowerShell, en esta carpeta:

```powershell
.\mvnw.cmd spring-boot:run
```

Abre `http://localhost:8086/`. El inicio público ofrece registro de cliente, login y navegación informativa. Clientes autenticados van al catálogo; administradores van al panel. Para comenzar con el sistema vacío, registra un cliente y luego carga categorías y canchas desde el panel administrativo.

## Configurar el primer administrador

El primer administrador se crea solo si aún no existe ninguno y se configuran todas estas variables de entorno antes de iniciar la aplicación:

- `ADMIN_CORREO`
- `ADMIN_CLAVE`
- `ADMIN_DOCUMENTO`
- `ADMIN_NOMBRES`
- `ADMIN_APELLIDOS`

En PowerShell se pueden solicitar los valores al iniciar la sesión, evitando guardarlos en el repositorio:

```powershell
$env:ADMIN_CORREO = Read-Host "Correo del administrador"
$env:ADMIN_CLAVE = Read-Host "Clave del administrador"
$env:ADMIN_DOCUMENTO = Read-Host "Documento del administrador"
$env:ADMIN_NOMBRES = Read-Host "Nombres del administrador"
$env:ADMIN_APELLIDOS = Read-Host "Apellidos del administrador"
.\mvnw.cmd spring-boot:run
```

Si falta una variable, la aplicación avisa que todavía no hay administrador y no inventa una identidad o clave. Puedes definir las variables y reiniciar la aplicación sin registrar primero otro usuario. La configuración inicial no cambia contraseñas ni reemplaza administradores existentes.

## Rutas principales

| Ruta | Uso |
| --- | --- |
| `/` | Inicio público; al entrar con sesión activa, redirige al panel o catálogo según rol. |
| `/publicidad`, `/contacto` | Información pública. |
| `/usuario/registro`, `/usuario/login`, `/usuario/logout` | Crear cuenta de cliente, iniciar y cerrar sesión. |
| `/canchas/catalogo` | Catálogo público y de cliente. |
| `/reservas/disponibilidad`, `/reservas/nueva` | Consultar horarios y comenzar una reserva. |
| `/reservas/pago` | Revisar y confirmar el pago simulado. |
| `/f1316/reservas`, `/f1316/reservas/{id}` | Historial y detalle del cliente. |
| `/f1316/admin/reservas` | Consulta administrativa con filtros. |
| `/f1316/admin/reservas/{id}` | Detalle administrativo de una reserva. |
| `/admin/panel` | Panel principal del administrador. |

Las rutas administrativas requieren rol `ADMINISTRADOR`. Las funciones del cliente usan la identidad de su sesión para limitar las reservas consultables o cancelables.

## Sesiones y varias pestañas

Las sesiones se rastrean mediante la URL. Al abrir una sesión nueva, se obtiene una URL limpia que genera una sesión distinta. Duplicar una pestaña o copiar una URL ya abierta conserva su identificador de sesión; no compartas ese enlace con otra persona porque le permitiría usar esa sesión. La navegación incluye “Abrir otra sesión” para iniciar una nueva sesión con URL limpia. Las respuestas usan cabeceras para evitar caché y envío del referrer.

## Pruebas y fixtures

Los datos de demostración están en `src/test/resources/test-data.sql`; no se cargan al ejecutar la aplicación normal. Las pruebas activan el perfil `test`, usan H2 en memoria y cargan `test-schema.sql` más los fixtures. Ese esquema reinicia tablas solo en el contexto de pruebas para aislar los casos. No copies fixtures de prueba a `src/main/resources`.

## Documentación

La guía de aprendizaje, el guion de exposición y la lista de pruebas manuales están en `../documentacion-woshtsu`. Empieza por [GUIA_APRENDIZAJE.md](../documentacion-woshtsu/GUIA_APRENDIZAJE.md).

## Integración con las otras responsabilidades

Antes del merge, coordina con el equipo:

| Responsabilidad | Contrato a conservar |
| --- | --- |
| Clientes | Sesión `usuarioLogueado`, ID y roles. Las operaciones de cliente usan el ID de su sesión. |
| Canchas | Nombres de columna y firmas DAO; conservar canchas referenciadas por reservas antiguas. |
| F9–F12 | Firmas DAO/Service, verificación de horario y flujo transaccional de pago + reserva. `ReservaService.registrarReserva` rechaza reservas sin pago; `confirmarPago` devuelve `false`; usar `ReservaF1316Service.registrarReservaPagada`. |
| F17–F20 | Acordar cancelación administrativa y devolución. La cancelación del usuario conserva el pago pero no registra reembolso. |
| Categorías | Mantener llaves foráneas y consultas SQL que enlazan categoría con cancha. |

Cualquier cambio compartido en `schema.sql`, firmas DAO o nombres de columna debe revisarse con los responsables antes de integrarse.


## Verificaciones

El agente de backend reportó 17 pruebas automatizadas aprobadas. La comprobación HTTP de los cambios de navegación y sesión está pendiente.
