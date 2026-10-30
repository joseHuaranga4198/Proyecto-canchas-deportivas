package com.example.demo;

import com.example.demo.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavegacionController {
    @GetMapping("/")
    public String inicio(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "publico/home";
        if ("ADMINISTRADOR".equalsIgnoreCase(usuario.getRol())) return "redirect:/admin/panel";
        if ("CLIENTE".equalsIgnoreCase(usuario.getRol())) return "redirect:/canchas/catalogo";
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/publicidad")
    public String publicidad() { return "publico/publicidad"; }

    @GetMapping("/contacto")
    public String contacto() { return "publico/contacto"; }
}
