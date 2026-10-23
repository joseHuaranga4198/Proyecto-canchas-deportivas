package com.example.demo.reserva;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.cancha.Cancha;
import com.example.demo.cancha.CanchaJpaRepository;
import com.example.demo.usuario.Usuario;
import com.example.demo.usuario.UsuarioJpaRepository;

@Service
public class ReservaF1316Service {
    private final ReservaJpaRepository reservas;
    private final PagoJpaRepository pagos;
    private final CanchaJpaRepository canchas;
    private final UsuarioJpaRepository usuarios;
    private final Clock clock;

    public ReservaF1316Service(ReservaJpaRepository reservas, PagoJpaRepository pagos,
            CanchaJpaRepository canchas, UsuarioJpaRepository usuarios, Clock clock) {
        this.reservas = reservas;
        this.pagos = pagos;
        this.canchas = canchas;
        this.usuarios = usuarios;
        this.clock = clock;
    }

    public List<Reserva> listarCliente(Long idUsuario, String estado) {
        if (estado == null || estado.isBlank()) {
            return reservas.findAllByUsuario_IdUsuarioOrderByFechaDescHoraInicioDesc(idUsuario);
        }
        return reservas.findByUsuario_IdUsuarioAndEstadoIgnoreCaseOrderByFechaDescHoraInicioDesc(idUsuario, estado.trim());
    }

    public Reserva detallePropio(Long idReserva, Long idUsuario) {
        Optional<Reserva> resultado = reservas.findDetailById(idReserva);
        if (resultado.isEmpty()) return null;
        Reserva reserva = resultado.get();
        if (!reserva.getUsuario().getIdUsuario().equals(idUsuario)) return null;
        return reserva;
    }

    public long duracionMinutos(Reserva reserva) {
        return Duration.between(reserva.getHoraInicio(), reserva.getHoraFin()).toMinutes();
    }

    public boolean estaDisponible(Long idCancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        return !reservas.existsOverlapping(idCancha, fecha, inicio, fin);
    }

    public boolean puedeCancelar(Reserva reserva) {
        if (!"CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) return false;
        LocalDateTime inicio = LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio());
        return Duration.between(LocalDateTime.now(clock), inicio).compareTo(Duration.ofHours(24)) >= 0;
    }

    public List<Reserva> buscarAdmin(Long id, String cliente, String cancha, String estado,
            LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta.");
        }
        return reservas.buscarAdministrativamente(id, vacioANull(cliente), vacioANull(cancha),
                vacioANull(estado), desde, hasta);
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
        Cancha cancha = canchas.findForReservation(idCancha)
                .orElseThrow(() -> new IllegalStateException("La cancha ya no está disponible."));
        Usuario usuario = usuarios.findById(idUsuario)
                .orElseThrow(() -> new IllegalStateException("El usuario no existe."));
        if (!"CLIENTE".equalsIgnoreCase(usuario.getRol()) || !"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            throw new IllegalStateException("El cliente está bloqueado o inactivo.");
        }
        if (!"ACTIVA".equalsIgnoreCase(cancha.getEstado())) {
            throw new IllegalStateException("La cancha se encuentra inactiva.");
        }
        if (reservas.existsOverlapping(idCancha, fecha, inicio, fin)) {
            throw new IllegalStateException("El horario solicitado ya se encuentra ocupado.");
        }
        if (!LocalDateTime.of(fecha, inicio).isAfter(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("La fecha y hora de inicio deben estar en el futuro.");
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
        reserva = reservas.saveAndFlush(reserva);

        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setImporte(importe);
        pago.setEstado("REGISTRADO");
        pagos.saveAndFlush(pago);
        reserva.setPago(pago);
        return reserva;
    }

    @Transactional
    public void cancelarPropia(Long idReserva, Long idUsuario) {
        Reserva reserva = reservas.findDetailById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la reserva."));
        if (!reserva.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("No puedes cancelar una reserva de otro cliente.");
        }
        if (!"CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) {
            throw new IllegalStateException("La reserva no está en estado cancelable.");
        }
        if (!puedeCancelar(reserva)) {
            throw new IllegalStateException("Solo puedes cancelar hasta 24 horas antes del inicio.");
        }
        reserva.setEstado("CANCELADA");
        reservas.save(reserva);
    }
}
