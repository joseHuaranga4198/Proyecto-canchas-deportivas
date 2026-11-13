package com.example.demo.reserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface ReservaService {

    List<Reserva> listarTodas();

    List<Reserva> listarPorUsuario(Long idUsuario);

    Reserva obtenerPorId(Long idReserva);

    boolean estaDisponible(Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);

    BigDecimal calcularMonto(Long idCancha, LocalTime horaInicio, LocalTime horaFin);

    List<Reserva> listarCliente(Long idUsuario, String estado);
    Reserva detallePropio(Long idReserva, Long idUsuario);
    Reserva detalleAdmin(Long idReserva);
    long duracionMinutos(Reserva reserva);
    boolean puedeCancelar(Reserva reserva);
    boolean puedeCancelarAdmin(Reserva reserva);
    List<Reserva> buscarAdmin(Long idReserva, String cliente, String cancha, String estado,
            LocalDate fechaDesde, LocalDate fechaHasta);
    Reserva registrarReservaPagada(Long idUsuario, Long idCancha, LocalDate fecha, LocalTime inicio, LocalTime fin);
    void cancelarPropia(Long idReserva, Long idUsuario);
    void cancelarAdmin(Long idReserva, Long idAdmin);
    void validarSolicitud(Long idUsuario, Long idCancha, LocalDate fecha, LocalTime inicio, LocalTime fin);
    void validarIntervaloFuturo(LocalDate fecha, LocalTime inicio, LocalTime fin);
    List<Map<String, Object>> turnos(Long idCancha, LocalDate fecha);
}
