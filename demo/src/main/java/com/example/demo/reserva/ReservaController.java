package com.example.demo.reserva;

import com.example.demo.cancha.Cancha;
import com.example.demo.cancha.CanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private CanchaService canchaService;

    // I04: Consultar disponibilidad
    @GetMapping("/disponibilidad")
    public String verDisponibilidad(Model model) {
        model.addAttribute("canchas", canchaService.listarDisponibles());
        return "reserva/disponibilidad";
    }

    // I05: Formulario para generar reserva
    @GetMapping("/nueva")
    public String formularioReserva(@RequestParam("canchaId") Long canchaId, Model model) {
        Cancha cancha = canchaService.obtenerPorId(canchaId);
        if (cancha == null) {
            return "redirect:/canchas/catalogo";
        }
        model.addAttribute("cancha", cancha);
        model.addAttribute("fechaMinima", LocalDate.now());
        return "reserva/generar";
    }

    // Procesar reserva (sin JavaScript, todo validado por POST MVC)
    @PostMapping("/guardar")
    public String procesarReserva(
            @RequestParam("canchaId") Long canchaId,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("horaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam("horaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            Model model) {

        Cancha cancha = canchaService.obtenerPorId(canchaId);

        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            model.addAttribute("error", "La hora de fin debe ser posterior a la hora de inicio.");
            model.addAttribute("cancha", cancha);
            model.addAttribute("fechaMinima", LocalDate.now());
            return "reserva/generar";
        }

        boolean disponible = reservaService.estaDisponible(canchaId, fecha, horaInicio, horaFin);
        if (!disponible) {
            model.addAttribute("error", "El horario seleccionado ya se encuentra ocupado.");
            model.addAttribute("cancha", cancha);
            model.addAttribute("fechaMinima", LocalDate.now());
            return "reserva/generar";
        }

        // Usuario mock (ID = 1, Carlos Escobar)
        Reserva confirmada = reservaService.registrarReserva(1L, "Carlos Escobar", canchaId, fecha, horaInicio, horaFin);
        return "redirect:/reservas/confirmacion/" + confirmada.getId();
    }

    // I19: Confirmación de reserva
    @GetMapping("/confirmacion/{id}")
    public String confirmacion(@PathVariable Long id, Model model) {
        Reserva res = reservaService.obtenerPorId(id);
        if (res == null) return "redirect:/reservas/mis-reservas";
        model.addAttribute("reserva", res);
        return "reserva/confirmacion";
    }

    // I07: Mis Reservas (Historial del cliente)
    @GetMapping("/mis-reservas")
    public String misReservas(Model model) {
        model.addAttribute("reservas", reservaService.listarPorUsuario(1L));
        return "reserva/mis-reservas";
    }

    // I08: Cancelar reserva
    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return "redirect:/reservas/mis-reservas";
    }
}