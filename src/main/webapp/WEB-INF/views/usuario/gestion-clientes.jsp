<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Gestión de Clientes</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 2rem; }
        .box { max-width: 950px; margin: 0 auto; background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .header-actions { display: flex; justify-content: space-between; align-items: center; }
        table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
        th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #e2e8f0; font-size: 0.9rem; }
        th { background-color: #f8fafc; color: #475569; }
        .badge-activo { background: #dcfce7; color: #15803d; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
        .badge-bloqueado { background: #fee2e2; color: #b91c1c; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; }
    </style>
<link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
<link rel="stylesheet" href="<c:url value='/css/sistema.css'/>">  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
    <div class="box">
        <div class="header-actions">
            <h2>Gestión de Clientes</h2>
            <a href="<c:url value='/admin/panel'/>" class="button secondary">← Volver al Panel</a>
        </div>

        <c:if test="${not empty error}"><p role="alert" style="color:#991b1b;background:#fee2e2;padding:1rem;"><c:out value="${error}"/></p></c:if>
<c:if test="${not empty mensaje}"><p role="status"><c:out value="${mensaje}"/></p></c:if>
<div class="table-scroll"><table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Documento</th>
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
                        <td>#${c.idUsuario}</td>
                        <td><c:out value='${c.documento}'/></td>
                        <td><c:out value='${c.nombres}'/> <c:out value='${c.apellidos}'/></td>
                        <td><c:out value='${c.correo}'/></td>
                        <td><c:out value='${c.telefono}'/></td>
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
                            <a class="button secondary small" href="<c:url value='/usuario/admin/editar/${c.idUsuario}'/>">Editar</a>
<form action="<c:url value='/usuario/admin/bloquear/${c.idUsuario}'/>" method="post" style="display:inline;">
<input type="hidden" name="_csrf" value="${csrfToken}">
                                <c:choose>
                                    <c:when test="${c.estado == 'ACTIVO'}">
                                        <button type="submit" class="button danger small">Bloquear</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="submit" class="button success small">Desbloquear</button>
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