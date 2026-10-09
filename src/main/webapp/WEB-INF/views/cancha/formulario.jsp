<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><c:choose><c:when test="${not empty cancha.idCancha}">Editar Cancha</c:when><c:otherwise>Nueva Cancha</c:otherwise></c:choose></title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 2rem; }
        .form-card { max-width: 520px; margin: 0 auto; background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .form-card h2 { margin-top: 0; color: #1e293b; }
        .form-group { margin-bottom: 1.2rem; }
        label { display: block; font-weight: bold; margin-bottom: 0.4rem; color: #475569; font-size: 0.9rem; }
        input[type="text"], input[type="number"], select, textarea { width: 100%; padding: 0.65rem; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box; font-family: inherit; font-size: 0.95rem; }
        textarea { resize: vertical; min-height: 70px; }
        .btn-submit { width: 100%; background: #2563eb; color: white; border: none; padding: 0.75rem; border-radius: 4px; font-weight: bold; font-size: 1rem; cursor: pointer; }
        .btn-submit:hover { background: #1d4ed8; }
        .btn-cancelar { display: block; text-align: center; margin-top: 1rem; color: #64748b; text-decoration: none; font-size: 0.9rem; }
    </style>
</head>
<body>
    <div class="form-card">
        <h2>
            <c:choose>
                <c:when test="${not empty cancha.idCancha}">Editar Cancha (#${cancha.idCancha})</c:when>
                <c:otherwise>Registrar Nueva Cancha</c:otherwise>
            </c:choose>
        </h2>

        <form action="${pageContext.request.contextPath}/canchas/admin/guardar" method="post">
            <input type="hidden" name="idCancha" value="${cancha.idCancha}">

            <div class="form-group">
                <label for="nombre">Nombre de la Cancha:</label>
                <input type="text" id="nombre" name="nombre" value="${cancha.nombre}" required placeholder="Ej: Cancha Central 1">
            </div>

            <div class="form-group">
                <label for="idCategoria">Categoría / Disciplina:</label>
                <select id="idCategoria" name="idCategoria" required>
                    <c:forEach var="cat" items="${categorias}">
                        <option value="${cat.idCategoria}" ${cancha.categoria != null && cancha.categoria.idCategoria == cat.idCategoria ? 'selected' : ''}>
                            ${cat.nombre}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="precioHora">Precio por Hora (S/):</label>
                <input type="number" step="0.50" id="precioHora" name="precioHora" value="${cancha.precioHora}" required placeholder="50.00">
            </div>

            <div class="form-group">
                <label for="caracteristicas">Características y Equipamiento:</label>
                <textarea id="caracteristicas" name="caracteristicas" placeholder="Grass sintético, iluminación LED, camerinos...">${cancha.caracteristicas}</textarea>
            </div>

            <div class="form-group">
                <label for="estado">Estado:</label>
                <select id="estado" name="estado">
                    <option value="ACTIVA" ${cancha.estado == 'ACTIVA' ? 'selected' : ''}>ACTIVA</option>
                    <option value="INACTIVA" ${cancha.estado == 'INACTIVA' ? 'selected' : ''}>INACTIVA</option>
                </select>
            </div>

            <button type="submit" class="btn-submit">Guardar Cancha</button>
            <a href="${pageContext.request.contextPath}/canchas/admin" class="btn-cancelar">Cancelar y Volver</a>
        </form>
    </div>
</body>
</html>