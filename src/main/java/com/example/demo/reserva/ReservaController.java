package com.example.demo.reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.cancha.Cancha;
import com.example.demo.cancha.CanchaService;
import com.example.demo.usuario.Usuario;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final CanchaService canchaService;

    public ReservaController(ReservaService reservaService, CanchaService canchaService) {
        this.reservaService = reservaService;
        this.canchaService = canchaService;
    }

    @GetMapping("/disponibilidad")
    public String verDisponibilidad(
            @RequestParam(name = "canchaId", required = false) Long canchaId,
            @RequestParam(name = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {

        List<Cancha> canchas = canchaService.listarDisponibles();
        model.addAttribute("canchas", canchas);

        if (canchas.isEmpty()) {
            return "reserva/disponibilidad";
        }

        Long idFinal = (canchaId != null) ? canchaId : canchas.get(0).getIdCancha();
        LocalDate fechaFinal = (fecha != null) ? fecha : LocalDate.now();

        Cancha seleccionada = canchaService.obtenerPorId(idFinal);
        List<Reserva> ocupadas = reservaService.listarTodas().stream()
                .filter(r -> r.getCancha().getIdCancha().equals(idFinal))
                .filter(r -> r.getFecha().equals(fechaFinal))
                .filter(r -> !"CANCELADA".equalsIgnoreCase(r.getEstado()))
                .toList();

        model.addAttribute("canchaSeleccionada", seleccionada);
        model.addAttribute("fechaSeleccionada", fechaFinal);
        model.addAttribute("reservasOcupadas", ocupadas);

        return "reserva/disponibilidad";
    }

    @GetMapping("/nueva")
    public String formularioReserva(@RequestParam(name = "canchaId", required = false) Long canchaId,
                                    HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/usuario/login";
        if (canchaId == null) return "redirect:/canchas/catalogo";

        Cancha cancha = canchaService.obtenerPorId(canchaId);
        model.addAttribute("cancha", cancha);
        model.addAttribute("fechaMinima", LocalDate.now());
        return "reserva/generar";
    }

    @PostMapping("/guardar")
    public String procesarReserva(
            @RequestParam("canchaId") Long canchaId,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("horaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam("horaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            HttpSession session, Model model) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/usuario/login";

        Cancha cancha = canchaService.obtenerPorId(canchaId);

        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            model.addAttribute("error", "La hora de fin debe ser posterior a la de inicio.");
            model.addAttribute("cancha", cancha);
            model.addAttribute("fechaMinima", LocalDate.now());
            return "reserva/generar";
        }

        try {
            Reserva nueva = reservaService.registrarReserva(user.getIdUsuario(), canchaId, fecha, horaInicio, horaFin);
            return "redirect:/reservas/pago/" + nueva.getIdReserva();
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("cancha", cancha);
            model.addAttribute("fechaMinima", LocalDate.now());
            return "reserva/generar";
        }
    }

    @GetMapping("/pago/{id}")
    public String verPago(@PathVariable("id") Long id, Model model) {
        Reserva r = reservaService.obtenerPorId(id);
        if (r == null) return "redirect:/canchas/catalogo";
        model.addAttribute("reserva", r);
        return "reserva/pago";
    }

    @PostMapping("/pago/procesar/{id}")
    public String pagar(@PathVariable("id") Long id) {
        reservaService.confirmarPago(id);
        return "redirect:/reservas/confirmacion/" + id;
    }

    @GetMapping("/confirmacion/{id}")
    public String confirmacion(@PathVariable("id") Long id, Model model) {
        Reserva r = reservaService.obtenerPorId(id);
        model.addAttribute("reserva", r);
        return "reserva/confirmacion";
    }

    @GetMapping("/mis-reservas")
    public String misReservas(HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/usuario/login";

        model.addAttribute("reservas", reservaService.listarPorUsuario(user.getIdUsuario()));
        return "reserva/mis-reservas";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable("id") Long id) {
        reservaService.cancelarReserva(id);
        return "redirect:/reservas/mis-reservas";
    }
}