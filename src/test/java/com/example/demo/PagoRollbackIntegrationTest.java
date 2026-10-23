package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.reserva.Pago;
import com.example.demo.reserva.PagoJpaRepository;
import com.example.demo.reserva.ReservaF1316Service;

@SpringBootTest
class PagoRollbackIntegrationTest {
    @Autowired private ReservaF1316Service service;
    @Autowired private JdbcTemplate jdbc;
    @SpyBean private PagoJpaRepository pagos;

    @AfterEach
    void limpiarEspia() {
        reset(pagos);
    }

    @Test
    void errorAlGuardarPagoRevierteLaReservaInsertada() {
        Integer reservasAntes = jdbc.queryForObject("select count(*) from reserva", Integer.class);
        Integer pagosAntes = jdbc.queryForObject("select count(*) from pago", Integer.class);
        doThrow(new DataIntegrityViolationException("Fallo controlado del pago"))
                .when(pagos).saveAndFlush(any(Pago.class));

        assertThatThrownBy(() -> service.registrarReservaPagada(2L, 1L,
                LocalDate.of(2030, 3, 3), LocalTime.NOON, LocalTime.of(13, 0)))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(jdbc.queryForObject("select count(*) from reserva", Integer.class)).isEqualTo(reservasAntes);
        assertThat(jdbc.queryForObject("select count(*) from pago", Integer.class)).isEqualTo(pagosAntes);
    }

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2030-03-01T17:00:00Z"), ZoneId.of("America/Lima"));
        }
    }
}
