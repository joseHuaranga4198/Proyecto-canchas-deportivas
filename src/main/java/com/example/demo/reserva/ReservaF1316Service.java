package com.example.demo.reserva;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.cancha.Cancha;
import com.example.demo.cancha.CanchaDAO;
import com.example.demo.usuario.Usuario;
import com.example.demo.usuario.UsuarioDAO;

@Service
public class ReservaF1316Service {
    private final ReservaDAO reservas;
    private final CanchaDAO canchas;
    private final UsuarioDAO usuarios;
    private final Clock clock;

    public ReservaF1316Service(ReservaDAO reservas, CanchaDAO canchas, UsuarioDAO usuarios, Clock clock) {
        this.reservas = reservas;
        this.canchas = canchas;
        this.usuarios = usuarios;
        this.clock = clock;
    }

    public List<Reserva> listarCliente(Long idUsuario, String estado) {
        if (estado == null || estado.isBlank()) return reservas.listarPorUsuario(idUsuario);
        return reservas.listarPorUsuarioYEstado(idUsuario, estado.trim());
    }

    public Reserva detallePropio(Long idReserva, Long idUsuario) {
        Reserva reserva = reservas.obtenerPorId(idReserva);
        if (reserva == null || !reserva.getUsuario().getIdUsuario().equals(idUsuario)) return null;
        return reserva;
    }

    public long duracionMinutos(Reserva reserva) {
        return Duration.between(reserva.getHoraInicio(), reserva.getHoraFin()).toMinutes();
    }

    public boolean estaDisponible(Long idCancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        return reservas.buscarSuperpuestas(idCancha, fecha, inicio, fin).isEmpty();
    }

    public boolean puedeCancelar(Reserva reserva) {
        if (!"CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) return false;
        LocalDateTime inicio = LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio());
        return Duration.between(LocalDateTime.now(clock), inicio).compareTo(Duration.ofHours(24)) >= 0;
    }

    public List<Reserva> buscarAdmin(Long idReserva, String cliente, String cancha, String estado,
            LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta.");
        }
        return reservas.buscarAdministrativamente(idReserva, vacioANull(cliente), vacioANull(cancha),
                vacioANull(estado), fechaDesde, fechaHasta);
    }

    private String vacioANull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    @Transactional
    public Reserva registrarReservaPagada(Long idUsuario, Long idCancha, LocalDate fecha,
            LocalTime inicio, LocalTime fin) {
        if (fecha == null || inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La fecha y el horario de la reserva no son válidos.");
        }
        if (!LocalDateTime.of(fecha, inicio).isAfter(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("La fecha y hora de inicio deben estar en el futuro.");
        }

        // Bloquea la cancha hasta terminar la transacción para serializar reservas simultáneas.
        reservas.bloquearCancha(idCancha);
        Usuario usuario = usuarios.obtenerPorId(idUsuario);
        if (usuario == null) throw new IllegalStateException("El usuario no existe.");
        if (!"CLIENTE".equalsIgnoreCase(usuario.getRol()) || !"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            throw new IllegalStateException("El cliente está bloqueado o inactivo.");
        }
        Cancha cancha = canchas.obtenerPorId(idCancha);
        if (cancha == null || !"ACTIVA".equalsIgnoreCase(cancha.getEstado())) {
            throw new IllegalStateException("La cancha se encuentra inactiva.");
        }
        if (!estaDisponible(idCancha, fecha, inicio, fin)) {
            throw new IllegalStateException("El horario solicitado ya se encuentra ocupado.");
        }

        long minutos = Duration.between(inicio, fin).toMinutes();
        BigDecimal importe = cancha.getPrecioHora().multiply(BigDecimal.valueOf(minutos))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(inicio);
        reserva.setHoraFin(fin);
        reserva.setImporte(importe);
        reserva.setEstado("CONFIRMADA");
        Long idGenerado = reservas.guardar(reserva);
        if (idGenerado == null) throw new IllegalStateException("No se pudo registrar la reserva.");
        reservas.registrarPago(idGenerado, importe);
        return reservas.obtenerPorId(idGenerado);
    }

    @Transactional
    public void cancelarPropia(Long idReserva, Long idUsuario) {
        Reserva reserva = reservas.obtenerPorId(idReserva);
        if (reserva == null) throw new IllegalArgumentException("No se encontró la reserva.");
        if (!reserva.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("No puedes cancelar una reserva de otro cliente.");
        }
        if (!"CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) {
            throw new IllegalStateException("La reserva no está en estado cancelable.");
        }
        if (!puedeCancelar(reserva)) {
            throw new IllegalStateException("Solo puedes cancelar hasta 24 horas antes del inicio.");
        }
        reservas.cambiarEstado(idReserva, "CANCELADA");
    }
}
