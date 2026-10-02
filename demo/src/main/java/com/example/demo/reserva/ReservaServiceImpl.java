package com.example.demo.reserva;

import com.example.demo.cancha.Cancha;
import com.example.demo.cancha.CanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private CanchaService canchaService;

    private final List<Reserva> reservas = new ArrayList<>();

    public ReservaServiceImpl() {
        // Datos mock para verificar la interfaz mis-reservas
        reservas.add(new Reserva(1L, 1L, "Carlos Escobar", 1L, "Cancha Monumental 1",
                LocalDate.now().plusDays(1), LocalTime.of(18, 0), LocalTime.of(19, 0), new BigDecimal("60.00"), "CONFIRMADA"));
        reservas.add(new Reserva(2L, 1L, "Carlos Escobar", 2L, "Cancha Monumental 2",
                LocalDate.now().minusDays(2), LocalTime.of(20, 0), LocalTime.of(21, 0), new BigDecimal("50.00"), "CONFIRMADA"));
    }

    @Override
    public List<Reserva> listarTodas() {
        return reservas;
    }

    @Override
    public List<Reserva> listarPorUsuario(Long usuarioId) {
        return reservas.stream()
                .filter(r -> r.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    @Override
    public Reserva obtenerPorId(Long id) {
        return reservas.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean estaDisponible(Long canchaId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        return reservas.stream()
                .filter(r -> r.getCanchaId().equals(canchaId))
                .filter(r -> r.getFecha().equals(fecha))
                .filter(r -> !"CANCELADA".equalsIgnoreCase(r.getEstado()))
                .noneMatch(r -> (horaInicio.isBefore(r.getHoraFin()) && horaFin.isAfter(r.getHoraInicio())));
    }

    @Override
    public BigDecimal calcularMonto(Long canchaId, LocalTime horaInicio, LocalTime horaFin) {
        Cancha cancha = canchaService.obtenerPorId(canchaId);
        if (cancha == null) return BigDecimal.ZERO;

        long minutos = Duration.between(horaInicio, horaFin).toMinutes();
        double horas = (double) minutos / 60.0;
        return cancha.getPrecioHora().multiply(BigDecimal.valueOf(horas));
    }

    @Override
    public Reserva registrarReserva(Long usuarioId, String clienteNombre, Long canchaId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        Cancha cancha = canchaService.obtenerPorId(canchaId);
        if (cancha == null || !estaDisponible(canchaId, fecha, horaInicio, horaFin)) {
            return null;
        }

        BigDecimal total = calcularMonto(canchaId, horaInicio, horaFin);
        Reserva nueva = new Reserva(
                (long) (reservas.size() + 1),
                usuarioId,
                clienteNombre,
                canchaId,
                cancha.getNombre(),
                fecha,
                horaInicio,
                horaFin,
                total,
                "CONFIRMADA"
        );
        reservas.add(nueva);
        return nueva;
    }

    @Override
    public void cancelarReserva(Long id) {
        Reserva res = obtenerPorId(id);
        if (res != null) {
            res.setEstado("CANCELADA");
        }
    }
}