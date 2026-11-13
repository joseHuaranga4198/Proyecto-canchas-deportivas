<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Consulta de Disponibilidad</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 0; color: #1e293b; }
        .navbar { background: #0f172a; color: white; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
        .navbar h2 { margin: 0; font-size: 1.2rem; }
        .nav-links a { color: #cbd5e1; text-decoration: none; margin-left: 1.2rem; font-size: 0.9rem; }
        .nav-links a:hover { color: white; }
        .container { max-width: 950px; margin: 2rem auto; padding: 0 1.5rem; }
        .filter-card { background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.5rem; box-shadow: 0 2px 4px rgba(0,0,0,0.04); margin-bottom: 2rem; }
        .filter-form { display: grid; grid-template-columns: 1fr 1fr auto; gap: 1rem; align-items: end; }
        .form-group label { display: block; font-weight: bold; margin-bottom: 0.35rem; font-size: 0.9rem; color: #475569; }
        select, input[type="date"] { width: 100%; padding: 0.65rem; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box; font-size: 0.9rem; }
        .info-cancha { background: #eff6ff; border-left: 4px solid #3b82f6; padding: 1rem; border-radius: 4px; margin-bottom: 1.5rem; }
        .info-cancha h3 { margin: 0 0 0.3rem 0; color: #1e40af; }
        .info-cancha p { margin: 0; color: #3b82f6; font-size: 0.9rem; }
        table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
        th, td { padding: 0.85rem; text-align: left; border-bottom: 1px solid #e2e8f0; font-size: 0.9rem; }
        th { background: #f1f5f9; color: #475569; }
        .badge-ocupado { background: #fee2e2; color: #b91c1c; padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: bold; font-size: 0.8rem; }
        .badge-disponible { background: #dcfce7; color: #15803d; padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: bold; font-size: 0.8rem; }
    </style>
<link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
<link rel="stylesheet" href="<c:url value='/css/sistema.css'/>">  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <h2>Consulta de Disponibilidad de Canchas</h2>

        <div class="filter-card">
            <form action="<c:url value='/reservas/disponibilidad'/>" method="get" class="filter-form">
                <div class="form-group">
                    <label for="canchaId">Seleccionar Cancha:</label>
                    <select id="canchaId" name="canchaId" required>
                        <c:forEach var="c" items="${canchas}">
                            <option value="${c.idCancha}" ${c.idCancha == canchaSeleccionada.idCancha ? 'selected' : ''}>
                                <c:out value='${c.nombre}'/> (<c:out value='${c.categoria.nombre}'/>) - S/ ${c.precioHora}/h
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="fecha">Seleccionar Fecha:</label>
                    <input type="date" id="fecha" name="fecha" value="${fechaSeleccionada}" required>
                </div>

                <button type="submit" class="button primary">Consultar</button>
            </form>
        </div>

        <c:if test="${not empty error}"><p role="alert" style="color:#991b1b;background:#fee2e2;padding:1rem;"><c:out value="${error}"/></p></c:if>
<c:if test="${not empty canchaSeleccionada}">
            <div class="info-cancha">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <h3><c:out value='${canchaSeleccionada.nombre}'/></h3>
                        <p><strong>Disciplina:</strong> <c:out value='${canchaSeleccionada.categoria.nombre}'/> | <strong>Tarifa:</strong> S/ ${canchaSeleccionada.precioHora} por hora</p>
                    </div>
                    <a href="<c:url value='/reservas/nueva?canchaId=${canchaSeleccionada.idCancha}'/>" class="button primary">+ Reservar Turno</a>
                </div>
            </div>

<h3>Turnos del ${fechaSeleccionada}</h3><p>Atención de 08:00 a 22:00. Cada turno representa una hora.</p>
<div class="turnos"><c:forEach var="t" items="${turnos}"><article class="turno ${t.estado eq 'Disponible' ? 'libre' : (t.estado eq 'Pasado' ? 'pasado' : 'ocupado')}"><strong>${t.inicio} – ${t.fin}</strong><span><c:out value="${t.estado}"/></span><c:if test="${t.estado eq 'Disponible'}"><c:url var="reservarTurno" value="/reservas/nueva"><c:param name="canchaId" value="${canchaSeleccionada.idCancha}"/><c:param name="fecha" value="${fechaSeleccionada}"/><c:param name="inicio" value="${t.inicio}"/></c:url><a class="button primary small" href="${reservarTurno}">Reservar turno</a></c:if></article></c:forEach></div>
        </c:if>
    </div>
</body>
</html>