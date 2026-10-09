<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Categorías | Admin</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 2rem; }
        .box { max-width: 1000px; margin: 0 auto; background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
        .btn-volver { background: #64748b; color: white; padding: 0.5rem 1rem; text-decoration: none; border-radius: 4px; font-size: 0.9rem; }
        .grid-layout { display: grid; grid-template-columns: 1fr 2fr; gap: 2rem; }
        .form-card { background: #f8fafc; padding: 1.2rem; border-radius: 6px; border: 1px solid #e2e8f0; }
        .form-group { margin-bottom: 1rem; }
        label { display: block; font-weight: bold; margin-bottom: 0.3rem; font-size: 0.85rem; color: #334155; }
        input[type="text"], textarea, select { width: 100%; padding: 0.55rem; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box; }
        .btn-guardar { background: #16a34a; color: white; border: none; padding: 0.65rem 1rem; border-radius: 4px; font-weight: bold; cursor: pointer; width: 100%; }
        table { width: 100%; border-collapse: collapse; }
        th, td { text-align: left; padding: 0.65rem; border-bottom: 1px solid #e2e8f0; font-size: 0.9rem; }
        th { background-color: #f1f5f9; color: #475569; }
        .badge { background: #dcfce7; color: #15803d; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; font-size: 0.75rem; }
        .btn-del { background: #ef4444; color: white; border: none; padding: 0.3rem 0.6rem; border-radius: 4px; cursor: pointer; font-size: 0.8rem; }
    </style>
</head>
<body>
    <div class="box">
        <div class="top-bar">
            <h2>Gestión de Categorías de Canchas (I11)</h2>
            <a href="${pageContext.request.contextPath}/admin/panel" class="btn-volver">← Volver al Panel</a>
        </div>

        <div class="grid-layout">
            <div class="form-card">
                <h3>Nueva Categoría</h3>
                <form action="${pageContext.request.contextPath}/admin/categorias/guardar" method="post">
                    <div class="form-group">
                        <label for="nombre">Nombre de Categoría:</label>
                        <input type="text" id="nombre" name="nombre" required placeholder="Ej: Vóley">
                    </div>
                    <div class="form-group">
                        <label for="descripcion">Descripción:</label>
                        <textarea id="descripcion" name="descripcion" rows="3" placeholder="Detalles de la disciplina..."></textarea>
                    </div>
                    <div class="form-group">
                        <label for="estado">Estado:</label>
                        <select id="estado" name="estado">
                            <option value="ACTIVA">ACTIVA</option>
                            <option value="INACTIVA">INACTIVA</option>
                        </select>
                    </div>
                    <button type="submit" class="btn-guardar">+ Registrar Categoría</button>
                </form>
            </div>

            <div>
                <h3>Categorías Registradas</h3>
                <table>
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
                                <td>#${cat.id}</td>
                                <td><strong>${cat.nombre}</strong></td>
                                <td>${cat.descripcion}</td>
                                <td><span class="badge">${cat.estado}</span></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/categorias/eliminar/${cat.id}" method="post" style="display:inline;">
                                        <button type="submit" class="btn-del" onclick="return confirm('¿Eliminar categoría?')">Eliminar</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>