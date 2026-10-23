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
    private final ReservaF1316Service flujoPagoService;

    public ReservaController(ReservaService reservaService, CanchaService canchaService, ReservaF1316Service flujoPagoService) {
        this.reservaService = reservaService;
        this.canchaService = canchaService;
        this.flujoPagoService = flujoPagoService;
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
            if (fecha.isBefore(LocalDate.now())) throw new IllegalArgumentException("La fecha no puede estar en el pasado.");
            session.setAttribute("reservaBorrador", new ReservaBorrador(user.getIdUsuario(), canchaId, fecha, horaInicio, horaFin));
            return "redirect:/reservas/pago";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("cancha", cancha);
            model.addAttribute("fechaMinima", LocalDate.now());
            return "reserva/generar";
        }
    }

    @GetMapping("/pago")
    public String verPago(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        ReservaBorrador borrador = (ReservaBorrador) session.getAttribute("reservaBorrador");
        if (usuario == null) return "redirect:/usuario/login";
        if (borrador == null || !borrador.getIdUsuario().equals(usuario.getIdUsuario())) return "redirect:/canchas/catalogo";
        Cancha cancha = canchaService.obtenerPorId(borrador.getIdCancha());
        Reserva resumen = new Reserva();
        resumen.setCancha(cancha);
        resumen.setFecha(borrador.getFecha());
        resumen.setHoraInicio(borrador.getHoraInicio());
        resumen.setHoraFin(borrador.getHoraFin());
        resumen.setImporte(reservaService.calcularMonto(borrador.getIdCancha(), borrador.getHoraInicio(), borrador.getHoraFin()));
        model.addAttribute("reserva", resumen);
        return "reserva/pago";
    }

    @PostMapping("/pago/procesar")
    public String pagar(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        ReservaBorrador borrador = (ReservaBorrador) session.getAttribute("reservaBorrador");
        if (usuario == null) return "redirect:/usuario/login";
        if (borrador == null || !borrador.getIdUsuario().equals(usuario.getIdUsuario())) return "redirect:/canchas/catalogo";
        try {
            Reserva reserva = flujoPagoService.registrarReservaPagada(usuario.getIdUsuario(), borrador.getIdCancha(),
                    borrador.getFecha(), borrador.getHoraInicio(), borrador.getHoraFin());
            session.removeAttribute("reservaBorrador");
            return "redirect:/reservas/confirmacion/" + reserva.getIdReserva();
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            Cancha cancha = canchaService.obtenerPorId(borrador.getIdCancha());
            Reserva resumen = new Reserva();
            resumen.setCancha(cancha);
            resumen.setFecha(borrador.getFecha());
            resumen.setHoraInicio(borrador.getHoraInicio());
            resumen.setHoraFin(borrador.getHoraFin());
            resumen.setImporte(reservaService.calcularMonto(borrador.getIdCancha(), borrador.getHoraInicio(), borrador.getHoraFin()));
            model.addAttribute("reserva", resumen);
            return "reserva/pago";
        }
    }

    @GetMapping("/confirmacion/{id}")
    public String confirmacion(@PathVariable("id") Long id, Model model) {
        return "redirect:/f1316/reservas/" + id;
    }

    @GetMapping("/mis-reservas")
    public String misReservas(HttpSession session, Model model) {
        return "redirect:/f1316/reservas";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable("id") Long id) {
        return "redirect:/f1316/reservas/" + id;
    }
}
