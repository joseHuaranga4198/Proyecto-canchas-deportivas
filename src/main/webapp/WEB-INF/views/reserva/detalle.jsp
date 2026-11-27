<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Detalle de reserva | CanchaYa</title>
  <link rel="stylesheet" href="<c:url value='/css/comun.css'/>">
<link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
<main class="container narrow">
  <c:choose><c:when test="${esAdmin}"><a class="button secondary" href="<c:url value='/reservas/admin'/>">← Volver a reservas</a></c:when><c:otherwise><a class="button secondary" href="<c:url value='/reservas/mis-reservas'/>">← Volver a mis reservas</a></c:otherwise></c:choose>
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
          <div><dt>Fecha de registro</dt><dd><c:out value="${fechaPagoTexto}"/></dd></div>
          <div><dt>Estado</dt><dd><c:out value="${pago.estado}"/></dd></div>
        </dl>
      </c:when>
      <c:otherwise><p class="muted">No hay datos de pago asociados en este registro.</p></c:otherwise>
    </c:choose>
    <c:if test="${not empty devolucion}"><div class="notice success"><strong>Devolución simulada registrada</strong><p>S/ <c:out value="${devolucion.importe}"/> · <c:out value="${devolucion.fecha}"/></p><p><c:out value="${devolucion.motivo}"/></p></div></c:if>

  </section>

  <c:if test="${puedeCancelar}"><section class="cancel-box"><div><h2>Cancelar y devolver el pago</h2><p><c:choose><c:when test="${esAdmin}">Cancelación administrativa excepcional.</c:when><c:otherwise>Cancelación disponible con al menos 24 horas de anticipación.</c:otherwise></c:choose> Se devuelve el importe completo de forma simulada y se conserva el historial.</p></div>
  <c:choose><c:when test="${esAdmin}"><c:url var="cancelarUrl" value="/reservas/admin/${reserva.idReserva}/cancelar"/></c:when><c:otherwise><c:url var="cancelarUrl" value="/reservas/mis-reservas/${reserva.idReserva}/cancelar"/></c:otherwise></c:choose>
    <%@ include file="cancelar-boton.jspf" %>
  </section></c:if>
</main>
<%@ include file="cancelar-dialogo.jspf" %>
<footer>CanchaYa · Sistema de reservas de canchas deportivas</footer>
</body>
</html>
