<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Datos del cliente | CanchaYa</title><link rel="stylesheet" href="<c:url value='/css/comun.css'/>"><link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head><body>
<%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
<main class="container narrow"><p class="eyebrow">CUENTA</p><h1><c:choose><c:when test="${esPerfil}">Mi perfil</c:when><c:otherwise>Editar cliente</c:otherwise></c:choose></h1>
<c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if>
<c:if test="${not empty mensaje}"><p class="notice success" role="status"><c:out value="${mensaje}"/></p></c:if>
<c:choose><c:when test="${esPerfil}"><c:url var="destino" value="/usuario/perfil"/></c:when><c:otherwise><c:url var="destino" value="/usuario/admin/editar/${idCliente}"/></c:otherwise></c:choose>
<form class="panel detail-panel" action="${destino}" method="post">
<input type="hidden" name="_csrf" value="${csrfToken}">
  <div class="details">
    <div class="field"><label for="nombres">Nombres</label><input id="nombres" name="nombres" maxlength="100" value="<c:out value='${usuario.nombres}'/>" required autocomplete="given-name"></div>
    <div class="field"><label for="apellidos">Apellidos</label><input id="apellidos" name="apellidos" maxlength="100" value="<c:out value='${usuario.apellidos}'/>" required autocomplete="family-name"></div>
    <div class="field"><label for="documento">Documento</label><input id="documento" name="documento" minlength="3" maxlength="20" value="<c:out value='${usuario.documento}'/>" required></div>
    <div class="field"><label for="telefono">Teléfono</label><input id="telefono" name="telefono" maxlength="20" value="<c:out value='${usuario.telefono}'/>" autocomplete="tel"></div>
    <div class="field"><label for="correo">Correo electrónico</label><input id="correo" type="email" name="correo" maxlength="100" value="<c:out value='${usuario.correo}'/>" required autocomplete="email"></div>
  </div>
  <p class="muted" style="margin:20px 0">Actualiza tus datos de contacto. Las credenciales y permisos se administran por separado.</p>
  <button class="button primary" type="submit">Guardar cambios</button>
  <c:choose><c:when test="${esPerfil}"><a class="button secondary" href="<c:url value='/canchas/catalogo'/>">Volver al catálogo</a></c:when><c:otherwise><a class="button secondary" href="<c:url value='/usuario/admin/lista'/>">Volver a clientes</a></c:otherwise></c:choose>
</form></main><footer>CanchaYa · Sistema de reservas deportivas</footer></body></html>
