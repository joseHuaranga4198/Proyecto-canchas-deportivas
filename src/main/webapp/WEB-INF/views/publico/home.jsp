<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es"><head>
  <meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>CanchaYa | Reserva tu espacio deportivo</title><link rel="stylesheet" href="<c:url value='/css/f1316.css'/>">
</head><body>
<header class="topbar"><a class="brand" href="<c:url value='/'/>">⚽ CanchaYa</a>
  <nav aria-label="Navegación principal"><a class="active" href="<c:url value='/'/>">Inicio</a><a href="<c:url value='/publicidad'/>">Publicidad</a><a href="<c:url value='/contacto'/>">Contacto</a><a href="<c:url value='/canchas/catalogo'/>">Canchas</a><a href="<c:url value='/usuario/login'/>">Iniciar sesión</a><a href="<c:url value='/usuario/registro'/>">Crear cuenta</a><a href="${pageContext.request.contextPath}/usuario/login" target="_blank" rel="noopener">Abrir otra sesión</a></nav>
</header>
<main class="container">
  <section class="public-hero"><p class="eyebrow">DEPORTE PARA TODOS</p><h1>Encuentra tu cancha.<br>Arma el partido.</h1><p>Consulta espacios deportivos, revisa horarios y reserva en línea.</p><div class="hero-actions"><a class="button primary" href="<c:url value='/canchas/catalogo'/>">Explorar canchas</a><a class="button secondary" href="<c:url value='/usuario/registro'/>">Crear una cuenta</a></div></section>
  <section class="public-grid"><article class="panel public-card"><span class="public-icon">01</span><h2>Explora</h2><p>Revisa las canchas y sus características disponibles.</p></article><article class="panel public-card"><span class="public-icon">02</span><h2>Elige un horario</h2><p>Consulta disponibilidad para planear tu actividad.</p></article><article class="panel public-card"><span class="public-icon">03</span><h2>Reserva</h2><p>Regístrate para gestionar tus reservas desde tu cuenta.</p></article></section>
  <section class="public-cta"><div><h2>¿Listo para jugar?</h2><p>Tu próximo partido comienza con una cancha.</p></div><a class="button primary" href="<c:url value='/canchas/catalogo'/>">Ver canchas</a></section>
</main><footer>CanchaYa · Plataforma de reservas deportivas · <a href="<c:url value='/contacto'/>">Contacto</a></footer>
</body></html>
