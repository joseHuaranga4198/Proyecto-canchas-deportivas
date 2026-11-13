<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Ocurrió un problema | CanchaYa</title><link rel="stylesheet" href="<c:url value='/css/comun.css'/>"><link rel="stylesheet" href="<c:url value='/css/navegacion.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/botones.css'/>">
</head>
<body><%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>
<main class="container narrow"><section class="panel error-panel"><p class="eyebrow">NO SE PUDO COMPLETAR</p><h1>Ocurrió un problema</h1><c:if test="${not empty error}"><p role="alert" style="color:#991b1b;background:#fee2e2;padding:1rem;"><c:out value="${error}"/></p></c:if><p class="muted">Vuelve a intentarlo en unos momentos o regresa al inicio.</p><a class="button primary" href="<c:url value='/'/>">Ir al inicio</a></section></main><footer>CanchaYa</footer></body></html>
