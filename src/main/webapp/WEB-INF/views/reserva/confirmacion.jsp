<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Confirmación de Reserva</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f0fdf4; display: flex; justify-content: center; align-items: center; min-height: 80vh; margin: 0; }
        .card-confirmacion { background: white; padding: 2.5rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.08); max-width: 480px; width: 100%; text-align: center; }
        .icono { font-size: 3rem; color: #16a34a; margin-bottom: 1rem; }
        .detalles { text-align: left; background: #f8fafc; padding: 1.2rem; border-radius: 6px; margin: 1.5rem 0; font-size: 0.95rem; }
        .detalles p { margin: 0.4rem 0; }
        .btn-link { display: inline-block; background: #2563eb; color: white; padding: 0.65rem 1.2rem; text-decoration: none; border-radius: 4px; font-weight: bold; }
    </style>
</head>
<body>
    <div class="card-confirmacion">
        <div class="icono">✓</div>
        <h2>¡Reserva Confirmada!</h2>
        <p>Tu turno y comprobante de pago han sido registrados exitosamente.</p>

        <div class="detalles">
            <p><strong>N° de Turno:</strong> #${reserva.idReserva}</p>
            <p><strong>Cliente:</strong> ${reserva.usuario.nombres} ${reserva.usuario.apellidos}</p>
            <p><strong>Cancha:</strong> ${reserva.cancha.nombre}</p>
            <p><strong>Fecha:</strong> ${reserva.fecha}</p>
            <p><strong>Horario:</strong> ${reserva.horaInicio} a ${reserva.horaFin}</p>
            <p><strong>Importe Pagado:</strong> S/ ${reserva.importe}</p>
            <p><strong>Estado:</strong> ${reserva.estado}</p>
        </div>

        <a href="${pageContext.request.contextPath}/reservas/mis-reservas" class="btn-link">Ver Mis Reservas</a>
    </div>
</body>
</html>