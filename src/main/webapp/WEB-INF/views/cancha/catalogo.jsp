<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Catálogo de Canchas Deportivas</title>
    <link rel="stylesheet" href="<c:url value='/css/menu.css'/>">
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
    </style>
<link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head>
<body>

    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <main class="catalogo-container">
        <h2>Catálogo de Canchas Deportivas</h2>
        <p style="color: #475569;">Explora los espacios disponibles, consulta la disponibilidad en tiempo real y reserva tu turno al instante.</p>

        <c:if test="${empty canchas}"><div style="padding:32px;background:white;border-radius:12px;margin:24px 0"><h3>Pronto tendremos espacios para ti</h3><p>No hay canchas activas disponibles en este momento.</p></div></c:if>
<div class="grid-canchas">
            <c:if test="${not empty error}"><p role="alert" style="color:#991b1b;background:#fee2e2;padding:1rem;"><c:out value="${error}"/></p></c:if>
<c:forEach var="c" items="${canchas}">
                <article class="card-cancha">
                    <div>
                        <span class="badge"><c:out value='${c.categoria.nombre}'/></span>
                        <h3><c:out value='${c.nombre}'/></h3>
                        <p class="desc"><c:out value='${c.caracteristicas}'/></p>
                    </div>
                    <div>
                        <div class="precio">S/ ${c.precioHora} <span style="font-size: 0.85rem; color: #64748b; font-weight: normal;">/ hora</span></div>
                        <div class="btn-group">
                            <a href="<c:url value='/reservas/disponibilidad?canchaId=${c.idCancha}'/>" class="button secondary">Horarios</a>
                            <a href="<c:url value='/reservas/nueva?canchaId=${c.idCancha}'/>" class="button primary">Reservar Turno</a>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
    </main>

</body>
</html>
