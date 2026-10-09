<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro de Cliente | Reserva de Canchas</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; padding: 1.5rem 0; }
        .registro-box { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); width: 100%; max-width: 440px; }
        .registro-box h2 { text-align: center; color: #1e293b; margin-top: 0; }
        .form-group { margin-bottom: 1rem; }
        label { display: block; font-weight: bold; margin-bottom: 0.3rem; color: #475569; font-size: 0.9rem; }
        input[type="text"], input[type="email"], input[type="password"] { width: 100%; padding: 0.6rem; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box; }
        .btn-registro { width: 100%; background: #16a34a; color: white; border: none; padding: 0.75rem; border-radius: 4px; font-weight: bold; cursor: pointer; margin-top: 0.5rem; }
        .btn-registro:hover { background: #15803d; }
        .alerta-error { background-color: #fee2e2; color: #b91c1c; padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; border: 1px solid #f87171; font-size: 0.9rem; }
        .links { text-align: center; margin-top: 1rem; font-size: 0.9rem; }
        .links a { color: #2563eb; text-decoration: none; }
    </style>
</head>
<body>
    <div class="registro-box">
        <h2>Crear Cuenta de Cliente</h2>

        <c:if test="${not empty error}">
            <div class="alerta-error">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/usuario/registro" method="post">
            <div class="form-group">
                <label for="nombre">Nombre:</label>
                <input type="text" id="nombre" name="nombre" required>
            </div>

            <div class="form-group">
                <label for="apellidos">Apellidos:</label>
                <input type="text" id="apellidos" name="apellidos" required>
            </div>

            <div class="form-group">
                <label for="correo">Correo Electrónico:</label>
                <input type="email" id="correo" name="correo" required>
            </div>

            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required>
            </div>

            <div class="form-group">
                <label for="telefono">Teléfono / Celular:</label>
                <input type="text" id="telefono" name="telefono" required>
            </div>

            <button type="submit" class="btn-registro">Completar Registro</button>
        </form>

        <div class="links">
            ¿Ya tienes cuenta? <a href="${pageContext.request.contextPath}/usuario/login">Inicia Sesión</a>
        </div>
    </div>
</body>
</html>