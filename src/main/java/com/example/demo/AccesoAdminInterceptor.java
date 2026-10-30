package com.example.demo;

import com.example.demo.usuario.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AccesoAdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null && "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol())) return true;
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/"));
        return false;
    }
}
