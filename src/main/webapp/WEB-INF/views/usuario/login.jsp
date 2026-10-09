<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Inicio de Sesión | Reserva de Canchas</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }
        .login-box { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); width: 100%; max-width: 380px; }
        .login-box h2 { text-align: center; color: #1e293b; margin-top: 0; }
        .form-group { margin-bottom: 1.2rem; }
        label { display: block; font-weight: bold; margin-bottom: 0.3rem; color: #475569; }
        input[type="email"], input[type="password"] { width: 100%; padding: 0.65rem; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box; }
        .btn-ingresar { width: 100%; background: #2563eb; color: white; border: none; padding: 0.75rem; border-radius: 4px; font-weight: bold; cursor: pointer; }
        .btn-ingresar:hover { background: #1d4ed8; }
        .alerta-error { background-color: #fee2e2; color: #b91c1c; padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; border: 1px solid #f87171; font-size: 0.9rem; }
        .alerta-exito { background-color: #dcfce7; color: #15803d; padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; border: 1px solid #86efac; font-size: 0.9rem; }
        .links { text-align: center; margin-top: 1.2rem; font-size: 0.9rem; }
        .links a { color: #2563eb; text-decoration: none; }
    </style>
</head>
<body>
    <div class="login-box">
        <h2>Iniciar Sesión</h2>

        <c:if test="${not empty error}">
            <div class="alerta-error">${error}</div>
        </c:if>
        <c:if test="${param.registrado == 'true'}">
            <div class="alerta-exito">Registro completado. Ahora puedes ingresar.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/usuario/login" method="post">
            <div class="form-group">
                <label for="correo">Correo Electrónico:</label>
                <input type="email" id="correo" name="correo" required placeholder="ejemplo@canchas.pe">
            </div>

            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required placeholder="••••••••">
            </div>

            <button type="submit" class="btn-ingresar">Ingresar</button>
        </form>

        <div class="links">
            ¿No tienes cuenta? <a href="${pageContext.request.contextPath}/usuario/registro">Regístrate aquí</a>
        </div>
    </div>
</body>
</html>