<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Clientes</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 2rem; }
        .box { max-width: 950px; margin: 0 auto; background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .header-actions { display: flex; justify-content: space-between; align-items: center; }
        .btn-volver { background: #64748b; color: white; padding: 0.5rem 1rem; text-decoration: none; border-radius: 4px; font-size: 0.9rem; }
        table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
        th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #e2e8f0; font-size: 0.9rem; }
        th { background-color: #f8fafc; color: #475569; }
        .badge-activo { background: #dcfce7; color: #15803d; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
        .badge-bloqueado { background: #fee2e2; color: #b91c1c; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
        .btn-bloquear { background: #eab308; color: #713f12; border: none; padding: 0.4rem 0.7rem; border-radius: 4px; cursor: pointer; font-weight: bold; }
        .btn-desbloquear { background: #22c55e; color: white; border: none; padding: 0.4rem 0.7rem; border-radius: 4px; cursor: pointer; font-weight: bold; }
    </style>
</head>
<body>
    <div class="box">
        <div class="header-actions">
            <h2>Gestión de Clientes (I14)</h2>
            <a href="${pageContext.request.contextPath}/admin/panel" class="btn-volver">← Volver al Panel</a>
        </div>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre Completo</th>
                    <th>Correo</th>
                    <th>Teléfono</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${clientes}">
                    <tr>
                        <td>#${c.id}</td>
                        <td>${c.nombre} ${c.apellidos}</td>
                        <td>${c.correo}</td>
                        <td>${c.telefono}</td>
                        <td>
                            <c:choose>
                                <c:when test="${c.estado == 'ACTIVO'}">
                                    <span class="badge-activo">ACTIVO</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge-bloqueado">BLOQUEADO</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <form action="${pageContext.request.contextPath}/usuario/admin/bloquear/${c.id}" method="post" style="display:inline;">
                                <c:choose>
                                    <c:when test="${c.estado == 'ACTIVO'}">
                                        <button type="submit" class="btn-bloquear">Bloquear</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="submit" class="btn-desbloquear">Desbloquear</button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>