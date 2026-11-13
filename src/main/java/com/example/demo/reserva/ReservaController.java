package com.example.demo.reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Clock;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.cancha.Cancha;
import com.example.demo.cancha.CanchaService;
import com.example.demo.usuario.Usuario;
import com.example.demo.operacion.OperacionService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final CanchaService canchaService;
    private final Clock clock;
    private final OperacionService operaciones;

    public ReservaController(ReservaService reservaService, CanchaService canchaService, Clock clock,
            OperacionService operaciones) {
        this.reservaService = reservaService;
        this.canchaService = canchaService;
        this.clock = clock;
        this.operaciones = operaciones;
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
        LocalDate fechaFinal = (fecha != null) ? fecha : LocalDate.now(clock);

        Cancha seleccionada = canchaService.obtenerPorId(idFinal);
        if (seleccionada == null || !"ACTIVA".equalsIgnoreCase(seleccionada.getEstado())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La cancha no está disponible.");
        }
        if (fechaFinal.isBefore(LocalDate.now(clock))) {
            model.addAttribute("error", "Selecciona una fecha desde hoy.");
            return "reserva/disponibilidad";
        }
        model.addAttribute("canchaSeleccionada", seleccionada);
        model.addAttribute("fechaSeleccionada", fechaFinal);
        model.addAttribute("turnos", reservaService.turnos(idFinal, fechaFinal));

        return "reserva/disponibilidad";
    }

    @GetMapping("/nueva")
    public String formularioReserva(@RequestParam(name = "canchaId", required = false) Long canchaId,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.TIME) LocalTime inicio,
            HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/usuario/login";
        if (canchaId == null) return "redirect:/canchas/catalogo";

        Cancha cancha = canchaService.obtenerPorId(canchaId);
        if (cancha == null || !"ACTIVA".equalsIgnoreCase(cancha.getEstado())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La cancha no está disponible.");
        }
        model.addAttribute("cancha", cancha);
        model.addAttribute("fechaMinima", LocalDate.now(clock));
        model.addAttribute("fechaElegida",fecha==null?LocalDate.now(clock):fecha);
        model.addAttribute("inicioElegido",inicio==null?LocalTime.of(8,0):inicio);
        model.addAttribute("finElegido",inicio==null?LocalTime.of(9,0):inicio.plusHours(1));
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
        if (cancha == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la cancha.");
        }

        session.removeAttribute("reservaBorrador");
        model.addAttribute("fechaElegida",fecha);model.addAttribute("inicioElegido",horaInicio);model.addAttribute("finElegido",horaFin);
        try {
            reservaService.validarSolicitud(user.getIdUsuario(), canchaId, fecha, horaInicio, horaFin);
            session.setAttribute("reservaBorrador", new Reserva.Borrador(user.getIdUsuario(), canchaId, fecha, horaInicio, horaFin));
            return "redirect:/reservas/pago";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("cancha", cancha);
            model.addAttribute("fechaMinima", LocalDate.now(clock));
            return "reserva/generar";
        }
    }

    @GetMapping("/pago")
    public String verPago(HttpSession session, Model model, RedirectAttributes redirect) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        Reserva.Borrador borrador = (Reserva.Borrador) session.getAttribute("reservaBorrador");
        if (usuario == null) return "redirect:/usuario/login";
        if (borrador == null || !borrador.getIdUsuario().equals(usuario.getIdUsuario())) return "redirect:/canchas/catalogo";
        try {
            reservaService.validarSolicitud(usuario.getIdUsuario(), borrador.getIdCancha(), borrador.getFecha(),
                    borrador.getHoraInicio(), borrador.getHoraFin());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            session.removeAttribute("reservaBorrador");
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/canchas/catalogo";
        }
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
    public String pagar(HttpSession session, Model model, RedirectAttributes redirect) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        Reserva.Borrador borrador = (Reserva.Borrador) session.getAttribute("reservaBorrador");
        if (usuario == null) return "redirect:/usuario/login";
        if (borrador == null || !borrador.getIdUsuario().equals(usuario.getIdUsuario())) return "redirect:/canchas/catalogo";
        try {
            Reserva reserva = reservaService.registrarReservaPagada(usuario.getIdUsuario(), borrador.getIdCancha(),
                    borrador.getFecha(), borrador.getHoraInicio(), borrador.getHoraFin());
            session.removeAttribute("reservaBorrador");
            return "redirect:/reservas/mis-reservas/" + reserva.getIdReserva();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            session.removeAttribute("reservaBorrador");
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/canchas/catalogo";
        }
    }

    @GetMapping("/mis-reservas")
    public String misReservas(@RequestParam(required = false) String estado, HttpSession session, Model model) {
        Usuario usuario = usuarioCliente(session);
        if (usuario == null) return "redirect:/usuario/login";
        List<Reserva> reservas = reservaService.listarCliente(usuario.getIdUsuario(), estado);
        Set<Long> cancelables = new HashSet<>();
        for (Reserva reserva : reservas) {
            if (reservaService.puedeCancelar(reserva)) cancelables.add(reserva.getIdReserva());
        }
        model.addAttribute("reservas", reservas);
        model.addAttribute("cancelables", cancelables);
        model.addAttribute("estado", estado);
        return "reserva/reservas";
    }

    @GetMapping("/mis-reservas/{id}")
    public String detalle(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirect) {
        Usuario usuario = usuarioCliente(session);
        if (usuario == null) return "redirect:/usuario/login";
        Reserva reserva = reservaService.detallePropio(id, usuario.getIdUsuario());
        if (reserva == null) {
            redirect.addFlashAttribute("error", "No se encontró una reserva propia con ese código.");
            return "redirect:/reservas/mis-reservas";
        }
        model.addAttribute("reserva", reserva);
        model.addAttribute("pago", reserva.getPago());
        model.addAttribute("fechaPagoTexto",fechaPago(reserva));
        model.addAttribute("devolucion", operaciones.devolucion(id));
        model.addAttribute("duracionMinutos", reservaService.duracionMinutos(reserva));
        model.addAttribute("puedeCancelar", reservaService.puedeCancelar(reserva));
        model.addAttribute("esAdmin", false);
        return "reserva/detalle";
    }

    @PostMapping("/mis-reservas/{id}/cancelar")
    public String cancelar(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        Usuario usuario = usuarioCliente(session);
        if (usuario == null) return "redirect:/usuario/login";
        try {
            reservaService.cancelarPropia(id, usuario.getIdUsuario());
            redirect.addFlashAttribute("mensaje", "La reserva fue cancelada y su devolución simulada fue registrada. El pago original se conserva.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reservas/mis-reservas";
    }

    @GetMapping("/admin")
    public String reservasAdmin(@RequestParam(required = false) Long idReserva,
            @RequestParam(required = false) String cliente, @RequestParam(required = false) String cancha,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/usuario/login";
        try {
            model.addAttribute("reservas", reservaService.buscarAdmin(idReserva, cliente, cancha, estado, fechaDesde, fechaHasta));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("reservas", List.of());
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("idReserva", idReserva);
        model.addAttribute("cliente", cliente);
        model.addAttribute("cancha", cancha);
        model.addAttribute("estado", estado);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        return "reserva/admin-reservas";
    }

    @GetMapping("/admin/{id}")
    public String detalleAdmin(@PathVariable Long id, HttpSession session, Model model,
            RedirectAttributes redirect) {
        if (!esAdmin(session)) return "redirect:/usuario/login";
        Reserva reserva = reservaService.detalleAdmin(id);
        if (reserva == null) {
            redirect.addFlashAttribute("error", "No se encontró la reserva.");
            return "redirect:/reservas/admin";
        }
        model.addAttribute("reserva", reserva);
        model.addAttribute("pago", reserva.getPago());
        model.addAttribute("fechaPagoTexto",fechaPago(reserva));
        model.addAttribute("devolucion", operaciones.devolucion(id));
        model.addAttribute("duracionMinutos", reservaService.duracionMinutos(reserva));
        model.addAttribute("puedeCancelar", reservaService.puedeCancelarAdmin(reserva));
        model.addAttribute("esAdmin", true);
        return "reserva/detalle";
    }

    @PostMapping("/admin/{id}/cancelar")
    public String cancelarAdmin(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        if (!esAdmin(session)) return "redirect:/usuario/login";
        try {
            reservaService.cancelarAdmin(id,((Usuario)session.getAttribute("usuarioLogueado")).getIdUsuario());
            redirect.addFlashAttribute("mensaje", "La reserva fue cancelada y su devolución simulada fue registrada. El pago original se conserva.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reservas/admin";
    }

    private Usuario usuarioCliente(HttpSession session) {
        Object value = session.getAttribute("usuarioLogueado");
        if (!(value instanceof Usuario)) return null;
        Usuario usuario = (Usuario) value;
        if (!"CLIENTE".equalsIgnoreCase(usuario.getRol())) return null;
        return usuario;
    }

    private String fechaPago(Reserva reserva){
        return reserva.getPago()==null||reserva.getPago().getFechaPago()==null?"No disponible":
                reserva.getPago().getFechaPago().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private boolean esAdmin(HttpSession session) {
        Object value = session.getAttribute("usuarioLogueado");
        if (!(value instanceof Usuario)) return false;
        Usuario usuario = (Usuario) value;
        return "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol());
    }

}
