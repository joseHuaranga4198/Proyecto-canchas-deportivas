<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Mantenimiento | CanchaYa</title><link rel="stylesheet" href="<c:url value='/css/comun.css'/>"><link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>"><link rel="stylesheet" href="<c:url value='/css/sistema.css'/>">  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head><body>
<%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
<main class="container"><p class="eyebrow">ADMINISTRACIÓN</p><h1>Disponibilidad y mantenimiento</h1><p class="muted">Elige la hora y los minutos entre las 08:00 y las 22:00. Si hay reservas en el intervalo, deben cancelarse antes de programar el mantenimiento.</p>
<c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if><c:if test="${not empty mensaje}"><p class="notice success" role="status"><c:out value="${mensaje}"/></p></c:if>
<form class="filters" action="<c:url value='/admin/disponibilidad/programar'/>" method="post"><input type="hidden" name="_csrf" value="${csrfToken}">
<div class="field"><label for="cancha">Cancha</label><select name="canchaId" id="cancha" required><option value="">Selecciona una cancha</option><c:forEach var="c" items="${canchas}"><option value="${c.idCancha}"><c:out value="${c.nombre}"/></option></c:forEach></select></div>
<div class="field"><label for="fecha">Fecha</label><input id="fecha" type="date" name="fecha" min="${fechaMinima}" required></div>
<div class="field"><label for="inicio">Inicio</label><input id="inicio" type="time" name="inicio" min="08:00" max="21:59" step="60" required></div>
<div class="field"><label for="fin">Fin</label><input id="fin" type="time" name="fin" min="08:01" max="22:00" step="60" required></div>
<div class="field"><label for="motivo">Motivo</label><input id="motivo" name="motivo" maxlength="250" required placeholder="Mantenimiento del grass"></div>
<button class="button primary" type="submit">Programar mantenimiento</button></form>
<section class="panel"><div class="panel-heading"><h2>Historial de mantenimientos</h2></div><div class="table-scroll" tabindex="0" role="region" aria-label="Historial de mantenimientos"><table><thead><tr><th scope="col">Acciones</th><th>Cancha</th><th>Fecha</th><th>Horario</th><th>Motivo</th><th>Estado</th></tr></thead><tbody>
<c:forEach var="b" items="${bloqueos}">
  <tr>
    <td>
      <c:choose>
        <c:when test="${b.estado eq 'ACTIVO'}">
          <form class="operacion-acciones" action="<c:url value='/admin/disponibilidad/anular/${b.id}'/>" method="post">
            <input type="hidden" name="_csrf" value="${csrfToken}">
            <button class="button small danger" type="submit">Anular mantenimiento</button>
          </form>
        </c:when>
        <c:otherwise><span class="muted">Sin acciones</span></c:otherwise>
      </c:choose>
    </td>
    <td><c:out value="${b.cancha}"/></td>
    <td>${b.fecha}</td>
    <td>${b.inicio} – ${b.fin}</td>
    <td><c:out value="${b.motivo}"/></td>
    <td><c:out value="${b.estado}"/></td>
  </tr>
</c:forEach>
<c:if test="${empty bloqueos}"><tr><td colspan="6" class="empty">No hay mantenimientos registrados.</td></tr></c:if></tbody></table></div></section></main><footer>CanchaYa · Administración</footer></body></html>
