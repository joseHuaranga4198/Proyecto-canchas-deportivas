<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Detalle de reserva | CanchaYa</title>
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
    <c:choose><c:when test="${esAdmin}"><a class="active" href="<c:url value='/f1316/admin/reservas'/>">Reservas</a></c:when><c:otherwise><a class="active" href="<c:url value='/f1316/reservas'/>">Mis reservas</a></c:otherwise></c:choose>
    <a href="<c:url value='/usuario/logout'/>">Cerrar sesión</a>
    <a href="${pageContext.request.contextPath}/usuario/login" target="_blank" rel="noopener">Abrir otra sesión</a>
  </nav>
</header>
<main class="container narrow">
  <c:choose><c:when test="${esAdmin}"><a class="back-link" href="<c:url value='/f1316/admin/reservas'/>">← Volver a reservas</a></c:when><c:otherwise><a class="back-link" href="<c:url value='/f1316/reservas'/>">← Volver a mis reservas</a></c:otherwise></c:choose>
  <div class="page-heading">
    <div><p class="eyebrow">RESERVA #<c:out value="${reserva.idReserva}"/></p><h1>Detalle de reserva</h1></div>
    <c:choose>
      <c:when test="${reserva.estado eq 'CANCELADA'}"><span class="badge cancelled"><c:out value="${reserva.estado}"/></span></c:when>
      <c:otherwise><span class="badge confirmed"><c:out value="${reserva.estado}"/></span></c:otherwise>
    </c:choose>
  </div>

  <c:if test="${not empty mensaje}"><p class="notice success" role="status"><c:out value="${mensaje}"/></p></c:if>
  <c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if>

  <section class="panel detail-panel" aria-labelledby="booking-title">
    <h2 id="booking-title"><c:out value="${reserva.cancha.nombre}"/></h2>
    <dl class="details">
      <div><dt>Fecha</dt><dd><c:out value="${reserva.fecha}"/></dd></div>
      <div><dt>Horario</dt><dd><c:out value="${reserva.horaInicio}"/> – <c:out value="${reserva.horaFin}"/></dd></div>
      <div><dt>Duración</dt><dd><c:out value="${duracionMinutos}"/> minutos</dd></div>
      <div><dt>Importe de reserva</dt><dd>S/ <c:out value="${reserva.importe}"/></dd></div>
      <div><dt>Estado</dt><dd><c:out value="${reserva.estado}"/></dd></div>
    </dl>
  </section>

  <section class="panel detail-panel" aria-labelledby="payment-title">
    <h2 id="payment-title">Datos del pago</h2>
    <c:choose>
      <c:when test="${not empty pago}">
        <dl class="details">
          <div><dt>Código de pago</dt><dd><c:choose><c:when test="${not empty pago.idPago}">#<c:out value="${pago.idPago}"/></c:when><c:otherwise>Registrado</c:otherwise></c:choose></dd></div>
          <div><dt>Importe</dt><dd>S/ <c:out value="${pago.importe}"/></dd></div>
          <div><dt>Fecha de registro</dt><dd><c:choose><c:when test="${not empty pago.fechaPago}"><c:out value="${pago.fechaPago}"/></c:when><c:otherwise>No disponible</c:otherwise></c:choose></dd></div>
          <div><dt>Estado</dt><dd><c:out value="${pago.estado}"/></dd></div>
        </dl>
      </c:when>
      <c:otherwise><p class="muted">No hay datos de pago asociados en este registro.</p></c:otherwise>
    </c:choose>
    <c:if test="${reserva.estado eq 'CANCELADA'}"><p class="muted refund-note">La reserva está cancelada. La cancelación por sí sola no confirma una devolución del dinero.</p></c:if>
  </section>

</main>
<footer>CanchaYa · Sistema de reservas de canchas deportivas</footer>
</body>
</html>
