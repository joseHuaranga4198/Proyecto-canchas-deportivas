package com.example.demo.admin;

import com.example.demo.cancha.CanchaService;
import com.example.demo.reserva.ReservaService;
import com.example.demo.usuario.Usuario;
import com.example.demo.usuario.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CanchaService canchaService;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    public AdminController(CanchaService canchaService, ReservaService reservaService, UsuarioService usuarioService) {
        this.canchaService = canchaService;
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
    }

    private boolean tienePermisoAdmin(HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        return user != null && "ADMINISTRADOR".equalsIgnoreCase(user.getRol());
    }

    @GetMapping("/panel")
    public String panelPrincipal(HttpSession session, Model model) {
        if (!tienePermisoAdmin(session)) {
            return "redirect:/usuario/login";
        }

        model.addAttribute("totalCanchas", canchaService.listarTodas().size());
        model.addAttribute("totalReservas", reservaService.listarTodas().size());
        model.addAttribute("totalClientes", usuarioService.listarClientes().size());
        model.addAttribute("reservasRecientes", reservaService.listarTodas());
        return "admin/panel";
    }

    @GetMapping("/reservas")
    public String gestionarReservas(HttpSession session, Model model) {
        if (!tienePermisoAdmin(session)) {
            return "redirect:/usuario/login";
        }

        model.addAttribute("reservas", reservaService.listarTodas());
        return "admin/gestion-reservas";
    }

    @PostMapping("/reservas/cancelar/{id}")
    public String cancelarReservaAdmin(@PathVariable("id") Long id, HttpSession session) {
        if (!tienePermisoAdmin(session)) {
            return "redirect:/usuario/login";
        }

        reservaService.cancelarReserva(id);
        return "redirect:/admin/reservas";
    }
}