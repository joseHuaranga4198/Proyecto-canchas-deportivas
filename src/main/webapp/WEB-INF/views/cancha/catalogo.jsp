<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Catálogo de Canchas Deportivas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css">
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif; background-color: #f8fafc; margin: 0; padding-bottom: 3rem; }
        .navbar { background: #1e293b; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; color: #fff; }
        .navbar a { color: #f8fafc; text-decoration: none; margin-left: 1rem; font-weight: 500; font-size: 0.95rem; }
        .navbar a:hover { color: #38bdf8; }
        .catalogo-container { max-width: 1100px; margin: 2rem auto; padding: 0 1rem; }
        .grid-canchas { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1.5rem; margin-top: 1.5rem; }
        .card-cancha { border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.4rem; background: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.04); display: flex; flex-direction: column; justify-content: space-between; }
        .card-cancha h3 { margin: 0.5rem 0; color: #0f172a; font-size: 1.25rem; }
        .badge { align-self: flex-start; display: inline-block; padding: 0.25rem 0.6rem; border-radius: 4px; font-size: 0.8rem; font-weight: 600; background: #e0f2fe; color: #0369a1; text-transform: uppercase; }
        .desc { color: #64748b; font-size: 0.95rem; line-height: 1.4; min-height: 2.8rem; }
        .precio { font-size: 1.35rem; font-weight: 700; color: #16a34a; margin: 0.8rem 0; }
        .btn-group { display: flex; gap: 0.5rem; margin-top: 0.5rem; }
        .btn-reservar { flex: 1; text-align: center; text-decoration: none; background: #2563eb; color: #fff; padding: 0.65rem; border-radius: 6px; font-weight: 600; font-size: 0.9rem; }
        .btn-reservar:hover { background: #1d4ed8; }
        .btn-horarios { text-align: center; text-decoration: none; background: #f1f5f9; color: #334155; padding: 0.65rem 0.8rem; border-radius: 6px; font-weight: 600; font-size: 0.9rem; border: 1px solid #cbd5e1; }
        .btn-horarios:hover { background: #e2e8f0; }
    </style>
</head>
<body>

    <header class="navbar">
        <div style="font-weight: bold; font-size: 1.2rem;">⚽ Reserva de Canchas</div>
        <nav>
            <a href="${pageContext.request.contextPath}/canchas/catalogo">Catálogo</a>
            <a href="${pageContext.request.contextPath}/reservas/disponibilidad">Ver Horarios</a>
            <c:choose>
                <c:when test="${not empty sessionScope.usuarioLogueado}">
                    <c:if test="${sessionScope.usuarioLogueado.rol eq 'ADMINISTRADOR'}">
                        <a href="${pageContext.request.contextPath}/admin/panel" style="color: #facc15;">Panel Admin</a>
                    </c:if>
                    <c:if test="${sessionScope.usuarioLogueado.rol eq 'CLIENTE'}">
                        <a href="${pageContext.request.contextPath}/reservas/mis-reservas">Mis Reservas</a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/usuario/logout" style="color: #ef4444;">Salir (${sessionScope.usuarioLogueado.nombres})</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/usuario/login">Iniciar Sesión</a>
                    <a href="${pageContext.request.contextPath}/usuario/registro">Registrarse</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </header>

    <main class="catalogo-container">
        <h2>Catálogo de Canchas Deportivas</h2>
        <p style="color: #475569;">Explora los espacios disponibles, consulta la disponibilidad en tiempo real y reserva tu turno al instante.</p>

        <div class="grid-canchas">
            <c:forEach var="c" items="${canchas}">
                <article class="card-cancha">
                    <div>
                        <span class="badge">${c.categoria.nombre}</span>
                        <h3>${c.nombre}</h3>
                        <p class="desc">${c.caracteristicas}</p>
                    </div>
                    <div>
                        <div class="precio">S/ ${c.precioHora} <span style="font-size: 0.85rem; color: #64748b; font-weight: normal;">/ hora</span></div>
                        <div class="btn-group">
                            <a href="${pageContext.request.contextPath}/reservas/disponibilidad?canchaId=${c.idCancha}" class="btn-horarios">Horarios</a>
                            <a href="${pageContext.request.contextPath}/reservas/nueva?canchaId=${c.idCancha}" class="btn-reservar">Reservar Turno</a>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
    </main>

</body>
</html>