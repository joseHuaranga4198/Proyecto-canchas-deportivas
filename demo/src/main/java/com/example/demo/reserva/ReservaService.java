package com.example.demo.reserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaService {
    List<Reserva> listarTodas();
    List<Reserva> listarPorUsuario(Long usuarioId);
    Reserva obtenerPorId(Long id);
    boolean estaDisponible(Long canchaId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
    BigDecimal calcularMonto(Long canchaId, LocalTime horaInicio, LocalTime horaFin);
    Reserva registrarReserva(Long usuarioId, String clienteNombre, Long canchaId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
    void cancelarReserva(Long id);
}