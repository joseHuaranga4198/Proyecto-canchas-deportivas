# CanchaYa — Sistema de reserva de canchas deportivas

Aplicación académica de autoservicio con Java, Spring Boot 3.5, Spring MVC, JSP/JSTL, Spring JDBC (`JdbcTemplate`, `RowMapper`) y H2. Chart.js 4.5.1 dibuja cinco gráficos a partir de consultas JDBC. No utiliza JPA, Thymeleaf, JSF ni MySQL.

## Ejecutar

Requiere JDK 17 o superior; verificado con JDK 21. Desde esta carpeta, en PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Abre [CanchaYa](http://localhost:8086/). El primer uso de Maven requiere conexión para descargar dependencias.

También puedes construir y ejecutar el WAR:

```powershell
.\mvnw.cmd package
java -jar target/demo-0.0.1-SNAPSHOT.war
```

El modo normal conserva sus datos en `./data/canchas.mv.db`. No carga clientes, canchas ni reservas de demostración. `schema.sql` crea las tablas e índices faltantes sin borrar registros. Realiza una copia de `data/` con el sistema apagado antes de trasladar una instalación.

## Acceso administrativo fijo

- Correo: **admin@canchas.pe**
- Contraseña: **admin123**

La cuenta académica se crea automáticamente si no existe ningún administrador. No se reemplazan administradores existentes. La clave fija permanece en el código de inicialización por requisito del proyecto; en H2 se almacena su hash PBKDF2 con sal. Las claves antiguas en texto se migran sin cambiar las credenciales de acceso. El registro público siempre crea clientes.

## Demostración con datos

Para explorar los cinco gráficos y los flujos con datos ficticios en una base **separada en memoria**:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo"
# O con el WAR:
java -jar target/demo-0.0.1-SNAPSHOT.war --spring.profiles.active=demo
```

El perfil `demo` ofrece cuatro canchas de fútbol en grass y vóley, 28 reservas históricas, pagos y cuatro devoluciones. Se reinicia al detener la aplicación y nunca usa la base local de trabajo.

- Administrador: **admin@canchas.pe / admin123**
- Cliente de demostración: **cliente@canchaya.pe / cliente123**

Usa un solo proceso por puerto. Si otro proceso usa 8086, detén ese proceso o añade `--server.port=8087` al WAR.

## Reglas del sistema

- Las reservas se hacen en horas exactas y bloques mínimos de una hora. Mantenimiento permite elegir los minutos de inicio y fin. Ambos se programan de 08:00 a 22:00 con inicio futuro en America/Lima.
- Un borrador no bloquea el horario ni se guarda como reserva. Al confirmar el pago simulado, reserva y pago se registran juntos dentro de una transacción.
- Cliente y cancha deben estar activos; la categoría debe estar activa. Reservas y mantenimiento no pueden superponerse. Se bloquea la fila de la cancha para serializar operaciones concurrentes.
- El cliente cancela con al menos 24 horas de anticipación. Administración puede gestionar cancelaciones excepcionales de reservas confirmadas.
- Cancelar registra la devolución completa simulada dentro de la misma transacción. El pago original permanece registrado. Una restricción única impide devolverlo dos veces.
- Las cancelaciones históricas sin devolución pueden regularizarse desde Pagos.
- Inactivar una cancha o bloquear un cliente conserva su historial. No se elimina una categoría con canchas vinculadas.
- Cada petición revalida en H2 el rol y estado de la sesión. Un bloqueo impide seguir usando una sesión abierta.

## Sesiones y navegación

“Iniciar sesión” autentica en la sesión actual. “Abrir otra sesión” abre una URL limpia en otra pestaña, sin identificador de sesión, para probar otra cuenta de forma independiente. Duplicar una pestaña o copiar su URL conserva la sesión. No compartas URLs autenticadas. Se conservó este mecanismo por el requisito de accesos independientes; hay expiración de 20 minutos, rotación del identificador al autenticar, respuestas sin caché y política sin referrer.

El logotipo **⚽ CanchaYa** se incluye desde un fragmento común. Su enlace `/` lleva al inicio público, al catálogo del cliente o al panel del administrador. Tras iniciar sesión, las páginas informativas se ocultan y sus rutas redirigen al área correspondiente. Cerrar sesión utiliza un formulario POST protegido con CSRF.

## Estructura del código

Los módulos con acceso a datos separan el modelo, el controlador, la interfaz `Service`, su implementación, la interfaz `DAO` y el repositorio JDBC. Los controladores usan las interfaces de servicio, y los servicios usan las interfaces DAO. Las validaciones de negocio se implementan en el servicio de cada módulo.

`reserva` reúne las funcionalidades F13 a F16 y el resto del flujo de reserva en un solo controlador. `operacion` reúne pagos, devoluciones y mantenimiento. Las JSP están agrupadas por módulo y comparten la cabecera, que muestra el nombre y el rol activos, y la hoja de estilos de botones.

## Rutas principales

| Ruta | Acceso |
| --- | --- |
| `/`, `/nosotros`, `/publicidad`, `/contacto` | Público; las informativas redirigen si hay autenticación |
| `/usuario/registro`, `/usuario/login` | Registro e ingreso |
| `/usuario/logout` (POST) | Cierre protegido con CSRF |
| `/canchas/catalogo`, `/reservas/disponibilidad` | Consulta pública |
| `/reservas/nueva`, `/reservas/pago` | Cliente |
| `/reservas/mis-reservas`, `/reservas/mis-reservas/{id}` | Historial y detalle propio |
| `/usuario/perfil` | Edición del perfil propio |
| `/admin/panel` | Administrador |
| `/canchas/admin`, `/admin/categorias`, `/usuario/admin/lista` | Gestión de catálogos y clientes |
| `/reservas/admin` | Consulta, detalle y cancelación administrativa |
| `/admin/disponibilidad` | Bloqueos temporales por mantenimiento |
| `/admin/pagos` | Pagos, devoluciones, filtros y regularización histórica |
| `/admin/metricas` | Cinco gráficos y sus tablas |

Todos los formularios POST incluyen un token CSRF vinculado a la sesión. Las rutas administrativas requieren ADMINISTRADOR; reserva e historial propio requieren CLIENTE. Los servicios validan nuevamente los datos aunque no se use el formulario HTML.

## Métricas

Tres líneas (reservas registradas/día, pagos brutos/día, horas confirmadas/día) y dos gráficos de barras (reservas confirmadas por cancha y por categoría). No hay acumulados progresivos. Cada gráfico explica su fecha y estados, incluye ejes, filtros y tabla de valores; los días sin movimientos valen cero. Los pagos brutos no representan ingreso neto: las devoluciones se consultan en Pagos. Las clasificaciones por cancha/categoría usan sus nombres y categoría actuales. Chart.js se sirve localmente, sin CDN en ejecución; licencia en `static/js/vendor/Chart.js-LICENSE.md`.

## Contacto y publicidad

Contacto muestra un formulario estático de demostración: no envía ni almacena mensajes. Las imágenes publicitarias fueron generadas con la herramienta integrada ImageGen para el negocio ficticio; no son fotografías de instalaciones reales. Los archivos y prompts están documentados en `documentacion/RECURSOS_VISUALES.md`.

## Pruebas y entrega

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

Las pruebas usan exclusivamente H2 en memoria. Incluyen validaciones, permisos, CSRF, migración de contraseñas, rollback, devolución única, cinco métricas, mantenimiento y concurrencia. Los scripts destructivos de pruebas no deben copiarse al perfil normal.

- `PLAN_DESARROLLO.md`: estado final del plan.
- `documentacion/INFORME_TECNICO_FINAL.md`: modelo, reglas y matriz F01–F24.
- `documentacion/PRUEBAS_MANUALES.md`: guion de demostración y verificación.
- `documentacion/prototipos/`: diseños anteriores conservados como referencia; no son vistas activas.

El alcance académico incluye pagos y devoluciones simulados, Contacto estático y operación local. No incorpora pasarela bancaria, envío de correos ni despliegue público.
