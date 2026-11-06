# Demostración con datos ficticios

Iniciar desde la carpeta del proyecto en PowerShell:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo"
```

Abrir http://localhost:8086/. Detener la aplicación antes de iniciar otra instancia en el mismo puerto.

Este perfil carga 5 cuentas, 5 categorías, 6 canchas, 8 reservas y 8 pagos en una base H2 **en memoria y separada**. No modifica `data/canchas`. Al cerrar el proceso y volver a iniciarlo se restaura la demostración y las fechas se calculan nuevamente según America/Lima. El inicio normal, sin `demo`, conserva su comportamiento sin datos precargados.

## Cuentas

Contraseña de todas las cuentas ficticias: **`Demo2026!`**.

| Correo | Persona | Caso |
| --- | --- | --- |
| admin@demo.test | Valeria Rojas | Panel, filtros, detalle de cualquier reserva y cancelación administrativa. |
| camila@demo.test | Camila Torres | Reservas próximas, histórica y cancelada. |
| diego@demo.test | Diego Salazar | Segundo propietario para probar aislamiento y horarios ocupados. |
| lucia@demo.test | Lucía Mendoza | Cliente activo sin reservas: lista vacía y nueva compra. |
| bloqueado@demo.test | Marco Vargas | Inicio de sesión rechazado por bloqueo. |

## Recorrido sugerido

1. Entrar como Camila. Mostrar su lista y los enlaces de filtro. La reserva **1**, Arena Central dentro de tres días, permite cancelar desde Acciones. El detalle muestra el pago y no contiene un formulario de cancelación.
2. Mostrar la reserva **2**, Arena Techada en la próxima medianoche: faltan menos de 24 horas, así que el cliente no puede cancelarla. La reserva **4** es histórica y tampoco permite cancelación del cliente.
3. Mostrar la reserva **3** cancelada: conserva el pago; su horario de Pádel dentro de cuatro días, de 10:00 a 11:30, está libre para otra reserva.
4. Usar **Abrir otra sesión** y entrar como Diego. Abrir el detalle de Camila por su identificador: debe rechazar el acceso. Cerrar esta sesión no debe cerrar la de Camila. No copiar URLs de sesión entre cuentas.
5. Entrar como Lucía. Mostrar lista vacía y crear una reserva. Para demostrar un choque horario, elegir Arena Central dentro de tres días, de 19:00 a 20:00: está ocupada por la reserva **5** de Diego. Usar de 20:00 a 21:00 para un pago simulado exitoso.
6. Entrar como administrador en otra sesión. Filtrar por Camila, cancha, estado o fechas; abrir detalles y cancelar la reserva **2** mediante la acción administrativa. Mostrar que el pago permanece registrado.
7. Mostrar Arena Sur inactiva en gestión: no aparece entre canchas disponibles. Su reserva histórica **8** conserva los datos. Mostrar la categoría Vóley inactiva.
8. Probar el usuario bloqueado, las páginas públicas Publicidad y Contacto y una ruta inexistente para mostrar el 404.

Las reservas ya existentes están pagadas. El pago nuevo sigue siendo una simulación local; no hay cobros reales. Para repetir el recorrido desde cero, detener completamente la aplicación y volver a ejecutar el comando del perfil `demo`.
