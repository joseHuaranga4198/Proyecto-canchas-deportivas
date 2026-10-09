package com.example.demo.reserva;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
        double horas = (double) minutos / 60.0;
        return c.getPrecioHora().multiply(BigDecimal.valueOf(horas));
    }

    @Override
    public Reserva registrarReserva(Long idUsuario, Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        Usuario u = usuarioDAO.obtenerPorId(idUsuario);
        Cancha c = canchaDAO.obtenerPorId(idCancha);

        // RN02: Validación cliente bloqueado
        if (u == null || "BLOQUEADO".equalsIgnoreCase(u.getEstado())) {
            throw new IllegalStateException("El cliente está bloqueado o no existe.");
        }

        // RN03: Validación cancha activa
        if (c == null || !"ACTIVA".equalsIgnoreCase(c.getEstado())) {
            throw new IllegalStateException("La cancha se encuentra inactiva.");
        }

        // RN01: Validación exclusividad horaria
        if (!estaDisponible(idCancha, fecha, horaInicio, horaFin)) {
            throw new IllegalStateException("El horario solicitado ya se encuentra ocupado.");
        }

        // RN04: Cálculo exacto
        BigDecimal total = calcularMonto(idCancha, horaInicio, horaFin);

        Reserva nueva = new Reserva();
        nueva.setUsuario(u);
        nueva.setCancha(c);
        nueva.setFecha(fecha);
        nueva.setHoraInicio(horaInicio);
        nueva.setHoraFin(horaFin);
        nueva.setImporte(total);
        nueva.setEstado("PENDIENTE"); // RN05: Nace pendiente de pago

        Long idGenerado = reservaDAO.guardar(nueva);
        nueva.setIdReserva(idGenerado);
        return nueva;
    }

    @Override
    public boolean confirmarPago(Long idReserva) {
        Reserva r = reservaDAO.obtenerPorId(idReserva);
        if (r == null || !"PENDIENTE".equalsIgnoreCase(r.getEstado())) {
            return false;
        }

        // RN05: Inserta registro en la tabla pago y confirma
        reservaDAO.registrarPago(idReserva, r.getImporte());
        reservaDAO.cambiarEstado(idReserva, "CONFIRMADA");
        return true;
    }

    @Override
    public void cancelarReserva(Long idReserva) {
        reservaDAO.cambiarEstado(idReserva, "CANCELADA");
    }
}