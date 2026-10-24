package com.example.demo.reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.math.BigDecimal;

public interface ReservaDAO {

    List<Reserva> listarTodas();

    List<Reserva> listarPorUsuario(Long idUsuario);

    List<Reserva> listarPorUsuarioYEstado(Long idUsuario, String estado);

    Reserva obtenerPorId(Long idReserva);

    Long guardar(Reserva reserva);

    int cambiarEstado(Long idReserva, String estado);

    List<Reserva> buscarSuperpuestas(Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);

    List<Reserva> buscarAdministrativamente(Long idReserva, String cliente, String cancha, String estado,
            LocalDate fechaDesde, LocalDate fechaHasta);

    void bloquearCancha(Long idCancha);

    int registrarPago(Long idReserva, BigDecimal importe);
}
