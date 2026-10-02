<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Generar Reserva</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 20px; }
        .reserva-card { max-width: 500px; margin: 2rem auto; background: #fff; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 1.2rem; }
        label { display: block; font-weight: bold; margin-bottom: 0.4rem; color: #333; }
        input[type="date"], select, input[type="time"] { width: 100%; padding: 0.6rem; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .alert-error { background-color: #fee2e2; color: #b91c1c; padding: 0.8rem; border-radius: 4px; margin-bottom: 1rem; border: 1px solid #f87171; }
        .info-box { background-color: #f0fdf4; border-left: 4px solid #22c55e; padding: 0.8rem; margin-bottom: 1.2rem; }
        .btn-submit { width: 100%; background: #16a34a; color: white; padding: 0.75rem; border: none; border-radius: 4px; font-size: 1rem; font-weight: bold; cursor: pointer; }
        .btn-submit:hover { background: #15803d; }
        .btn-volver { display: block; text-align: center; margin-top: 1rem; color: #666; text-decoration: none; }
    </style>
</head>
<body>
    <div class="reserva-card">
        <h2>Completar Reserva</h2>

        <div class="info-box">
            <strong>Cancha:</strong> ${cancha.nombre}<br>
            <strong>Tarifa:</strong> S/ ${cancha.precioHora} por hora
        </div>

        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/reservas/guardar" method="post">
            <input type="hidden" name="canchaId" value="${cancha.id}">

            <div class="form-group">
                <label for="fecha">Fecha de Reserva:</label>
                <input type="date" id="fecha" name="fecha" min="${fechaMinima}" required>
            </div>

            <div class="form-group">
                <label for="horaInicio">Hora de Inicio:</label>
                <input type="time" id="horaInicio" name="horaInicio" required step="3600">
            </div>

            <div class="form-group">
                <label for="horaFin">Hora de Fin:</label>
                <input type="time" id="horaFin" name="horaFin" required step="3600">
            </div>

            <button type="submit" class="btn-submit">Confirmar y Registrar Reserva</button>
            <a href="${pageContext.request.contextPath}/canchas/catalogo" class="btn-volver">Cancelar y Volver al Catálogo</a>
        </form>
    </div>
</body>
</html>