<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Página no encontrada | CanchaYa</title><link rel="stylesheet" href="<c:url value='/css/comun.css'/>"><link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head>
<body><%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
<main class="container narrow"><section class="panel error-panel"><p class="eyebrow">ERROR 404</p><h1>No encontramos esa página</h1><p class="muted">La dirección puede haber cambiado o ya no estar disponible.</p><a class="button primary" href="<c:url value='/'/>">Ir al inicio</a></section></main><footer>CanchaYa</footer></body></html>
