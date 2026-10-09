<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Canchas | Admin</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 2rem; }
        .box { max-width: 1050px; margin: 0 auto; background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.2rem; }
        .btn-panel { background: #64748b; color: white; padding: 0.5rem 1rem; text-decoration: none; border-radius: 4px; font-size: 0.9rem; }
        .btn-crear { background: #16a34a; color: white; padding: 0.5rem 1rem; text-decoration: none; border-radius: 4px; font-weight: bold; font-size: 0.9rem; }
        .btn-crear:hover { background: #15803d; }
        table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
        th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #e2e8f0; font-size: 0.9rem; }
        th { background-color: #f8fafc; color: #475569; }
        .badge-disponible { background: #dcfce7; color: #15803d; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
        .badge-mantenimiento { background: #fef3c7; color: #b45309; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
        .badge-inactivo { background: #fee2e2; color: #b91c1c; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
        .btn-editar { background: #2563eb; color: white; padding: 0.35rem 0.65rem; text-decoration: none; border-radius: 4px; font-size: 0.85rem; }
        .btn-accion { background: #e2e8f0; color: #334155; border: 1px solid #cbd5e1; padding: 0.35rem 0.65rem; border-radius: 4px; cursor: pointer; font-size: 0.85rem; }
        .btn-accion:hover { background: #cbd5e1; }
    </style>
</head>
<body>
    <div class="box">
        <div class="header-actions">
            <div>
                <h2>Gestión de Canchas Deportivas (I10)</h2>
                <a href="${pageContext.request.contextPath}/admin/panel" class="btn-panel">← Volver al Panel</a>
            </div>
            <a href="${pageContext.request.contextPath}/canchas/admin/nueva" class="btn-crear">+ Registrar Cancha</a>
        </div>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Categoría</th>
                    <th>Características</th>
                    <th>Precio / Hora</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${canchas}">
                    <tr>
                        <td>#${c.id}</td>
                        <td><strong>${c.nombre}</strong></td>
                        <td>${c.categoria}</td>
                        <td>${c.caracteristicas}</td>
                        <td>S/ ${c.precioHora}</td>
                        <td>
                            <c:choose>
                                <c:when test="${c.estado == 'DISPONIBLE'}">
                                    <span class="badge-disponible">DISPONIBLE</span>
                                </c:when>
                                <c:when test="${c.estado == 'MANTENIMIENTO'}">
                                    <span class="badge-mantenimiento">MANTENIMIENTO</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge-inactivo">${c.estado}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/canchas/admin/editar/${c.id}" class="btn-editar">Editar</a>

                            <form action="${pageContext.request.contextPath}/canchas/admin/estado/${c.id}" method="post" style="display:inline; margin-left: 0.3rem;">
                                <c:choose>
                                    <c:when test="${c.estado == 'DISPONIBLE'}">
                                        <input type="hidden" name="estado" value="MANTENIMIENTO">
                                        <button type="submit" class="btn-accion">Mantenimiento</button>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" name="estado" value="DISPONIBLE">
                                        <button type="submit" class="btn-accion">Habilitar</button>
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