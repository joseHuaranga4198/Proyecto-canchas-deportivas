<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mis Reservas</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 2rem; }
        .table-container { max-width: 900px; margin: 0 auto; background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
        th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #e2e8f0; }
        th { background-color: #f1f5f9; color: #475569; }
        .badge-confirmada { background: #dcfce7; color: #15803d; padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: bold; font-size: 0.85rem; }
        .badge-cancelada { background: #fee2e2; color: #b91c1c; padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: bold; font-size: 0.85rem; }
        .btn-cancelar { background: #ef4444; color: white; border: none; padding: 0.35rem 0.7rem; border-radius: 4px; cursor: pointer; }
        .btn-cancelar:hover { background: #dc2626; }
        .btn-nuevo { display: inline-block; margin-bottom: 1rem; text-decoration: none; background: #2563eb; color: white; padding: 0.5rem 1rem; border-radius: 4px; font-weight: bold; }
    </style>
</head>
<body>
    <div class="table-container">
        <h2>Historial de Mis Reservas</h2>
        <a href="${pageContext.request.contextPath}/canchas/catalogo" class="btn-nuevo">+ Nueva Reserva</a>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Cancha</th>
                    <th>Fecha</th>
                    <th>Horario</th>
                    <th>Total</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${reservas}">
                    <tr>
                        <td>#${r.id}</td>
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
                                <form action="${pageContext.request.contextPath}/reservas/cancelar/${r.id}" method="post" style="display:inline;">
                                    <button type="submit" class="btn-cancelar">Cancelar</button>
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