<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Gestión de Categorías | Admin</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 2rem; }
        .box { max-width: 1000px; margin: 0 auto; background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
        .grid-layout { display: grid; grid-template-columns: 1fr 2fr; gap: 2rem; }
        .form-card { background: #f8fafc; padding: 1.2rem; border-radius: 6px; border: 1px solid #e2e8f0; }
        .form-group { margin-bottom: 1rem; }
        label { display: block; font-weight: bold; margin-bottom: 0.3rem; font-size: 0.85rem; color: #334155; }
        input[type="text"], textarea, select { width: 100%; padding: 0.55rem; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box; }
        table { width: 100%; border-collapse: collapse; }
        th, td { text-align: left; padding: 0.65rem; border-bottom: 1px solid #e2e8f0; font-size: 0.9rem; }
        th { background-color: #f1f5f9; color: #475569; }
        .badge { background: #dcfce7; color: #15803d; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; font-size: 0.75rem; }
    </style>
<link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
<link rel="stylesheet" href="<c:url value='/css/sistema.css'/>">  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
    <div class="box">
        <div class="top-bar">
            <h2>Gestión de Categorías de Canchas</h2>
            <a href="<c:url value='/admin/panel'/>" class="button secondary">← Volver al Panel</a>
        </div>

        <div class="grid-layout">
            <div class="form-card">
                <c:if test="${not empty error}"><p role="alert" style="color:#991b1b;background:#fee2e2;padding:1rem;"><c:out value="${error}"/></p></c:if><c:if test="${not empty mensaje}"><p class="notice success" role="status"><c:out value="${mensaje}"/></p></c:if><p class="muted">Las categorías no se eliminan: al desactivarlas, sus canchas dejan de mostrarse en el catálogo y se conservan sus reservas.</p>
<h3><c:choose><c:when test="${not empty nuevaCategoria.idCategoria}">Editar categoría</c:when><c:otherwise>Nueva categoría</c:otherwise></c:choose></h3>
                <form action="<c:url value='/admin/categorias/guardar'/>" method="post">
<input type="hidden" name="_csrf" value="${csrfToken}">
                    <input type="hidden" name="idCategoria" value="${nuevaCategoria.idCategoria}">
<div class="form-group">
                        <label for="nombre">Nombre de Categoría:</label>
                        <input type="text" id="nombre" name="nombre" value="<c:out value='${nuevaCategoria.nombre}'/>" maxlength="50" required placeholder="Ej: Vóley">
                    </div>
                    <div class="form-group">
                        <label for="descripcion">Descripción:</label>
                        <textarea id="descripcion" name="descripcion" rows="3" placeholder="Detalles de la disciplina..." maxlength="255"><c:out value="${nuevaCategoria.descripcion}"/></textarea>
                    </div>
                    <div class="form-group">
                        <label for="estado">Estado:</label>
                        <select id="estado" name="estado">
                            <option value="ACTIVA" ${nuevaCategoria.estado eq 'ACTIVA' ? 'selected' : ''}>ACTIVA</option>
                            <option value="INACTIVA" ${nuevaCategoria.estado eq 'INACTIVA' ? 'selected' : ''}>INACTIVA</option>
                        </select>
                    </div>
                    <button type="submit" class="button primary">Guardar categoría</button>
                </form><a class="button secondary" href="<c:url value='/admin/categorias'/>">Nueva categoría</a>
            </div>

            <div>
                <h3>Categorías Registradas</h3>
                <div class="table-scroll"><table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Descripción</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cat" items="${categorias}">
                            <tr>
                                <td>#${cat.idCategoria}</td>
                                <td><strong><c:out value='${cat.nombre}'/></strong></td>
                                <td><c:out value='${cat.descripcion}'/></td>
                                <td><span class="badge">${cat.estado}</span></td>
                                <td>
                                    <a class="button secondary small" href="<c:url value='/admin/categorias/editar/${cat.idCategoria}'/>">Editar</a>
<form action="<c:url value='/admin/categorias/estado/${cat.idCategoria}'/>" method="post" style="display:inline;">
<input type="hidden" name="_csrf" value="${csrfToken}">
<c:choose><c:when test="${cat.estado eq 'ACTIVA'}"><input type="hidden" name="estado" value="INACTIVA">
                                        <button type="submit" class="button danger small">Desactivar</button></c:when>
<c:otherwise><input type="hidden" name="estado" value="ACTIVA">
                                        <button type="submit" class="button primary small">Reactivar</button></c:otherwise></c:choose>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table></div>
            </div>
        </div>
    </div>
</body>
</html>