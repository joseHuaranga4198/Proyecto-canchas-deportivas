package com.example.demo.reserva;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

import com.example.demo.cancha.Cancha;
import com.example.demo.cancha.CanchaDAO;
import com.example.demo.usuario.Usuario;
import com.example.demo.usuario.UsuarioDAO;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaDAO reservaDAO;
    private final CanchaDAO canchaDAO;
    private final UsuarioDAO usuarioDAO;

    public ReservaServiceImpl(ReservaDAO reservaDAO, CanchaDAO canchaDAO, UsuarioDAO usuarioDAO) {
        this.reservaDAO = reservaDAO;
        this.canchaDAO = canchaDAO;
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public List<Reserva> listarTodas() {
        return reservaDAO.listarTodas();
    }

    @Override
    public List<Reserva> listarPorUsuario(Long idUsuario) {
        return reservaDAO.listarPorUsuario(idUsuario);
    }

    @Override
    public Reserva obtenerPorId(Long idReserva) {
        return reservaDAO.obtenerPorId(idReserva);
    }

    @Override
    public boolean estaDisponible(Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        return reservaDAO.buscarSuperpuestas(idCancha, fecha, horaInicio, horaFin).isEmpty();
    }

    @Override
    public BigDecimal calcularMonto(Long idCancha, LocalTime horaInicio, LocalTime horaFin) {
        Cancha c = canchaDAO.obtenerPorId(idCancha);
        if (c == null) return BigDecimal.ZERO;

        long minutos = Duration.between(horaInicio, horaFin).toMinutes();
        return c.getPrecioHora().multiply(BigDecimal.valueOf(minutos))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    @Override
    public Reserva registrarReserva(Long idUsuario, Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        throw new UnsupportedOperationException(
                "No se guardan reservas antes del pago; usa el flujo de borrador y pago de ReservaController.");
    }

    @Override
    public boolean confirmarPago(Long idReserva) {
        return false;
    }

    @Override
    public void cancelarReserva(Long idReserva) {
        reservaDAO.cambiarEstado(idReserva, "CANCELADA");
    }
}
