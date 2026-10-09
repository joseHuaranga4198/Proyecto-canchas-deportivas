<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión - Canchas Deportivas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css">
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f6f9; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }
        .login-card { background: white; padding: 2.5rem; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); width: 100%; max-width: 400px; }
        .login-card h2 { margin-top: 0; color: #2c3e50; text-align: center; }
        .form-group { margin-bottom: 1.2rem; }
        .form-group label { display: block; margin-bottom: 0.4rem; font-weight: bold; color: #34495e; }
        .form-group input { width: 100%; padding: 0.6rem; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .btn-submit { width: 100%; padding: 0.75rem; background-color: #27ae60; color: white; border: none; border-radius: 4px; font-size: 1rem; cursor: pointer; font-weight: bold; }
        .btn-submit:hover { background-color: #219150; }
        .alert-error { background-color: #f8d7da; color: #721c24; padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; text-align: center; }
        .alert-success { background-color: #d4edda; color: #155724; padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; text-align: center; }
        .links { text-align: center; margin-top: 1rem; font-size: 0.9rem; }
        .links a { color: #2980b9; text-decoration: none; }
    </style>
</head>
<body>

<div class="login-card">
    <h2>Acceso al Sistema</h2>

    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>
    <c:if test="${param.registrado eq 'true'}">
        <div class="alert-success">Cuenta creada con éxito. Inicia sesión.</div>
    </c:if>

    <!-- FORMULARIO CON ATRIBUTOS NAME ESTANDARIZADOS -->
    <form action="${pageContext.request.contextPath}/usuario/login" method="post">
        <div class="form-group">
            <label for="correo">Correo Electrónico:</label>
            <input type="email" id="correo" name="correo" required placeholder="ejemplo@canchas.pe" autocomplete="username">
        </div>

        <div class="form-group">
            <label for="contrasena">Contraseña:</label>
            <input type="password" id="contrasena" name="contrasena" required placeholder="••••••••" autocomplete="current-password">
        </div>

        <button type="submit" class="btn-submit">Ingresar</button>
    </form>

    <div class="links">
        <p>¿No tienes cuenta? <a href="${pageContext.request.contextPath}/usuario/registro">Regístrate aquí</a></p>
        <p><a href="${pageContext.request.contextPath}/canchas/catalogo">Volver al catálogo</a></p>
    </div>
</div>

</body>
</html>