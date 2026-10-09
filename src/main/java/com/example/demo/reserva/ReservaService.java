package com.example.demo.reserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaService {

    List<Reserva> listarTodas();

    List<Reserva> listarPorUsuario(Long idUsuario);

    Reserva obtenerPorId(Long idReserva);

    boolean estaDisponible(Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);

    BigDecimal calcularMonto(Long idCancha, LocalTime horaInicio, LocalTime horaFin);

    Reserva registrarReserva(Long idUsuario, Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);

    boolean confirmarPago(Long idReserva);

    void cancelarReserva(Long idReserva);
}