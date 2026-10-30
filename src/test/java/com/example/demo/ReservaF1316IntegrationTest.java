package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

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
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
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
    void listaExponeLosIdsDeReservasCancelables() throws Exception {
        Reserva reserva = crearReserva(LocalDate.of(2030, 3, 3), LocalTime.NOON);
        Usuario cliente = usuarioSesion(2L, "CLIENTE");

        MvcResult resultado = mvc.perform(get("/f1316/reservas").sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().isOk()).andReturn();

        assertThat((Set<Long>) resultado.getModelAndView().getModel().get("cancelables"))
                .contains(reserva.getIdReserva());
    }

    @Test
    void cancelarComoClienteVuelveAlListado() throws Exception {
        Reserva reserva = crearReserva(LocalDate.of(2030, 3, 3), LocalTime.NOON);
        Usuario cliente = usuarioSesion(2L, "CLIENTE");

        mvc.perform(post("/f1316/reservas/{id}/cancelar", reserva.getIdReserva())
                        .sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/f1316/reservas"));
        assertThat(service.detallePropio(reserva.getIdReserva(), 2L).getEstado()).isEqualTo("CANCELADA");
    }

    @Test
    void adminVeDetalleAjenoYCancelaSinPlazoDe24Horas() throws Exception {
        // La reserva inicia dentro de 20 horas: no es cancelable por cliente, sí por administración.
        Reserva reserva = crearReserva(LocalDate.of(2030, 3, 2), LocalTime.of(8, 0));
        Usuario admin = usuarioSesion(1L, "ADMINISTRADOR");

        MvcResult detalle = mvc.perform(get("/f1316/admin/reservas/{id}", reserva.getIdReserva())
                        .sessionAttr("usuarioLogueado", admin))
                .andExpect(status().isOk()).andExpect(view().name("f1316/detalle")).andReturn();
        assertThat(detalle.getModelAndView().getModel()).containsEntry("esAdmin", true)
                .containsEntry("puedeCancelar", true);
        assertThat(service.puedeCancelar(reserva)).isFalse();

        mvc.perform(post("/f1316/admin/reservas/{id}/cancelar", reserva.getIdReserva())
                        .sessionAttr("usuarioLogueado", admin))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/f1316/admin/reservas"));

        Reserva cancelada = service.detalleAdmin(reserva.getIdReserva());
        assertThat(cancelada.getEstado()).isEqualTo("CANCELADA");
        assertThat(cancelada.getPago()).isNotNull();
        assertThat(cancelada.getPago().getEstado()).isEqualTo("REGISTRADO");
    }

    @Test
    void impideAccesoClienteAReservaAjenaYARutasAdministrativas() throws Exception {
        Reserva reservaAjena = service.registrarReservaPagada(3L, 1L, LocalDate.of(2030, 3, 3),
                LocalTime.NOON, LocalTime.of(13, 0));
        Usuario cliente = usuarioSesion(2L, "CLIENTE");

        mvc.perform(get("/f1316/reservas/{id}", reservaAjena.getIdReserva())
                        .sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/f1316/reservas"));
        mvc.perform(get("/f1316/admin/reservas/{id}", reservaAjena.getIdReserva())
                        .sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
        mvc.perform(post("/f1316/admin/reservas/{id}/cancelar", reservaAjena.getIdReserva())
                        .sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
        assertThat(service.detalleAdmin(reservaAjena.getIdReserva()).getEstado()).isEqualTo("CONFIRMADA");
    }

    @Test
    void rutaAdministrativaLegadaUsaLaMismaReglaYServicio() throws Exception {
        Reserva reserva = crearReserva(LocalDate.of(2030, 3, 2), LocalTime.of(8, 0));
        Usuario admin = usuarioSesion(1L, "ADMINISTRADOR");

        mvc.perform(post("/admin/reservas/cancelar/{id}", reserva.getIdReserva())
                        .sessionAttr("usuarioLogueado", admin))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/f1316/admin/reservas"));
        assertThat(service.detalleAdmin(reserva.getIdReserva()).getEstado()).isEqualTo("CANCELADA");
    }

    @Test
    void inicioRedirigeSegunRolYErroresDesconocidosConservan404() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("publico/home"));
        mvc.perform(get("/;jsessionid=sesion-de-prueba"))
                .andExpect(status().isOk()).andExpect(view().name("publico/home"));

        mvc.perform(get("/").sessionAttr("usuarioLogueado", usuarioSesion(2L, "CLIENTE")))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/canchas/catalogo"));
        mvc.perform(get("/").sessionAttr("usuarioLogueado", usuarioSesion(1L, "ADMINISTRADOR")))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/panel"));

        mvc.perform(get("/ruta-que-no-existe")).andExpect(status().isNotFound());
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
