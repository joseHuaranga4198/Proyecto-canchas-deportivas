<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Reservas | Administración CanchaYa</title>
  <link rel="stylesheet" href="<c:url value='/css/comun.css'/>">
<link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
<main class="container">
  <div class="page-heading"><div><p class="eyebrow">ADMINISTRACIÓN</p><h1>Consulta de reservas</h1>
    <p class="muted">Filtra el historial para localizar reservas y revisar sus estados.</p></div></div>
  <c:if test="${not empty mensaje}"><p class="notice success" role="status"><c:out value="${mensaje}"/></p></c:if>
  <c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if>

  <form class="filter-grid" method="get" action="<c:url value='/reservas/admin'/>">
    <div class="field"><label for="idReserva">Código</label><input id="idReserva" name="idReserva" type="text" value="<c:out value='${idReserva}'/>" placeholder="Ej. 104"></div>
    <div class="field"><label for="cliente">Cliente</label><input id="cliente" name="cliente" type="text" value="<c:out value='${cliente}'/>" placeholder="Nombre o correo"></div>
    <div class="field"><label for="cancha">Cancha</label><input id="cancha" name="cancha" type="text" value="<c:out value='${cancha}'/>" placeholder="Nombre de cancha"></div>
    <div class="field"><label for="estado">Estado</label>
      <select id="estado" name="estado"><option value="" <c:if test="${empty estado}">selected</c:if>>Todos</option>
        <option value="CONFIRMADA" <c:if test="${estado eq 'CONFIRMADA'}">selected</c:if>>Confirmada</option>
        <option value="CANCELADA" <c:if test="${estado eq 'CANCELADA'}">selected</c:if>>Cancelada</option></select></div>
    <div class="field"><label for="fechaDesde">Fecha desde</label><input id="fechaDesde" name="fechaDesde" type="date" value="<c:out value='${fechaDesde}'/>"></div>
    <div class="field"><label for="fechaHasta">Fecha hasta</label><input id="fechaHasta" name="fechaHasta" type="date" value="<c:out value='${fechaHasta}'/>"></div>
    <div class="filter-actions"><button class="button primary" type="submit">Filtrar reservas</button><a class="button quiet" href="<c:url value='/reservas/admin'/>">Limpiar filtros</a></div>
  </form>

  <section class="panel" aria-labelledby="table-title">
    <div class="panel-heading"><h2 id="table-title">Resultados</h2><span class="muted"><c:out value="${fn:length(reservas)}"/> reserva(s)</span></div>
    <c:choose>
      <c:when test="${not empty reservas}">
        <div class="table-scroll"><table>
          <thead><tr><th>Código</th><th>Cliente</th><th>Cancha</th><th>Fecha</th><th>Horario</th><th>Importe</th><th>Estado</th><th>Acciones</th></tr></thead>
          <tbody><c:forEach var="reserva" items="${reservas}">
            <tr>
              <td>#<c:out value="${reserva.idReserva}"/></td>
              <td><span><c:out value="${reserva.usuario.nombres}"/> <c:out value="${reserva.usuario.apellidos}"/></span><small class="subtext"><c:out value="${reserva.usuario.correo}"/></small></td>
              <td><c:out value="${reserva.cancha.nombre}"/></td>
              <td><c:out value="${reserva.fecha}"/></td>
              <td><c:out value="${reserva.horaInicio}"/> – <c:out value="${reserva.horaFin}"/></td>
              <td>S/ <c:out value="${reserva.importe}"/></td>
              <td><c:choose>
                <c:when test="${reserva.estado eq 'CANCELADA'}"><span class="badge cancelled"><c:out value="${reserva.estado}"/></span></c:when>
                <c:otherwise><span class="badge confirmed"><c:out value="${reserva.estado}"/></span></c:otherwise>
              </c:choose></td>
              <td class="row-actions"><a class="button small secondary" href="<c:url value='/reservas/admin/${reserva.idReserva}'/>">Ver detalle</a>
                <c:if test="${reserva.estado eq 'CONFIRMADA'}">
                  <c:url var="cancelarUrl" value="/reservas/admin/${reserva.idReserva}/cancelar"/>
                  <%@ include file="cancelar-boton.jspf" %>
                </c:if>
              </td>
            </tr>
          </c:forEach></tbody>
        </table></div>
      </c:when>
      <c:otherwise><div class="empty"><h3>Sin resultados</h3><p>Prueba con otros valores de filtro.</p></div></c:otherwise>
    </c:choose>
  </section>
</main>
<%@ include file="cancelar-dialogo.jspf" %>
<footer>CanchaYa · Administración</footer>
</body>
</html>
