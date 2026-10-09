<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Reservas - Administrador</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding-bottom: 3rem; }
        .navbar { background: #0f172a; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; color: #fff; }
        .navbar a { color: #f8fafc; text-decoration: none; margin-left: 1.2rem; font-weight: 500; font-size: 0.95rem; }
        .navbar a:hover { color: #38bdf8; }
        .container { max-width: 1150px; margin: 2rem auto; padding: 0 1.5rem; }
        .box { background: white; border-radius: 8px; padding: 1.5rem; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        table { width: 100%; border-collapse: collapse; text-align: left; }
        th { background: #f8fafc; color: #475569; padding: 0.75rem 1rem; border-bottom: 2px solid #e2e8f0; font-size: 0.85rem; text-transform: uppercase; }
        td { padding: 0.85rem 1rem; border-bottom: 1px solid #e2e8f0; color: #334155; font-size: 0.95rem; }
        .badge { display: inline-block; padding: 0.25rem 0.6rem; border-radius: 4px; font-size: 0.8rem; font-weight: 600; }
        .badge-confirmada { background: #dcfce7; color: #15803d; }
        .badge-pendiente { background: #fef9c3; color: #854d0e; }
        .badge-cancelada { background: #fee2e2; color: #b91c1c; }
        .btn-cancelar { background: #ef4444; color: white; border: none; padding: 0.4rem 0.8rem; border-radius: 4px; font-size: 0.85rem; cursor: pointer; }
        .btn-cancelar:hover { background: #dc2626; }
    </style>
</head>
<body>

    <header class="navbar">
        <div style="font-weight: bold; font-size: 1.2rem;">⚙️ Administración de Canchas</div>
        <nav>
            <a href="${pageContext.request.contextPath}/admin/panel">Panel</a>
            <a href="${pageContext.request.contextPath}/canchas/admin">Canchas</a>
            <a href="${pageContext.request.contextPath}/admin/categorias">Categorías</a>
            <a href="${pageContext.request.contextPath}/admin/reservas" style="color: #38bdf8;">Reservas</a>
            <a href="${pageContext.request.contextPath}/usuario/admin/lista">Clientes</a>
            <a href="${pageContext.request.contextPath}/usuario/logout" style="color: #f87171;">Cerrar Sesión</a>
        </nav>
    </header>

    <main class="container">
        <h2>Listado General de Reservas</h2>
        <div class="box">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Cliente</th>
                        <th>Cancha</th>
                        <th>Fecha</th>
                        <th>Horario</th>
                        <th>Importe</th>
                        <th>Estado</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${reservas}">
                        <tr>
                            <td>#${r.idReserva}</td>
                            <td>${r.usuario.nombres} ${r.usuario.apellidos}</td>
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
                                <c:if test="${r.estado ne 'CANCELADA'}">
                                    <form action="${pageContext.request.contextPath}/admin/reservas/cancelar/${r.idReserva}" method="post" style="display:inline;">
                                        <button type="submit" class="btn-cancelar" onclick="return confirm('¿Desea cancelar esta reserva?');">Cancelar</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty reservas}">
                        <tr>
                            <td colspan="8" style="text-align: center; color: #64748b; padding: 2rem;">No existen reservas registradas.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </main>

</body>
</html>