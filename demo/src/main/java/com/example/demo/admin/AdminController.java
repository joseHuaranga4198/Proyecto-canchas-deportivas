package com.example.demo.admin;

import com.example.demo.cancha.CanchaService;
import com.example.demo.reserva.ReservaService;
import com.example.demo.usuario.Usuario;
import com.example.demo.usuario.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CanchaService canchaService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private UsuarioService usuarioService;

    // I09: Panel Principal Administrativo
    @GetMapping("/panel")
    public String panelPrincipal(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !"ADMIN".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/usuario/login";
        }

        // Resumen rápido de entidades para el dashboard operativo
        model.addAttribute("totalCanchas", canchaService.listarTodas().size());
        model.addAttribute("totalReservas", reservaService.listarTodas().size());
        model.addAttribute("totalClientes", usuarioService.listarClientes().size());
        model.addAttribute("reservasRecientes", reservaService.listarTodas());
        return "admin/panel";
    }

    // I15 / F16: Gestión de Reservas Administrativa
    @GetMapping("/reservas")
    public String gestionarReservas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !"ADMIN".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/usuario/login";
        }

        model.addAttribute("reservas", reservaService.listarTodas());
        return "admin/gestion-reservas";
    }

    // F17: Cancelación administrativa de reserva
    @PostMapping("/reservas/cancelar/{id}")
    public String cancelarReservaAdmin(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !"ADMIN".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/usuario/login";
        }

        reservaService.cancelarReserva(id);
        return "redirect:/admin/reservas";
    }
}