<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Gestión de Canchas | Admin</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 2rem; }
        .box { max-width: 1050px; margin: 0 auto; background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.2rem; }
        table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
        th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #e2e8f0; font-size: 0.9rem; }
        th { background-color: #f8fafc; color: #475569; }
        .badge-disponible { background: #dcfce7; color: #15803d; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
        .badge-inactivo { background: #fee2e2; color: #b91c1c; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
    </style>
<link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
<link rel="stylesheet" href="<c:url value='/css/sistema.css'/>">  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
    <div class="box">
        <div class="header-actions">
            <div>
                <h2>Gestión de Canchas Deportivas</h2>
                <a href="<c:url value='/admin/panel'/>" class="button secondary">← Volver al Panel</a>
            </div>
            <a href="<c:url value='/canchas/admin/nueva'/>" class="button primary">+ Registrar Cancha</a>
        </div>

        <c:if test="${not empty error}"><p role="alert" style="color:#991b1b;background:#fee2e2;padding:1rem;"><c:out value="${error}"/></p></c:if>
<div class="table-scroll"><table>
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
                        <td>#${c.idCancha}</td>
                        <td><strong><c:out value='${c.nombre}'/></strong></td>
                        <td><c:out value='${c.categoria.nombre}'/></td>
                        <td><c:out value='${c.caracteristicas}'/></td>
                        <td>S/ ${c.precioHora}</td>
                        <td>
                            <c:choose>
                                <c:when test="${c.estado == 'ACTIVA'}">
                                    <span class="badge-disponible">ACTIVA</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge-inactivo">${c.estado}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="<c:url value='/canchas/admin/editar/${c.idCancha}'/>" class="button secondary small">Editar</a>

                            <form action="<c:url value='/canchas/admin/estado/${c.idCancha}'/>" method="post" style="display:inline; margin-left: 0.3rem;">
<input type="hidden" name="_csrf" value="${csrfToken}">
                                <c:choose>
                                    <c:when test="${c.estado == 'ACTIVA'}">
                                        <input type="hidden" name="estado" value="INACTIVA">
                                        <button type="submit" class="button danger small">Deshabilitar</button>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" name="estado" value="ACTIVA">
                                        <button type="submit" class="button success small">Habilitar</button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table></div>
    </div>
</body>
</html>