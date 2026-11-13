<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es"><head>
  <meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>CanchaYa | Reserva tu espacio deportivo</title><link rel="stylesheet" href="<c:url value='/css/comun.css'/>">
<link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
<link rel="stylesheet" href="<c:url value='/css/sistema.css'/>">  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head><body>
<%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
<main class="container">
  <section class="public-hero"><p class="eyebrow">DEPORTE PARA TODOS</p><h1>Encuentra tu cancha.<br>Arma el partido.</h1><p>Consulta espacios deportivos, revisa horarios y reserva en línea.</p><div class="hero-actions"><a class="button primary" href="<c:url value='/canchas/catalogo'/>">Explorar canchas</a><a class="button secondary" href="<c:url value='/usuario/registro'/>">Crear una cuenta</a></div></section>
  <section class="public-grid deportes hero-foto" aria-label="Canchas deportivas de CanchaYa">
    <img class="public-photo" src="<c:url value='/images/futbol-grass.png'/>" alt="Cancha ilustrativa de fútbol en grass de CanchaYa" width="1672" height="941">
    <img class="public-photo" src="<c:url value='/images/voley.png'/>" alt="Cancha ilustrativa de vóley de CanchaYa" width="1672" height="941">
  </section>
  <section class="public-grid"><article class="panel public-card"><span class="public-icon">01</span><h2>Explora</h2><p>Revisa las canchas y sus características disponibles.</p></article><article class="panel public-card"><span class="public-icon">02</span><h2>Elige un horario</h2><p>Consulta disponibilidad para planear tu actividad.</p></article><article class="panel public-card"><span class="public-icon">03</span><h2>Reserva</h2><p>Regístrate para gestionar tus reservas desde tu cuenta.</p></article></section>
  <section class="public-cta"><div><h2>¿Listo para jugar?</h2><p>Tu próximo partido comienza con una cancha.</p></div><a class="button primary" href="<c:url value='/canchas/catalogo'/>">Ver canchas</a></section>
</main><footer>CanchaYa · Plataforma de reservas deportivas · <a href="<c:url value='/contacto'/>">Contacto</a></footer>
</body></html>
