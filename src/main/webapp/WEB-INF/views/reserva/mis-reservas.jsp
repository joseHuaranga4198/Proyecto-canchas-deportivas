<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mis Reservas - Canchas Deportivas</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif; background-color: #f8fafc; margin: 0; padding-bottom: 3rem; }
        .navbar { background: #1e293b; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; color: #fff; }
        .navbar a { color: #f8fafc; text-decoration: none; margin-left: 1rem; font-weight: 500; font-size: 0.95rem; }
        .container { max-width: 1000px; margin: 2rem auto; padding: 0 1rem; }
        .box { background: white; border-radius: 8px; padding: 1.5rem; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        table { width: 100%; border-collapse: collapse; text-align: left; }
        th { background: #f8fafc; color: #475569; padding: 0.75rem 1rem; border-bottom: 2px solid #e2e8f0; font-size: 0.85rem; text-transform: uppercase; }
        td { padding: 0.85rem 1rem; border-bottom: 1px solid #e2e8f0; color: #334155; font-size: 0.95rem; }
        .badge { display: inline-block; padding: 0.25rem 0.6rem; border-radius: 4px; font-size: 0.8rem; font-weight: 600; }
        .badge-confirmada { background: #dcfce7; color: #15803d; }
        .badge-pendiente { background: #fef9c3; color: #854d0e; }
        .badge-cancelada { background: #fee2e2; color: #b91c1c; }
        .btn-pay { background: #0284c7; color: white; padding: 0.35rem 0.7rem; border-radius: 4px; text-decoration: none; font-size: 0.85rem; font-weight: 600; }
        .btn-cancel { background: #ef4444; color: white; border: none; padding: 0.35rem 0.7rem; border-radius: 4px; font-size: 0.85rem; cursor: pointer; }
    </style>
</head>
<body>

    <header class="navbar">
        <div style="font-weight: bold; font-size: 1.2rem;">⚽ Reserva de Canchas</div>
        <nav>
            <a href="${pageContext.request.contextPath}/canchas/catalogo">Catálogo</a>
            <a href="${pageContext.request.contextPath}/reservas/disponibilidad">Ver Horarios</a>
            <a href="${pageContext.request.contextPath}/reservas/mis-reservas" style="color: #38bdf8;">Mis Reservas</a>
            <a href="${pageContext.request.contextPath}/usuario/logout" style="color: #ef4444;">Salir (${sessionScope.usuarioLogueado.nombres})</a>
        </nav>
    </header>

    <main class="container">
        <h2>Historial de Mis Reservas</h2>
        <div class="box">
            <table>
                <thead>
                    <tr>
                        <th>N° Turno</th>
                        <th>Cancha</th>
                        <th>Fecha</th>
                        <th>Horario</th>
                        <th>Importe</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${reservas}">
                        <tr>
                            <td>#${r.idReserva}</td>
                            <td>${r.cancha.nombre}</td>
                            <td>${r.fecha}</td>
                            <td>${r.horaInicio} - ${r.horaFin}</td>
                            <td>S/ ${r.importe}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${r.estado eq 'CONFIRMADA'}"><span class="badge badge-confirmada">CONFIRMADA</span></c:when>
                                    <c:when test="${r.estado eq 'PENDIENTE'}"><span class="badge badge-pendiente">PENDIENTE</span></c:when>
                                    <c:otherwise><span class="badge badge-cancelada">${r.estado}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${r.estado eq 'PENDIENTE'}">
                                    <a href="${pageContext.request.contextPath}/reservas/pago/${r.idReserva}" class="btn-pay">Pagar</a>
                                </c:if>
                                <c:if test="${r.estado ne 'CANCELADA'}">
                                    <form action="${pageContext.request.contextPath}/reservas/cancelar/${r.idReserva}" method="post" style="display:inline; margin-left: 0.3rem;">
                                        <button type="submit" class="btn-cancel" onclick="return confirm('¿Estás seguro de cancelar este turno?');">Cancelar</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty reservas}">
                        <tr>
                            <td colspan="7" style="text-align: center; color: #64748b; padding: 2rem;">No tienes reservas realizadas. <a href="${pageContext.request.contextPath}/canchas/catalogo">Reservar una cancha</a></td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </main>

</body>
</html>