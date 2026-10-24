package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.reserva.Reserva;
import com.example.demo.reserva.ReservaF1316Service;
import com.example.demo.usuario.Usuario;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReservaF1316IntegrationTest {
    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    @Autowired private ReservaF1316Service service;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private MockMvc mvc;

    @Test
    void guardaReservaSoloJuntoConPago() {
        int pagosAntes = contar("pago");
        int reservasAntes = contar("reserva");

        Reserva reserva = crearReserva(LocalDate.of(2030, 3, 3), LocalTime.NOON);

        assertThat(reserva.getEstado()).isEqualTo("CONFIRMADA");
        assertThat(contar("reserva")).isEqualTo(reservasAntes + 1);
        assertThat(contar("pago")).isEqualTo(pagosAntes + 1);
        assertThat(reserva.getPago().getEstado()).isEqualTo("REGISTRADO");
        assertThat(reserva.getImporte()).isEqualByComparingTo("50.00");
    }

    @Test
    void permiteCancelarEnElLimiteExactoYConservaPago() {
        Reserva reserva = crearReserva(LocalDate.of(2030, 3, 2), LocalTime.NOON);
        assertThat(service.puedeCancelar(reserva)).isTrue();

        service.cancelarPropia(reserva.getIdReserva(), 2L);

        Reserva guardada = service.detallePropio(reserva.getIdReserva(), 2L);
        assertThat(guardada.getEstado()).isEqualTo("CANCELADA");
        assertThat(guardada.getPago()).isNotNull();
        assertThat(guardada.getPago().getEstado()).isEqualTo("REGISTRADO");
        assertThat(service.estaDisponible(reserva.getCancha().getIdCancha(), reserva.getFecha(),
                reserva.getHoraInicio(), reserva.getHoraFin())).isTrue();
    }

    @Test
    void rechazaCancelacionConMenosDe24Horas() {
        Reserva reserva = crearReserva(LocalDate.of(2030, 3, 2), LocalTime.of(11, 59));
        assertThat(service.puedeCancelar(reserva)).isFalse();
        assertThatThrownBy(() -> service.cancelarPropia(reserva.getIdReserva(), 2L))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("24 horas");
    }

    @Test
    void impideConsultarOCancelarReservaAjena() {
        Reserva reserva = crearReserva(LocalDate.of(2030, 3, 3), LocalTime.NOON);
        assertThat(service.detallePropio(reserva.getIdReserva(), 3L)).isNull();
        assertThatThrownBy(() -> service.cancelarPropia(reserva.getIdReserva(), 3L))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("otro cliente");
    }

    @Test
    void filtrosAdministrativosPorClienteEstadoYFechas() {
        crearReserva(LocalDate.of(2030, 3, 3), LocalTime.NOON);
        assertThat(service.buscarAdmin(null, "carlos", "sintética 1", "CONFIRMADA",
                LocalDate.of(2030, 3, 3), LocalDate.of(2030, 3, 3))).hasSize(1);
        assertThat(service.buscarAdmin(null, "juan", null, null, null, null)).isEmpty();
    }

    @Test
    void requiereSesionYRolAdminParaSusRutas() throws Exception {
        mvc.perform(get("/f1316/reservas")).andExpect(status().is3xxRedirection());
        Usuario cliente = usuarioSesion(2L, "CLIENTE");
        mvc.perform(get("/f1316/admin/reservas").sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().is3xxRedirection());
        Usuario admin = usuarioSesion(1L, "ADMINISTRADOR");
        mvc.perform(get("/f1316/admin/reservas").sessionAttr("usuarioLogueado", admin))
                .andExpect(status().isOk()).andExpect(view().name("f1316/admin-reservas"));
    }

    @Test
    void preparaModelosParaLasVistasJsp() throws Exception {
        Usuario cliente = usuarioSesion(2L, "CLIENTE");
        MvcResult lista = mvc.perform(get("/f1316/reservas").sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().isOk()).andExpect(view().name("f1316/reservas")).andReturn();
        assertThat(lista.getModelAndView().getModel()).containsKey("reservas");

        MvcResult detalle = mvc.perform(get("/f1316/reservas/1").sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().isOk()).andExpect(view().name("f1316/detalle")).andReturn();
        assertThat(detalle.getModelAndView().getModel()).containsKeys("reserva", "pago", "duracionMinutos", "puedeCancelar");
    }

    @Test
    void elBorradorNoCreaReservaHastaQueSeProcesaElPago() throws Exception {
        int reservasAntes = contar("reserva");
        int pagosAntes = contar("pago");
        Usuario cliente = usuarioSesion(2L, "CLIENTE");

        MvcResult borrador = mvc.perform(post("/reservas/guardar").sessionAttr("usuarioLogueado", cliente)
                        .param("canchaId", "1").param("fecha", "2030-03-04")
                        .param("horaInicio", "12:00").param("horaFin", "13:00"))
                .andExpect(status().is3xxRedirection()).andReturn();
        assertThat(contar("reserva")).isEqualTo(reservasAntes);
        assertThat(contar("pago")).isEqualTo(pagosAntes);

        MockHttpSession sesion = (MockHttpSession) borrador.getRequest().getSession(false);
        mvc.perform(post("/reservas/pago/procesar").session(sesion)).andExpect(status().is3xxRedirection());
        assertThat(contar("reserva")).isEqualTo(reservasAntes + 1);
        assertThat(contar("pago")).isEqualTo(pagosAntes + 1);
    }

    @Test
    void sirveCssParaLasVistas() throws Exception {
        mvc.perform(get("/css/f1316.css")).andExpect(status().isOk());
    }

    private int contar(String tabla) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + tabla, Integer.class);
    }

    private Reserva crearReserva(LocalDate fecha, LocalTime inicio) {
        return service.registrarReservaPagada(2L, 1L, fecha, inicio, inicio.plusHours(1));
    }

    private Usuario usuarioSesion(Long id, String rol) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setRol(rol);
        return usuario;
    }

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2030-03-01T17:00:00Z"), LIMA);
        }
    }
}
