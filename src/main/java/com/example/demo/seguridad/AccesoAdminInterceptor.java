package com.example.demo.seguridad;

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
        Object dato = session == null ? null : session.getAttribute("usuarioLogueado");
        if (dato instanceof Usuario usuario && "ACTIVO".equalsIgnoreCase(usuario.getEstado())
                && "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol())) return true;
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/"));
        return false;
    }
}
