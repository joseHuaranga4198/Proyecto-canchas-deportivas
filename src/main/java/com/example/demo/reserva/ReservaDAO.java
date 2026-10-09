package com.example.demo.reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaDAO {

    List<Reserva> listarTodas();

    List<Reserva> listarPorUsuario(Long idUsuario);

    Reserva obtenerPorId(Long idReserva);

    Long guardar(Reserva reserva);

    int cambiarEstado(Long idReserva, String estado);

    List<Reserva> buscarSuperpuestas(Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);

    int registrarPago(Long idReserva, java.math.BigDecimal importe);
}