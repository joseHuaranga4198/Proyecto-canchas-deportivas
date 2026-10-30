<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mis reservas | CanchaYa</title>
  <link rel="stylesheet" href="<c:url value='/css/f1316.css'/>">
</head>
<body>
<header class="topbar">
  <a class="brand" href="<c:url value='/canchas/catalogo'/>">⚽ CanchaYa</a>
  <nav aria-label="Navegación principal">
    <a href="<c:url value='/'/>">Inicio</a>
    <a href="<c:url value='/publicidad'/>">Publicidad</a>
    <a href="<c:url value='/contacto'/>">Contacto</a>
    <a href="<c:url value='/canchas/catalogo'/>">Canchas</a>
    <a class="active" href="<c:url value='/f1316/reservas'/>">Mis reservas</a>
    <a href="<c:url value='/usuario/logout'/>">Cerrar sesión</a>
    <a href="${pageContext.request.contextPath}/usuario/login" target="_blank" rel="noopener">Abrir otra sesión</a>
  </nav>
</header>
<main class="container">
  <div class="page-heading">
    <div><p class="eyebrow">CUENTA DEL CLIENTE</p><h1>Mis reservas</h1>
      <p class="muted">Consulta tus reservas y el estado de cada una.</p></div>
    <a class="button primary" href="<c:url value='/canchas/catalogo'/>">Buscar una cancha</a>
  </div>

  <c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if>
  <c:if test="${not empty mensaje}"><p class="notice success" role="status"><c:out value="${mensaje}"/></p></c:if>

  <nav class="filters" aria-label="Filtrar reservas por estado">
    <strong>Estado:</strong>
    <a class="button <c:if test='${empty estado}'>primary</c:if><c:if test='${not empty estado}'>quiet</c:if>" href="<c:url value='/f1316/reservas'/>">Todas</a>
    <a class="button <c:if test='${estado eq "CONFIRMADA"}'>primary</c:if><c:if test='${estado ne "CONFIRMADA"}'>quiet</c:if>" href="<c:url value='/f1316/reservas'><c:param name='estado' value='CONFIRMADA'/></c:url>">Confirmadas</a>
    <a class="button <c:if test='${estado eq "CANCELADA"}'>primary</c:if><c:if test='${estado ne "CANCELADA"}'>quiet</c:if>" href="<c:url value='/f1316/reservas'><c:param name='estado' value='CANCELADA'/></c:url>">Canceladas</a>
  </nav>

  <section class="panel" aria-labelledby="reservas-title">
    <div class="panel-heading"><h2 id="reservas-title">Historial</h2>
      <span class="muted"><c:out value="${fn:length(reservas)}"/> resultado(s)</span></div>
    <p class="table-hint">Al pulsar «Cancelar» confirmas la solicitud. Solo puedes cancelar con 24 horas o más antes del inicio.</p>
    <c:choose>
      <c:when test="${not empty reservas}">
        <div class="table-scroll"><table>
          <thead><tr><th>Código</th><th>Cancha</th><th>Fecha</th><th>Horario</th><th>Importe</th><th>Estado</th><th>Acción</th></tr></thead>
          <tbody>
          <c:forEach var="reserva" items="${reservas}">
            <tr>
              <td>#<c:out value="${reserva.idReserva}"/></td>
              <td><c:out value="${reserva.cancha.nombre}"/></td>
              <td><c:out value="${reserva.fecha}"/></td>
              <td><c:out value="${reserva.horaInicio}"/> – <c:out value="${reserva.horaFin}"/></td>
              <td>S/ <c:out value="${reserva.importe}"/></td>
              <td><c:choose>
                <c:when test="${reserva.estado eq 'CANCELADA'}"><span class="badge cancelled"><c:out value="${reserva.estado}"/></span></c:when>
                <c:otherwise><span class="badge confirmed"><c:out value="${reserva.estado}"/></span></c:otherwise>
              </c:choose></td>
              <td class="row-actions">
                <a class="button small secondary" href="<c:url value='/f1316/reservas/${reserva.idReserva}'/>">Ver detalle</a>
                <c:if test="${cancelables.contains(reserva.idReserva)}">
                  <form method="post" action="<c:url value='/f1316/reservas/${reserva.idReserva}/cancelar'/>">
                    <button class="button small danger" type="submit">Cancelar</button>
                  </form>
                </c:if>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table></div>
      </c:when>
      <c:otherwise>
        <div class="empty">
          <h3>No hay reservas para mostrar</h3>
          <p>Cuando completes el pago de una reserva, aparecerá aquí.</p>
          <a class="button primary" href="<c:url value='/canchas/catalogo'/>">Explorar canchas</a>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
</main>
<footer>CanchaYa · Sistema de reservas de canchas deportivas</footer>
</body>
</html>
