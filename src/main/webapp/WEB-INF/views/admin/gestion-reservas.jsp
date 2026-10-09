<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Reservas | Admin</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 2rem; }
        .box { max-width: 1050px; margin: 0 auto; background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
        .btn-volver { background: #64748b; color: white; padding: 0.5rem 1rem; text-decoration: none; border-radius: 4px; font-size: 0.9rem; }
        table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
        th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #e2e8f0; font-size: 0.9rem; }
        th { background-color: #f8fafc; color: #475569; }
        .badge-confirmada { background: #dcfce7; color: #15803d; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
        .badge-cancelada { background: #fee2e2; color: #b91c1c; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
        .btn-cancelar { background: #ef4444; color: white; border: none; padding: 0.4rem 0.7rem; border-radius: 4px; cursor: pointer; }
    </style>
</head>
<body>
    <div class="box">
        <div class="header-actions">
            <h2>Control Administrativo de Reservas</h2>
            <a href="${pageContext.request.contextPath}/admin/panel" class="btn-volver">← Volver al Panel</a>
        </div>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Cliente</th>
                    <th>Cancha</th>
                    <th>Fecha</th>
                    <th>Horario</th>
                    <th>Monto</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${reservas}">
                    <tr>
                        <td>#${r.id}</td>
                        <td>${r.clienteNombre}</td>
                        <td>${r.canchaNombre}</td>
                        <td>${r.fecha}</td>
                        <td>${r.horaInicio} - ${r.horaFin}</td>
                        <td>S/ ${r.montoTotal}</td>
                        <td>
                            <c:choose>
                                <c:when test="${r.estado == 'CONFIRMADA'}">
                                    <span class="badge-confirmada">${r.estado}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge-cancelada">${r.estado}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${r.estado == 'CONFIRMADA'}">
                                <form action="${pageContext.request.contextPath}/admin/reservas/cancelar/${r.id}" method="post" style="display:inline;">
                                    <button type="submit" class="btn-cancelar" onclick="return confirm('¿Seguro de cancelar esta reserva?')">Cancelar Reserva</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>