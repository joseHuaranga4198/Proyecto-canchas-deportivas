<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Generar Reserva - ${cancha.nombre}</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 2rem 1rem; }
        .form-card { max-width: 500px; margin: auto; background: #fff; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); }
        .form-card h2 { margin-top: 0; color: #1e293b; }
        .detalle-cancha { background: #f8fafc; padding: 1rem; border-radius: 6px; margin-bottom: 1.5rem; border: 1px solid #e2e8f0; }
        .campo { margin-bottom: 1.2rem; }
        .campo label { display: block; font-weight: bold; margin-bottom: 0.4rem; color: #334155; }
        .campo input, .campo select { width: 100%; padding: 0.6rem; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box; }
        .alert-error { background-color: #fee2e2; color: #991b1b; padding: 0.75rem; border-radius: 4px; margin-bottom: 1.2rem; border: 1px solid #f87171; }
        .btn-confirmar { width: 100%; background: #16a34a; color: white; padding: 0.75rem; border: none; border-radius: 4px; font-weight: bold; font-size: 1rem; cursor: pointer; }
        .btn-confirmar:hover { background: #15803d; }
        .btn-cancelar { display: block; text-align: center; margin-top: 1rem; color: #64748b; text-decoration: none; }
    </style>
</head>
<body>

<div class="form-card">
    <h2>Reservar Espacio Deportivo</h2>

    <div class="detalle-cancha">
        <strong>${cancha.nombre}</strong><br>
        <span style="color: #64748b;">Tarifa:</span> S/ ${cancha.precioHora} por hora
    </div>

    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/reservas/guardar" method="post">
        <input type="hidden" name="canchaId" value="${cancha.idCancha}">

        <div class="campo">
            <label for="fecha">Fecha de Reserva:</label>
            <input type="date" id="fecha" name="fecha" min="${fechaMinima}" required>
        </div>

        <div class="campo">
            <label for="horaInicio">Hora de Inicio:</label>
            <select id="horaInicio" name="horaInicio" required>
                <option value="08:00">08:00 AM</option>
                <option value="09:00">09:00 AM</option>
                <option value="10:00">10:00 AM</option>
                <option value="11:00">11:00 AM</option>
                <option value="12:00">12:00 PM</option>
                <option value="13:00">01:00 PM</option>
                <option value="14:00">02:00 PM</option>
                <option value="15:00">03:00 PM</option>
                <option value="16:00">04:00 PM</option>
                <option value="17:00">05:00 PM</option>
                <option value="18:00">06:00 PM</option>
                <option value="19:00">07:00 PM</option>
                <option value="20:00">08:00 PM</option>
                <option value="21:00">09:00 PM</option>
                <option value="22:00">10:00 PM</option>
            </select>
        </div>

        <div class="campo">
            <label for="horaFin">Hora de Fin:</label>
            <select id="horaFin" name="horaFin" required>
                <option value="09:00">09:00 AM</option>
                <option value="10:00">10:00 AM</option>
                <option value="11:00">11:00 AM</option>
                <option value="12:00">12:00 PM</option>
                <option value="13:00">01:00 PM</option>
                <option value="14:00">02:00 PM</option>
                <option value="15:00">03:00 PM</option>
                <option value="16:00">04:00 PM</option>
                <option value="17:00">05:00 PM</option>
                <option value="18:00">06:00 PM</option>
                <option value="19:00">07:00 PM</option>
                <option value="20:00">08:00 PM</option>
                <option value="21:00">09:00 PM</option>
                <option value="22:00">10:00 PM</option>
                <option value="23:00">11:00 PM</option>
            </select>
        </div>

        <button type="submit" class="btn-confirmar">Continuar al Pago</button>
        <a href="${pageContext.request.contextPath}/canchas/catalogo" class="btn-cancelar">Cancelar y regresar</a>
    </form>
</div>

</body>
</html>