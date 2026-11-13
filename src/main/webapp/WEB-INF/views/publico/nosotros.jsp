<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Sobre nosotros | CanchaYa</title><link rel="stylesheet" href="<c:url value='/css/comun.css'/>"><link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head><body>
<%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
<main class="container narrow"><p class="eyebrow">SOBRE NOSOTROS</p><h1>El deporte nos reúne</h1><p class="muted">CanchaYa es un negocio ficticio creado como proyecto del curso de Ingeniería Web.</p>
<section class="panel detail-panel" style="margin-top:24px"><h2>Nuestra propuesta</h2><p>Facilitamos la reserva de canchas de fútbol en grass y vóley. Queremos que organizar tu próximo partido sea sencillo: consulta horarios, elige tu espacio y confirma tu reserva.</p><h2>Nuestro compromiso</h2><p>Disponibilidad clara, precios por hora y un historial de tus reservas en un solo lugar.</p><h2>Horario de atención</h2><p>De 08:00 a 22:00. Reservas en bloques de una hora.</p></section>
<a class="button primary" href="<c:url value='/canchas/catalogo'/>">Explorar canchas</a></main><footer>CanchaYa · Proyecto académico</footer></body></html>
