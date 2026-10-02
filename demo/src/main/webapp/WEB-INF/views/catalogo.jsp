<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Catálogo de Canchas Deportivas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css">
    <style>
        .catalogo-container { max-width: 1100px; margin: 2rem auto; font-family: sans-serif; padding: 0 1rem; }
        .grid-canchas { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1.5rem; margin-top: 1.5rem; }
        .card-cancha { border: 1px solid #ddd; border-radius: 8px; padding: 1.2rem; background: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .card-cancha h3 { margin-top: 0; color: #2c3e50; }
        .badge { display: inline-block; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.85rem; font-weight: bold; background: #e0f2fe; color: #0369a1; }
        .precio { font-size: 1.25rem; font-weight: bold; color: #16a34a; margin: 0.8rem 0; }
        .btn-reservar { display: block; text-align: center; text-decoration: none; background: #2563eb; color: #fff; padding: 0.6rem; border-radius: 4px; font-weight: bold; }
        .btn-reservar:hover { background: #1d4ed8; }
    </style>
</head>
<body>
    <div class="catalogo-container">
        <h2>Catálogo de Canchas Disponibles</h2>
        <p>Selecciona tu cancha preferida para verificar disponibilidad y generar tu reserva.</p>

        <div class="grid-canchas">
            <c:forEach var="c" items="${canchas}">
                <div class="card-cancha">
                    <span class="badge">${c.categoria}</span>
                    <h3>${c.nombre}</h3>
                    <p>${c.caracteristicas}</p>
                    <div class="precio">S/ ${c.precioHora} <span style="font-size: 0.85rem; color: #666; font-weight: normal;">/ hora</span></div>
                    <a href="${pageContext.request.contextPath}/reservas/nueva?canchaId=${c.id}" class="btn-reservar">Reservar Turno</a>
                </div>
            </c:forEach>
        </div>
    </div>
</body>
</html>