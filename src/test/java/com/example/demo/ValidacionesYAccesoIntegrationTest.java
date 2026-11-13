package com.example.demo;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static com.example.demo.TestRequests.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.demo.cancha.*;
import com.example.demo.categoria.*;
import com.example.demo.reserva.*;
import com.example.demo.usuario.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(ReservaIntegrationTest.FixedClockConfig.class)
class ValidacionesYAccesoIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired UsuarioService usuarios;
    @Autowired CategoriaService categorias;
    @Autowired CanchaService canchas;
    @Autowired ReservaService reservas;

    @ParameterizedTest
    @ValueSource(strings = {"/admin/panel", "/admin/categorias",
            "/canchas/admin", "/canchas/admin/nueva", "/canchas/admin/editar/1",
            "/usuario/admin/lista", "/reservas/admin", "/reservas/admin/1"})
    void rutasAdminRechazanAnonimoYClienteYPermitenAdmin(String ruta) throws Exception {
        mvc.perform(get(ruta)).andExpect(status().is3xxRedirection());
        mvc.perform(get(ruta).session(sesion(2L))).andExpect(status().is3xxRedirection());
        mvc.perform(get(ruta).session(sesion(1L))).andExpect(status().is2xxSuccessful());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/reservas/nueva?canchaId=1", "/reservas/pago", "/reservas/mis-reservas",
            "/reservas/mis-reservas/1"})
    void rutasClienteRequierenSesionDeCliente(String ruta) throws Exception {
        mvc.perform(get(ruta)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/usuario/login"));
        mvc.perform(get(ruta).session(sesion(1L))).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/usuario/login", "/usuario/registro", "/publicidad", "/contacto",
            "/canchas/catalogo", "/reservas/disponibilidad"})
    void paginasPublicasNoRequierenSesion(String ruta) throws Exception {
        mvc.perform(get(ruta)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/admin/categorias/guardar", "/admin/categorias/eliminar/1",
            "/canchas/admin/guardar", "/canchas/admin/estado/1", "/usuario/admin/bloquear/3",
            "/reservas/admin/1/cancelar"})
    void escriturasAdminSeProtegenAntesDeProcesarParametros(String ruta) throws Exception {
        mvc.perform(post(ruta)).andExpect(status().is3xxRedirection());
        mvc.perform(post(ruta).session(sesion(2L))).andExpect(status().is3xxRedirection());
        assertThat(jdbc.queryForObject("SELECT estado FROM usuario WHERE id_usuario=3", String.class)).isEqualTo("ACTIVO");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/reservas/guardar", "/reservas/pago/procesar", "/reservas/mis-reservas/1/cancelar"})
    void escriturasClienteRechazanAnonimosYAdministradores(String ruta) throws Exception {
        mvc.perform(post(ruta)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/usuario/login"));
        mvc.perform(post(ruta).session(sesion(1L))).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
    }

    @Test
    void bloqueoDuranteSesionInvalidaAcceso() throws Exception {
        MockHttpSession sesion = sesion(2L);
        jdbc.update("UPDATE usuario SET estado='BLOQUEADO' WHERE id_usuario=2");
        mvc.perform(get("/reservas/mis-reservas").session(sesion)).andExpect(redirectedUrl("/usuario/login"));
        assertThat(sesion.isInvalid()).isTrue();
        assertThat(usuarios.autenticar("carlos@gmail.com", "123456")).isNull();
    }

    @Test
    void rolEnSesionNoPermiteEscalarPrivilegios() throws Exception {
        MockHttpSession sesion = sesion(2L);
        ((Usuario) sesion.getAttribute("usuarioLogueado")).setRol("ADMINISTRADOR");
        mvc.perform(get("/admin/panel").session(sesion)).andExpect(redirectedUrl("/"));
        assertThat(((Usuario) sesion.getAttribute("usuarioLogueado")).getRol()).isEqualTo("CLIENTE");
    }

    @Test
    void sesionConDatoInvalidoNoProvocaError500() throws Exception {
        mvc.perform(get("/reservas/pago").sessionAttr("usuarioLogueado", "invalido"))
                .andExpect(redirectedUrl("/usuario/login"));
        mvc.perform(get("/reservas/pago").session(sesion(99999L))).andExpect(redirectedUrl("/usuario/login"));
    }

    @Test
    void loginValidaCredencialesRotaSesionYNoConservaClave() throws Exception {
        mvc.perform(post("/usuario/login").param("correo", "carlos@gmail.com").param("contrasena", "incorrecta"))
                .andExpect(view().name("usuario/login")).andExpect(model().attributeExists("error"));
        MockHttpSession sesion = new MockHttpSession();
        String anterior = sesion.getId();
        mvc.perform(post("/usuario/login").session(sesion).param("correo", "CARLOS@gmail.com").param("contrasena", "123456"))
                .andExpect(redirectedUrl("/canchas/catalogo"));
        assertThat(sesion.getId()).isNotEqualTo(anterior);
        assertThat(((Usuario) sesion.getAttribute("usuarioLogueado")).getContrasena()).isNull();
        mvc.perform(post("/usuario/logout").session(sesion)).andExpect(redirectedUrl("/"));
        assertThat(sesion.isInvalid()).isTrue();
    }

    @Test
    void registroValidaDatosYFijaRolSinRecortarContrasena() throws Exception {
        Usuario u = nuevoUsuario();
        u.setNombres("  ");
        assertThatThrownBy(() -> usuarios.registrarCliente(u)).isInstanceOf(IllegalArgumentException.class);
        mvc.perform(post("/usuario/registro").param("nombres", "Ana").param("apellidos", "Pérez")
                .param("documento", "AB12345").param("correo", "ana@example.com").param("contrasena", " 123456 ")
                .param("rol", "ADMINISTRADOR").param("estado", "BLOQUEADO"))
                .andExpect(redirectedUrl("/usuario/login?registrado=true"));
        Usuario registrada = usuarios.autenticar("ana@example.com", " 123456 ");
        assertThat(registrada.getRol()).isEqualTo("CLIENTE");
        assertThat(registrada.getEstado()).isEqualTo("ACTIVO");
        assertThat(usuarios.autenticar("ana@example.com", "123456")).isNull();
        Usuario invalida = nuevoUsuario();
        invalida.setCorreo("correo-invalido");
        assertThatThrownBy(() -> usuarios.registrarCliente(invalida)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({"07:00,08:00", "21:00,23:00", "12:00,12:00", "13:00,12:00", "12:30,13:30", "12:00:01,13:00:01"})
    void horariosInvalidosSeRechazanEnServicios(String inicio, String fin) {
        int antes = cantidadReservas();
        assertThatThrownBy(() -> reservas.registrarReservaPagada(2L, 1L, LocalDate.of(2030,3,4), LocalTime.parse(inicio), LocalTime.parse(fin)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> reservas.calcularMonto(1L, LocalTime.parse(inicio), LocalTime.parse(fin)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(cantidadReservas()).isEqualTo(antes);
    }

    @Test
    void admiteUltimoTurnoHastaLas22YRechazaSuperposicion() {
        Reserva r = reservas.registrarReservaPagada(2L, 1L, LocalDate.of(2030,3,4), LocalTime.of(21,0), LocalTime.of(22,0));
        assertThat(r.getImporte()).isEqualByComparingTo("50");
        assertThatThrownBy(() -> reservas.registrarReservaPagada(3L, 1L, r.getFecha(), r.getHoraInicio(), r.getHoraFin()))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("ocupado");
    }

    @Test
    void borradorInvalidoNoLlegaAPagoNiEscribeEnH2() throws Exception {
        int antes = cantidadReservas();
        mvc.perform(post("/reservas/guardar").session(sesion(2L)).param("canchaId", "1").param("fecha", "2030-03-04")
                .param("horaInicio", "21:00").param("horaFin", "23:00"))
                .andExpect(view().name("reserva/generar")).andExpect(model().attributeExists("error"));
        assertThat(cantidadReservas()).isEqualTo(antes);
        assertThatThrownBy(() -> reservas.validarSolicitud(2L,1L,LocalDate.of(2020,1,1),LocalTime.NOON,LocalTime.of(13,0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void noReservaCanchaInactivaNiCategoriaInactivaNiClienteBloqueado() {
        jdbc.update("UPDATE cancha SET estado='INACTIVA' WHERE id_cancha=1");
        assertThatThrownBy(() -> solicitud()).isInstanceOf(IllegalStateException.class);
        jdbc.update("UPDATE cancha SET estado='ACTIVA' WHERE id_cancha=1");
        jdbc.update("UPDATE categoria SET estado='INACTIVA' WHERE id_categoria=1");
        assertThatThrownBy(() -> solicitud()).isInstanceOf(IllegalStateException.class);
        jdbc.update("UPDATE categoria SET estado='ACTIVA' WHERE id_categoria=1");
        jdbc.update("UPDATE usuario SET estado='BLOQUEADO' WHERE id_usuario=2");
        assertThatThrownBy(() -> solicitud()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void canchasValidanPrecioCategoriaEstadoYLongitudes() {
        for (BigDecimal precio : List.of(BigDecimal.ZERO, new BigDecimal("-1"), new BigDecimal("1.001"), new BigDecimal("100000000"))) {
            Cancha c = nuevaCancha(); c.setPrecioHora(precio);
            assertThatThrownBy(() -> canchas.guardar(c)).isInstanceOf(IllegalArgumentException.class);
        }
        Cancha c = nuevaCancha(); c.setCategoria(new Categoria(9999L, null, null, null));
        assertThatThrownBy(() -> canchas.guardar(c)).isInstanceOf(IllegalArgumentException.class);
        c.setCategoria(categorias.obtenerPorId(1L)); c.setEstado("ARBITRARIO");
        assertThatThrownBy(() -> canchas.guardar(c)).isInstanceOf(IllegalArgumentException.class);
        c.setEstado("ACTIVA"); c.setNombre("x".repeat(101));
        assertThatThrownBy(() -> canchas.guardar(c)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void categoriasValidanNombreDuplicadosYReferencias() {
        assertThatThrownBy(() -> categorias.guardar(new Categoria(null, " ", "", "ACTIVA"))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> categorias.guardar(new Categoria(null, "fútbol 5", "", "ACTIVA"))).isInstanceOf(IllegalArgumentException.class);
        assertThat(categorias.eliminar(1L)).isFalse();
        assertThatThrownBy(() -> usuarios.alternarBloqueo(1L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rutasInvalidasDevuelven400O404SinError500() throws Exception {
        mvc.perform(get("/canchas/admin/editar/99999").session(sesion(1L))).andExpect(status().isNotFound());
        mvc.perform(get("/reservas/nueva?canchaId=99999").session(sesion(2L))).andExpect(status().isNotFound());
        mvc.perform(get("/reservas/disponibilidad?canchaId=-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/reservas/disponibilidad?fecha=no-es-fecha")).andExpect(status().isBadRequest());
    }

    @Test
    void pagoRevalidaCanchaYNoPermiteRepetirLaCompra() throws Exception {
        MockHttpSession sesion = sesion(2L);
        int antes = cantidadReservas();
        mvc.perform(post("/reservas/guardar").session(sesion).param("canchaId", "1").param("fecha", "2030-03-04")
                .param("horaInicio", "12:00").param("horaFin", "13:00")).andExpect(redirectedUrl("/reservas/pago"));
        jdbc.update("UPDATE cancha SET estado='INACTIVA' WHERE id_cancha=1");
        mvc.perform(post("/reservas/pago/procesar").session(sesion)).andExpect(redirectedUrl("/canchas/catalogo"));
        assertThat(cantidadReservas()).isEqualTo(antes);
        jdbc.update("UPDATE cancha SET estado='ACTIVA' WHERE id_cancha=1");
        mvc.perform(post("/reservas/guardar").session(sesion).param("canchaId", "1").param("fecha", "2030-03-04")
                .param("horaInicio", "12:00").param("horaFin", "13:00")).andExpect(redirectedUrl("/reservas/pago"));
        mvc.perform(post("/reservas/pago/procesar").session(sesion)).andExpect(status().is3xxRedirection());
        mvc.perform(post("/reservas/pago/procesar").session(sesion)).andExpect(redirectedUrl("/canchas/catalogo"));
        assertThat(cantidadReservas()).isEqualTo(antes + 1);
    }

    @Test
    void clienteCancelaSoloSuPropiaReserva() throws Exception {
        Reserva propia = reservas.registrarReservaPagada(2L,1L,LocalDate.of(2030,3,4),LocalTime.NOON,LocalTime.of(13,0));
        mvc.perform(post("/reservas/mis-reservas/" + propia.getIdReserva() + "/cancelar").session(sesion(3L)))
                .andExpect(redirectedUrl("/reservas/mis-reservas"));
        assertThat(reservas.detalleAdmin(propia.getIdReserva()).getEstado()).isEqualTo("CONFIRMADA");
        mvc.perform(post("/reservas/mis-reservas/" + propia.getIdReserva() + "/cancelar").session(sesion(2L)))
                .andExpect(redirectedUrl("/reservas/mis-reservas"));
        assertThat(reservas.detalleAdmin(propia.getIdReserva()).getEstado()).isEqualTo("CANCELADA");
    }

    @Test
    void rutasConIdentificadorDeSesionConservanLasRestricciones() throws Exception {
        mvc.perform(post("/reservas/pago/procesar;jsessionid=prueba"))
                .andExpect(redirectedUrl("/usuario/login"));
        mvc.perform(get("/admin/panel;jsessionid=prueba").session(sesion(2L))).andExpect(redirectedUrl("/"));
    }

    private void solicitud() { reservas.validarSolicitud(2L,1L,LocalDate.of(2030,3,4),LocalTime.NOON,LocalTime.of(13,0)); }
    private int cantidadReservas() { return jdbc.queryForObject("SELECT COUNT(*) FROM reserva", Integer.class); }
    private MockHttpSession sesion(Long id) {
        MockHttpSession s = new MockHttpSession();
        Usuario u = new Usuario(); u.setIdUsuario(id); s.setAttribute("usuarioLogueado",u); return s;
    }
    private Cancha nuevaCancha() { return new Cancha(null, categorias.obtenerPorId(1L), "Nueva", "", new BigDecimal("50.00"), "ACTIVA"); }
    private Usuario nuevoUsuario() {
        Usuario u = new Usuario(); u.setNombres("Ana"); u.setApellidos("Pérez"); u.setDocumento("AB12345");
        u.setCorreo("ana@example.com"); u.setContrasena("123456"); return u;
    }
}
