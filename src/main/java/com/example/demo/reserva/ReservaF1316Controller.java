package com.example.demo.reserva;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.usuario.Usuario;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/f1316")
public class ReservaF1316Controller {
    private final ReservaF1316Service service;

    public ReservaF1316Controller(ReservaF1316Service service) {
        this.service = service;
    }

    @GetMapping("/reservas")
    public String misReservas(@RequestParam(required = false) String estado, HttpSession session, Model model) {
        Usuario usuario = usuarioCliente(session);
        if (usuario == null) return "redirect:/usuario/login";
        java.util.List<Reserva> reservas = service.listarCliente(usuario.getIdUsuario(), estado);
        Set<Long> cancelables = new HashSet<>();
        for (Reserva reserva : reservas) {
            if (service.puedeCancelar(reserva)) cancelables.add(reserva.getIdReserva());
        }
        model.addAttribute("reservas", reservas);
        model.addAttribute("cancelables", cancelables);
        model.addAttribute("estado", estado);
        return "f1316/reservas";
    }

    @GetMapping("/reservas/{id}")
    public String detalle(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirect) {
        Usuario usuario = usuarioCliente(session);
        if (usuario == null) return "redirect:/usuario/login";
        Reserva reserva = service.detallePropio(id, usuario.getIdUsuario());
        if (reserva == null) {
            redirect.addFlashAttribute("error", "No se encontró una reserva propia con ese código.");
            return "redirect:/f1316/reservas";
        }
        model.addAttribute("reserva", reserva);
        model.addAttribute("pago", reserva.getPago());
        model.addAttribute("duracionMinutos", service.duracionMinutos(reserva));
        model.addAttribute("puedeCancelar", service.puedeCancelar(reserva));
        model.addAttribute("esAdmin", false);
        return "f1316/detalle";
    }

    @PostMapping("/reservas/{id}/cancelar")
    public String cancelar(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        Usuario usuario = usuarioCliente(session);
        if (usuario == null) return "redirect:/usuario/login";
        try {
            service.cancelarPropia(id, usuario.getIdUsuario());
            redirect.addFlashAttribute("mensaje", "La reserva fue cancelada. El pago permanece en el historial.");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/f1316/reservas";
    }

    @GetMapping("/admin/reservas")
    public String reservasAdmin(@RequestParam(required = false) Long idReserva,
            @RequestParam(required = false) String cliente, @RequestParam(required = false) String cancha,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            HttpSession session, Model model, RedirectAttributes redirect) {
        if (!esAdmin(session)) return "redirect:/usuario/login";
        try {
            model.addAttribute("reservas", service.buscarAdmin(idReserva, cliente, cancha, estado, fechaDesde, fechaHasta));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("reservas", java.util.List.of());
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("idReserva", idReserva);
        model.addAttribute("cliente", cliente);
        model.addAttribute("cancha", cancha);
        model.addAttribute("estado", estado);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("filtros", new ReservaFiltros(idReserva, cliente, cancha, estado, fechaDesde, fechaHasta));
        return "f1316/admin-reservas";
    }

    @GetMapping("/admin/reservas/{id}")
    public String detalleAdmin(@PathVariable Long id, HttpSession session, Model model,
            RedirectAttributes redirect) {
        if (!esAdmin(session)) return "redirect:/usuario/login";
        Reserva reserva = service.detalleAdmin(id);
        if (reserva == null) {
            redirect.addFlashAttribute("error", "No se encontró la reserva.");
            return "redirect:/f1316/admin/reservas";
        }
        model.addAttribute("reserva", reserva);
        model.addAttribute("pago", reserva.getPago());
        model.addAttribute("duracionMinutos", service.duracionMinutos(reserva));
        model.addAttribute("puedeCancelar", service.puedeCancelarAdmin(reserva));
        model.addAttribute("esAdmin", true);
        return "f1316/detalle";
    }

    @PostMapping("/admin/reservas/{id}/cancelar")
    public String cancelarAdmin(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        if (!esAdmin(session)) return "redirect:/usuario/login";
        try {
            service.cancelarAdmin(id);
            redirect.addFlashAttribute("mensaje", "La reserva fue cancelada. El pago permanece en el historial.");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/f1316/admin/reservas";
    }

    private Usuario usuarioCliente(HttpSession session) {
        Object value = session.getAttribute("usuarioLogueado");
        if (!(value instanceof Usuario)) return null;
        Usuario usuario = (Usuario) value;
        if (!"CLIENTE".equalsIgnoreCase(usuario.getRol())) return null;
        return usuario;
    }

    private boolean esAdmin(HttpSession session) {
        Object value = session.getAttribute("usuarioLogueado");
        if (!(value instanceof Usuario)) return false;
        Usuario usuario = (Usuario) value;
        return "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol());
    }

    public static class ReservaFiltros {
        private final Long idReserva;
        private final String cliente;
        private final String cancha;
        private final String estado;
        private final LocalDate fechaDesde;
        private final LocalDate fechaHasta;

        public ReservaFiltros(Long idReserva, String cliente, String cancha, String estado,
                LocalDate fechaDesde, LocalDate fechaHasta) {
            this.idReserva = idReserva;
            this.cliente = cliente;
            this.cancha = cancha;
            this.estado = estado;
            this.fechaDesde = fechaDesde;
            this.fechaHasta = fechaHasta;
        }

        public Long getIdReserva() { return idReserva; }
        public String getCliente() { return cliente; }
        public String getCancha() { return cancha; }
        public String getEstado() { return estado; }
        public LocalDate getFechaDesde() { return fechaDesde; }
        public LocalDate getFechaHasta() { return fechaHasta; }
    }
}
