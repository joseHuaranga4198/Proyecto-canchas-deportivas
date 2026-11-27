# Demostración con datos ficticios

Iniciar desde la carpeta del proyecto en PowerShell:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo"
```

Abrir http://localhost:8086/. Los datos se cargan en una base H2 en memoria y se reinician al detener la aplicación.

| Correo | Contraseña | Caso |
| --- | --- | --- |
| admin@canchas.pe | admin123 | Administrador: panel, gestión, mantenimiento, pagos y métricas. |
| cliente@canchaya.pe | cliente123 | Cliente con historial, una reserva hoy y otra dentro de tres días (cancelable). |
| ana@canchaya.pe | cliente123 | Segunda clienta, para probar el choque de horarios (RN03). |
| luis@canchaya.pe | cliente123 | Cliente SUSPENDIDO: el inicio de sesión se rechaza (RN01). |

Recorrido sugerido (reglas de negocio):

1. RN01: entrar como luis@canchaya.pe y ver el mensaje de cuenta suspendida.
2. RN02: como cliente, cancelar la reserva de dentro de tres días (se crea la devolución y el turno se libera); la reserva de hoy no muestra Cancelar.
3. RN03: reservar una cancha y luego intentar el mismo horario con Ana; un turno contiguo sí se acepta.
4. RN04: como administrador, desactivar la categoría Vóley y revisar el catálogo; las reservas de Vóley se conservan.
5. RN05: programar un mantenimiento sobre un horario reservado (se rechaza) y sobre uno libre (se acepta).
6. Métricas: abrir /admin/metricas y revisar las 5 series de tiempo y los 5 indicadores.
