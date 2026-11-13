package com.example.demo.seguridad;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import jakarta.servlet.http.*;
import org.springframework.web.servlet.HandlerInterceptor;

public class ProteccionCsrf implements HandlerInterceptor {
    public static final String ATRIBUTO = "tokenCsrf";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute(ATRIBUTO);
        if (token == null) { token = UUID.randomUUID().toString(); session.setAttribute(ATRIBUTO, token); }
        request.setAttribute("csrfToken", token);
        if (!java.util.Set.of("GET", "HEAD", "OPTIONS").contains(request.getMethod())) {
            String recibido = request.getParameter("_csrf");
            if (recibido == null || !MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8))) {
                response.sendError(403, "Formulario vencido o inválido. Recarga la página e inténtalo de nuevo.");
                return false;
            }
        }
        return true;
    }
}
