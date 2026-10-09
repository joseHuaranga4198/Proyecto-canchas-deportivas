<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
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
        .btn-buscar { background: #2563eb; color: white; border: none; padding: 0.65rem 1.5rem; border-radius: 4px; font-weight: bold; cursor: pointer; }
        .btn-buscar:hover { background: #1d4ed8; }
        .info-cancha { background: #eff6ff; border-left: 4px solid #3b82f6; padding: 1rem; border-radius: 4px; margin-bottom: 1.5rem; }
        .info-cancha h3 { margin: 0 0 0.3rem 0; color: #1e40af; }
        .info-cancha p { margin: 0; color: #3b82f6; font-size: 0.9rem; }
        table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
        th, td { padding: 0.85rem; text-align: left; border-bottom: 1px solid #e2e8f0; font-size: 0.9rem; }
        th { background: #f1f5f9; color: #475569; }
        .badge-ocupado { background: #fee2e2; color: #b91c1c; padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: bold; font-size: 0.8rem; }
        .badge-disponible { background: #dcfce7; color: #15803d; padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: bold; font-size: 0.8rem; }
        .btn-reservar-ahora { background: #16a34a; color: white; padding: 0.5rem 1rem; text-decoration: none; border-radius: 4px; font-weight: bold; font-size: 0.9rem; display: inline-block; }
        .btn-reservar-ahora:hover { background: #15803d; }
    </style>
</head>
<body>
    <div class="navbar">
        <h2>Reserva de Canchas Deportivas</h2>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/canchas/catalogo">Catálogo</a>
            <a href="${pageContext.request.contextPath}/reservas/disponibilidad">Disponibilidad</a>
            <a href="${pageContext.request.contextPath}/reservas/mis-reservas">Mis Reservas</a>
            <a href="${pageContext.request.contextPath}/usuario/login">Iniciar Sesión</a>
        </div>
    </div>

    <div class="container">
        <h2>Consulta de Disponibilidad de Canchas (I04)</h2>

        <div class="filter-card">
            <form action="${pageContext.request.contextPath}/reservas/disponibilidad" method="get" class="filter-form">
                <div class="form-group">
                    <label for="canchaId">Seleccionar Cancha:</label>
                    <select id="canchaId" name="canchaId" required>
                        <c:forEach var="c" items="${canchas}">
                            <option value="${c.id}" ${c.id == canchaSeleccionada.id ? 'selected' : ''}>
                                ${c.nombre} (${c.categoria}) - S/ ${c.precioHora}/h
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="fecha">Seleccionar Fecha:</label>
                    <input type="date" id="fecha" name="fecha" value="${fechaSeleccionada}" required>
                </div>

                <button type="submit" class="btn-buscar">Consultar</button>
            </form>
        </div>

        <c:if test="${not empty canchaSeleccionada}">
            <div class="info-cancha">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <h3>${canchaSeleccionada.nombre}</h3>
                        <p><strong>Disciplina:</strong> ${canchaSeleccionada.categoria} | <strong>Tarifa:</strong> S/ ${canchaSeleccionada.precioHora} por hora</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/reservas/nueva?canchaId=${canchaSeleccionada.id}" class="btn-reservar-ahora">+ Reservar Turno</a>
                </div>
            </div>

            <h3>Horarios Ocupados para el ${fechaSeleccionada}</h3>
            <table>
                <thead>
                    <tr>
                        <th>Horario Reservado</th>
                        <th>Estado de la Cancha</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty reservasOcupadas}">
                            <c:forEach var="r" items="${reservasOcupadas}">
                                <tr>
                                    <td><strong>${r.horaInicio} - ${r.horaFin}</strong></td>
                                    <td><span class="badge-ocupado">HORARIO OCUPADO</span></td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="2" style="text-align: center; color: #16a34a; padding: 1.5rem;">
                                    <span class="badge-disponible">DISPONIBLE TODO EL DÍA</span>
                                    <p style="margin: 0.5rem 0 0 0; color: #64748b;">No hay reservas confirmadas para esta fecha.</p>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </c:if>
    </div>
</body>
</html>