<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Pagar Reserva #${reserva.idReserva}</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f8fafc; padding: 2rem 1rem; }
        .checkout-box { max-width: 480px; margin: auto; background: #fff; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.08); }
        .checkout-box h2 { margin-top: 0; color: #0f172a; }
        .resumen { background: #f1f5f9; border-radius: 6px; padding: 1rem; margin-bottom: 1.5rem; }
        .item-row { display: flex; justify-content: space-between; margin-bottom: 0.5rem; font-size: 0.95rem; }
        .total-row { display: flex; justify-content: space-between; font-weight: bold; font-size: 1.25rem; border-top: 1px solid #cbd5e1; padding-top: 0.75rem; color: #16a34a; }
        .btn-pagar { width: 100%; background: #0284c7; color: white; border: none; padding: 0.8rem; border-radius: 6px; font-weight: bold; font-size: 1rem; cursor: pointer; }
        .btn-pagar:hover { background: #0369a1; }
    </style>
</head>
<body>

<div class="checkout-box">
    <h2>Confirmación de Pago</h2>
    <p style="color: #64748b; font-size: 0.9rem;">Tu turno se encuentra temporalmente retenido como <strong>${reserva.estado}</strong>.</p>

    <div class="resumen">
        <div class="item-row"><span>Cancha:</span><strong>${reserva.cancha.nombre}</strong></div>
        <div class="item-row"><span>Fecha:</span><strong>${reserva.fecha}</strong></div>
        <div class="item-row"><span>Horario:</span><strong>${reserva.horaInicio} - ${reserva.horaFin}</strong></div>
        <div class="total-row"><span>Total a Pagar:</span><span>S/ ${reserva.importe}</span></div>
    </div>

    <form action="${pageContext.request.contextPath}/reservas/pago/procesar/${reserva.idReserva}" method="post">
        <button type="submit" class="btn-pagar">Simular Pago Exitoso</button>
    </form>
</div>

</body>
</html>