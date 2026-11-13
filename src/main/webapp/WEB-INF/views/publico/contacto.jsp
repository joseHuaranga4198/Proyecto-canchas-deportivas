<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Contacto | CanchaYa</title><link rel="stylesheet" href="<c:url value='/css/comun.css'/>"><link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
<link rel="stylesheet" href="<c:url value='/css/sistema.css'/>">  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head>
<body><%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
<main class="container narrow"><div class="page-heading"><div><p class="eyebrow">CONTACTO</p><h1>Estamos para ayudarte</h1><p class="muted">Consulta sobre reservas, espacios deportivos o publicidad.</p></div></div>
  <section class="panel detail-panel"><p class="promo-label">DATOS ILUSTRATIVOS</p><h2>Atención CanchaYa</h2><dl class="details"><div><dt>Horario de atención de ejemplo</dt><dd>De 08:00 a 22:00</dd></div><div><dt>Canal de atención de ejemplo</dt><dd>Centro de ayuda de la plataforma</dd></div></dl><p class="muted illustrative-note">Los datos mostrados son ilustrativos. El formulario es una presentación estática: no envía ni almacena mensajes.</p></section>
  <section class="panel detail-panel"><h2>Formulario de contacto</h2>
    <p id="aviso-contacto">Formulario estático de demostración. Los datos no se envían ni se guardan.</p>
    <form class="contact-form" aria-describedby="aviso-contacto">
      <p><label for="nombre">Nombre</label><br><input id="nombre" type="text" maxlength="100" required autocomplete="name"></p>
      <p><label for="correo">Correo electrónico</label><br><input id="correo" type="email" maxlength="100" required autocomplete="email"></p>
      <p><label for="asunto">Asunto</label><br><input id="asunto" type="text" maxlength="150" required></p>
      <p><label for="mensaje">Mensaje</label><br><textarea id="mensaje" rows="5" maxlength="2000" required></textarea></p>
      <button class="button primary" type="button" disabled>Enviar (demostración)</button>
    </form>
  </section>
  <a class="button secondary" href="<c:url value='/'/>">Volver al inicio</a>
</main><footer>CanchaYa · Información de contacto ilustrativa</footer></body></html>
