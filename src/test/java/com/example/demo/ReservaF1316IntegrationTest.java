package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.reserva.PagoJpaRepository;
import com.example.demo.reserva.Reserva;
import com.example.demo.reserva.ReservaF1316Service;
import com.example.demo.reserva.ReservaJpaRepository;
import com.example.demo.usuario.Usuario;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReservaF1316IntegrationTest {
    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    @Autowired private ReservaF1316Service service;
    @Autowired private ReservaJpaRepository reservas;
    @Autowired private PagoJpaRepository pagos;
    @Autowired private MockMvc mvc;

    @Test
    void guardaReservaSoloJuntoConPago() {
        long pagosAntes = pagos.count();
        long reservasAntes = reservas.count();

        Reserva reserva = crearReserva(LocalDate.of(2030, 3, 3), LocalTime.NOON);

        assertThat(reserva.getEstado()).isEqualTo("CONFIRMADA");
        assertThat(reservas.count()).isEqualTo(reservasAntes + 1);
        assertThat(pagos.count()).isEqualTo(pagosAntes + 1);
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
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("24 horas");
    }

    @Test
    void impideConsultarOCancelarReservaAjena() {
        Reserva reserva = crearReserva(LocalDate.of(2030, 3, 3), LocalTime.NOON);
        assertThat(service.detallePropio(reserva.getIdReserva(), 3L)).isNull();
        assertThatThrownBy(() -> service.cancelarPropia(reserva.getIdReserva(), 3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("otro cliente");
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
        mvc.perform(get("/f1316/reservas"))
                .andExpect(status().is3xxRedirection());

        Usuario cliente = usuarioSesion(2L, "CLIENTE");
        mvc.perform(get("/f1316/admin/reservas").sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().is3xxRedirection());

        Usuario admin = usuarioSesion(1L, "ADMINISTRADOR");
        mvc.perform(get("/f1316/admin/reservas").sessionAttr("usuarioLogueado", admin))
                .andExpect(status().isOk())
                .andExpect(view().name("f1316/admin-reservas"));
    }

    @Test
    void renderizaListaYDetalleConLosDatosDeThymeleaf() throws Exception {
        Usuario cliente = usuarioSesion(2L, "CLIENTE");
        mvc.perform(get("/f1316/reservas").sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().isOk())
                .andExpect(view().name("f1316/reservas"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mis reservas")));

        mvc.perform(get("/f1316/reservas/1").sessionAttr("usuarioLogueado", cliente))
                .andExpect(status().isOk())
                .andExpect(view().name("f1316/detalle"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Datos del pago")));
    }

    @Test
    void elBorradorNoCreaReservaHastaQueSeProcesaElPago() throws Exception {
        long reservasAntes = reservas.count();
        long pagosAntes = pagos.count();
        Usuario cliente = usuarioSesion(2L, "CLIENTE");

        MvcResult borrador = mvc.perform(post("/reservas/guardar")
                        .sessionAttr("usuarioLogueado", cliente)
                        .param("canchaId", "1")
                        .param("fecha", "2030-03-04")
                        .param("horaInicio", "12:00")
                        .param("horaFin", "13:00"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        assertThat(reservas.count()).isEqualTo(reservasAntes);
        assertThat(pagos.count()).isEqualTo(pagosAntes);

        MockHttpSession sesion = (MockHttpSession) borrador.getRequest().getSession(false);
        mvc.perform(post("/reservas/pago/procesar").session(sesion))
                .andExpect(status().is3xxRedirection());
        assertThat(reservas.count()).isEqualTo(reservasAntes + 1);
        assertThat(pagos.count()).isEqualTo(pagosAntes + 1);
    }

    @Test
    void publicaLosRecursosCssThymeleaf() throws Exception {
        mvc.perform(get("/css/f1316.css")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/css"));
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
