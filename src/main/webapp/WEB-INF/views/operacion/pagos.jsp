<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Pagos y devoluciones | CanchaYa</title><link rel="stylesheet" href="<c:url value='/css/comun.css'/>"><link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>"><link rel="stylesheet" href="<c:url value='/css/sistema.css'/>">  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head><body>
<%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
<main class="container"><p class="eyebrow">ADMINISTRACIÓN</p><h1>Pagos y devoluciones</h1><p class="muted">Operaciones simuladas. El pago original se conserva; la devolución es un registro independiente por el importe completo.</p>
<c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if><c:if test="${not empty mensaje}"><p class="notice success" role="status"><c:out value="${mensaje}"/></p></c:if>
<form class="filters" action="<c:url value='/admin/pagos'/>" method="get"><div class="field"><label for="desde">Fecha de pago desde</label><input id="desde" name="desde" type="date" value="${desde}" required></div><div class="field"><label for="hasta">Hasta</label><input id="hasta" name="hasta" type="date" value="${hasta}" required></div>
<div class="field"><label for="canchaId">Cancha</label><select id="canchaId" name="canchaId"><option value="">Todas</option><c:forEach var="c" items="${canchas}"><option value="${c.idCancha}" ${canchaId eq c.idCancha ? 'selected' : ''}><c:out value="${c.nombre}"/></option></c:forEach></select></div>
<div class="field"><label for="estado">Devolución</label><select id="estado" name="estado"><option value="">Todos</option><option value="DEVUELTO" ${estado eq 'DEVUELTO' ? 'selected' : ''}>Devuelto</option><option value="SIN_DEVOLUCION" ${estado eq 'SIN_DEVOLUCION' ? 'selected' : ''}>Sin devolución</option></select></div><button class="button primary">Consultar</button></form>
<section class="panel"><div class="table-scroll" tabindex="0" role="region" aria-label="Tabla de pagos y devoluciones"><table><thead><tr><th scope="col">Acciones</th><th>Pago / Reserva</th><th>Cliente y cancha</th><th>Fecha de pago</th><th>Pagado</th><th>Devuelto</th><th>Saldo retenido</th><th>Reserva</th></tr></thead><tbody>
<c:forEach var="p" items="${pagos}">
  <tr>
    <td>
      <div class="operacion-acciones">
        <a class="button small secondary" href="<c:url value='/reservas/admin/${p.reservaId}'/>">Ver reserva</a>
      </div>
    </td>
    <td>#${p.id} / #${p.reservaId}</td><td><c:out value="${p.cliente}"/><span class="subtext"><c:out value="${p.cancha}"/></span></td><td>${p.fecha}</td><td>S/ ${p.importe}</td><td><c:choose><c:when test="${not empty p.devuelto}">S/ ${p.devuelto}<span class="subtext">${p.fechaDevolucion}</span></c:when><c:otherwise>S/ 0.00</c:otherwise></c:choose></td><td>S/ ${p.importe - (empty p.devuelto ? 0 : p.devuelto)}</td><td><c:out value="${p.reservaEstado}"/></td></tr></c:forEach>
<c:if test="${empty pagos}"><tr><td class="empty" colspan="8">No hay pagos para estos filtros.</td></tr></c:if></tbody></table></div></section></main><footer>CanchaYa · Administración</footer></body></html>
