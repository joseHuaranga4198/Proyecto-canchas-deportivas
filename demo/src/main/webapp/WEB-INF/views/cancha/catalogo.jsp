<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Catálogo de Canchas Deportivas</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 0; color: #1e293b; }
        .navbar { background: #0f172a; color: white; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
        .navbar h2 { margin: 0; font-size: 1.2rem; }
        .nav-links a { color: #cbd5e1; text-decoration: none; margin-left: 1.2rem; font-size: 0.9rem; }
        .nav-links a:hover { color: white; }
        .container { max-width: 1100px; margin: 2rem auto; padding: 0 1.5rem; }
        .header-title { margin-bottom: 2rem; }
        .header-title h1 { margin: 0 0 0.5rem 0; font-size: 1.8rem; }
        .header-title p { margin: 0; color: #64748b; }
        .grid-canchas { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1.5rem; }
        .card { background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.5rem; box-shadow: 0 2px 4px rgba(0,0,0,0.04); display: flex; flex-direction: column; justify-content: space-between; }
        .badge { display: inline-block; padding: 0.25rem 0.6rem; border-radius: 4px; font-size: 0.8rem; font-weight: bold; background: #e0f2fe; color: #0284c7; width: fit-content; margin-bottom: 0.75rem; }
        .card h3 { margin: 0 0 0.5rem 0; font-size: 1.25rem; color: #1e293b; }
        .card p { margin: 0 0 1rem 0; color: #64748b; font-size: 0.9rem; line-height: 1.4; flex-grow: 1; }
        .precio-box { font-size: 1.3rem; font-weight: bold; color: #16a34a; margin-bottom: 1.2rem; }
        .precio-box span { font-size: 0.85rem; color: #64748b; font-weight: normal; }
        .btn-group { display: flex; gap: 0.5rem; }
        .btn { text-align: center; text-decoration: none; padding: 0.6rem 1rem; border-radius: 4px; font-weight: bold; font-size: 0.9rem; flex: 1; }
        .btn-primary { background: #2563eb; color: white; }
        .btn-primary:hover { background: #1d4ed8; }
        .btn-secondary { background: #f1f5f9; color: #334155; border: 1px solid #cbd5e1; }
        .btn-secondary:hover { background: #e2e8f0; }
    </style>
</head>
<body>
    <div class="navbar">
        <h2>Reserva de Canchas Deportivas</h2>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/canchas/catalogo">Catálogo</a>
            <a href="${pageContext.request.contextPath}/reservas/disponibilidad">Disponibilidad</a>
            <a href="${pageContext.request.contextPath}/reservas/mis-reservas">Mis Reservas</a>
            <a href="${pageContext.request.contextPath}/usuario/login">Iniciar Sesión</a>
        </div>
    </div>

    <div class="container">
        <div class="header-title">
            <h1>Catálogo de Canchas</h1>
            <p>Consulta las canchas disponibles, sus características y selecciona el horario de tu preferencia.</p>
        </div>

        <div class="grid-canchas">
            <c:forEach var="c" items="${canchas}">
                <div class="card">
                    <div>
                        <span class="badge">${c.categoria}</span>
                        <h3>${c.nombre}</h3>
                        <p>${c.caracteristicas}</p>
                    </div>
                    <div>
                        <div class="precio-box">
                            S/ ${c.precioHora} <span>/ hora</span>
                        </div>
                        <div class="btn-group">
                            <a href="${pageContext.request.contextPath}/reservas/disponibilidad?canchaId=${c.id}" class="btn btn-secondary">Disponibilidad</a>
                            <a href="${pageContext.request.contextPath}/reservas/nueva?canchaId=${c.id}" class="btn btn-primary">Reservar</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</body>
</html>