<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Pago y Confirmación de Reserva</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }
        .pago-card { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); max-width: 440px; width: 100%; }
        .resumen { background: #f8fafc; border: 1px solid #e2e8f0; padding: 1rem; border-radius: 6px; margin: 1.2rem 0; font-size: 0.9rem; }
        .monto { font-size: 1.5rem; font-weight: bold; color: #16a34a; text-align: center; margin: 1rem 0; }
        .btn-pagar { width: 100%; background: #16a34a; color: white; border: none; padding: 0.8rem; border-radius: 4px; font-weight: bold; font-size: 1rem; cursor: pointer; }
        .btn-pagar:hover { background: #15803d; }
    </style>
</head>
<body>
    <div class="pago-card">
        <h2>Confirmación y Pago (I06)</h2>
        <p style="color: #64748b; font-size: 0.9rem;">Verifica los datos del turno antes de confirmar la reserva.</p>

        <div class="resumen">
            <p><strong>Cancha:</strong> ${reserva.canchaNombre}</p>
            <p><strong>Fecha:</strong> ${reserva.fecha}</p>
            <p><strong>Horario:</strong> ${reserva.horaInicio} - ${reserva.horaFin}</p>
            <p><strong>Cliente:</strong> ${reserva.clienteNombre}</p>
        </div>

        <div class="monto">Total a Pagar: S/ ${reserva.montoTotal}</div>

        <form action="${pageContext.request.contextPath}/reservas/pago/procesar/${reserva.id}" method="post">
            <button type="submit" class="btn-pagar">Completar Reserva y Pagar</button>
        </form>
    </div>
</body>
</html>