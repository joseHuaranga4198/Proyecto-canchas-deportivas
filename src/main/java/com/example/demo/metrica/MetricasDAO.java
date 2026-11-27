package com.example.demo.metrica;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public interface MetricasDAO {

    Map<LocalDate, BigDecimal> reservasRegistradasPorDia(LocalDate desde, LocalDate hasta, Long cancha);

    Map<LocalDate, BigDecimal> importePagadoPorDia(LocalDate desde, LocalDate hasta, Long cancha);

    Map<LocalDate, BigDecimal> horasReservadasPorDia(LocalDate desde, LocalDate hasta, Long cancha);

    Map<LocalDate, BigDecimal> importeDevueltoPorDia(LocalDate desde, LocalDate hasta, Long cancha);

    Map<LocalDate, BigDecimal> clientesConReservaPorDia(LocalDate desde, LocalDate hasta, Long cancha);

    BigDecimal reservasConfirmadasDelDia(LocalDate dia, Long cancha);

    BigDecimal horasConfirmadasDelDia(LocalDate dia, Long cancha);

    BigDecimal canchasHabilitadas(Long cancha);

    BigDecimal reservasPendientesDeUso(LocalDateTime ahora, Long cancha);

    BigDecimal saldoRetenido(Long cancha);

    BigDecimal mantenimientosProgramados(LocalDate dia, Long cancha);
}
