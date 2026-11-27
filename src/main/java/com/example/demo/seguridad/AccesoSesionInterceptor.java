package com.example.demo.seguridad;

import com.example.demo.usuario.Usuario;
import com.example.demo.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

public class AccesoSesionInterceptor implements HandlerInterceptor {
    private final UsuarioService usuarios;

    public AccesoSesionInterceptor(UsuarioService usuarios) { this.usuarios = usuarios; }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        Object dato = session == null ? null : session.getAttribute("usuarioLogueado");
        Usuario vigente = null;
        if (dato instanceof Usuario usuario && usuario.getIdUsuario() != null && usuario.getIdUsuario() > 0) {
            vigente = usuarios.obtenerPorId(usuario.getIdUsuario());
        }
        if (vigente != null && "ACTIVO".equalsIgnoreCase(vigente.getEstado())
                && ("CLIENTE".equalsIgnoreCase(vigente.getRol()) || "ADMINISTRADOR".equalsIgnoreCase(vigente.getRol()))) {
            vigente.setContrasena(null);
            session.setAttribute("usuarioLogueado", vigente);
        } else {
            vigente = null;
            if (dato != null) session.invalidate();
        }

        String ruta = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (ruta == null) ruta = request.getServletPath();
        boolean paginaPublica = ruta.equals("/publicidad") || ruta.equals("/contacto") || ruta.equals("/nosotros")
                || ruta.equals("/usuario/registro");
        if (paginaPublica && vigente != null) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/"));
            return false;
        }
        boolean cliente = ruta.equals("/reservas/nueva") || ruta.equals("/reservas/guardar")
                || ruta.equals("/reservas/pago") || ruta.startsWith("/reservas/pago/")
                || ruta.equals("/reservas/mis-reservas") || ruta.startsWith("/reservas/mis-reservas/")
                || ruta.equals("/usuario/perfil");
        if (cliente && (vigente == null || !"CLIENTE".equalsIgnoreCase(vigente.getRol()))) {
            String destino = vigente == null ? "/usuario/login" : "/";
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + destino));
            return false;
        }
        return true;
    }
}
