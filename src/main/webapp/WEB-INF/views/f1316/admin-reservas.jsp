<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Reservas | Administración CanchaYa</title>
  <link rel="stylesheet" href="<c:url value='/css/f1316.css'/>">
</head>
<body>
<header class="topbar">
  <a class="brand" href="<c:url value='/admin/panel'/>">⚽ CanchaYa <span class="brand-tag">ADMIN</span></a>
  <nav aria-label="Navegación administrativa">
    <a href="<c:url value='/'/>">Inicio</a>
    <a href="<c:url value='/publicidad'/>">Publicidad</a>
    <a href="<c:url value='/contacto'/>">Contacto</a>
    <a href="<c:url value='/admin/panel'/>">Panel</a>
    <a href="<c:url value='/canchas/admin'/>">Canchas</a>
    <a class="active" href="<c:url value='/f1316/admin/reservas'/>">Reservas</a>
    <a href="<c:url value='/usuario/logout'/>">Cerrar sesión</a>
    <a href="${pageContext.request.contextPath}/usuario/login" target="_blank" rel="noopener">Abrir otra sesión</a>
  </nav>
</header>
<main class="container">
  <div class="page-heading"><div><p class="eyebrow">ADMINISTRACIÓN</p><h1>Consulta de reservas</h1>
    <p class="muted">Filtra el historial para localizar reservas y revisar sus estados.</p></div></div>
  <c:if test="${not empty mensaje}"><p class="notice success" role="status"><c:out value="${mensaje}"/></p></c:if>
  <c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if>

  <form class="filter-grid" method="get" action="<c:url value='/f1316/admin/reservas'/>">
    <div class="field"><label for="idReserva">Código</label><input id="idReserva" name="idReserva" type="text" value="<c:out value='${filtros.idReserva}'/>" placeholder="Ej. 104"></div>
    <div class="field"><label for="cliente">Cliente</label><input id="cliente" name="cliente" type="text" value="<c:out value='${filtros.cliente}'/>" placeholder="Nombre o correo"></div>
    <div class="field"><label for="cancha">Cancha</label><input id="cancha" name="cancha" type="text" value="<c:out value='${filtros.cancha}'/>" placeholder="Nombre de cancha"></div>
    <div class="field"><label for="estado">Estado</label>
      <select id="estado" name="estado"><option value="" <c:if test="${empty filtros.estado}">selected</c:if>>Todos</option>
        <option value="CONFIRMADA" <c:if test="${filtros.estado eq 'CONFIRMADA'}">selected</c:if>>Confirmada</option>
        <option value="CANCELADA" <c:if test="${filtros.estado eq 'CANCELADA'}">selected</c:if>>Cancelada</option></select></div>
    <div class="field"><label for="fechaDesde">Fecha desde</label><input id="fechaDesde" name="fechaDesde" type="date" value="<c:out value='${filtros.fechaDesde}'/>"></div>
    <div class="field"><label for="fechaHasta">Fecha hasta</label><input id="fechaHasta" name="fechaHasta" type="date" value="<c:out value='${filtros.fechaHasta}'/>"></div>
    <div class="filter-actions"><button class="button primary" type="submit">Filtrar reservas</button><a class="button quiet" href="<c:url value='/f1316/admin/reservas'/>">Limpiar filtros</a></div>
  </form>

  <section class="panel" aria-labelledby="table-title">
    <div class="panel-heading"><h2 id="table-title">Resultados</h2><span class="muted"><c:out value="${fn:length(reservas)}"/> reserva(s)</span></div>
    <p class="table-hint">Al pulsar «Cancelar» confirmas la acción administrativa. El historial del pago se conserva.</p>
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
              <td class="row-actions"><a class="button small secondary" href="<c:url value='/f1316/admin/reservas/${reserva.idReserva}'/>">Ver detalle</a>
                <c:if test="${reserva.estado eq 'CONFIRMADA'}"><form method="post" action="<c:url value='/f1316/admin/reservas/${reserva.idReserva}/cancelar'/>"><button class="button small danger" type="submit">Cancelar</button></form></c:if>
              </td>
            </tr>
          </c:forEach></tbody>
        </table></div>
      </c:when>
      <c:otherwise><div class="empty"><h3>Sin resultados</h3><p>Prueba con otros valores de filtro.</p></div></c:otherwise>
    </c:choose>
  </section>
</main>
<footer>CanchaYa · Administración</footer>
</body>
</html>
