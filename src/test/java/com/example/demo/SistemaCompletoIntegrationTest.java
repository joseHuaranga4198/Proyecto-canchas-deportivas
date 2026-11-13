package com.example.demo;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static com.example.demo.TestRequests.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import com.example.demo.reserva.*;
import com.example.demo.operacion.*;
import com.example.demo.metrica.*;
import com.example.demo.usuario.*;
import com.example.demo.config.Claves;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
@Import(ReservaIntegrationTest.FixedClockConfig.class)
@Sql({"/test-schema.sql","/test-data.sql"})
@Sql(scripts={"/test-schema.sql","/test-data.sql"},executionPhase=Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SistemaCompletoIntegrationTest {
    @Autowired ReservaService reservas;
    @Autowired OperacionService operaciones;
    @Autowired MetricasService metricas;
    @Autowired UsuarioService usuarios;
    @Autowired JdbcTemplate jdbc;
    @Autowired MockMvc mvc;
    private final LocalDate fecha=LocalDate.of(2030,3,4);

    @Test void mantenimientoConMinutosImpideReservarPeroPermiteTurnosFueraDelBloqueo(){
        operaciones.bloquear(1L,1L,fecha,LocalTime.of(12,15),LocalTime.of(13,45),"Mantenimiento");
        assertThatThrownBy(()->crear(12)).isInstanceOf(IllegalStateException.class);
        assertThat(crear(14).getIdReserva()).isPositive();
        Map<String, Object> bloqueo = operaciones.bloqueos(1L, fecha).get(0);
        assertThat(bloqueo).containsEntry("inicio", LocalTime.of(12, 15)).containsEntry("fin", LocalTime.of(13, 45));
        long id = ((Number) bloqueo.get("id")).longValue();
        operaciones.anular(1L,id);assertThat(crear(12).getIdReserva()).isPositive();
        assertThatThrownBy(()->operaciones.anular(1L,id)).isInstanceOf(IllegalStateException.class);
    }
    @Test void mantenimientoRechazaReservaExistenteYBloqueoDuplicado(){
        crear(12);
        assertThatThrownBy(()->operaciones.bloquear(1L,1L,fecha,LocalTime.NOON,LocalTime.of(13,0),"Reparación"))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("reservas");
        operaciones.bloquear(1L,1L,fecha,LocalTime.of(14,0),LocalTime.of(15,0),"Limpieza");
        assertThatThrownBy(()->operaciones.bloquear(1L,1L,fecha,LocalTime.of(14,0),LocalTime.of(16,0),"Limpieza"))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(()->operaciones.bloquear(2L,2L,fecha,LocalTime.NOON,LocalTime.of(13,0),"No autorizado"))
                .isInstanceOf(IllegalStateException.class);
    }
    @Test void cancelarDevuelveImporteCompletoSinDuplicarNiBorrarPago(){
        Reserva r=crear(12);reservas.cancelarPropia(r.getIdReserva(),2L);
        assertThat(operaciones.devolucion(r.getIdReserva()).get("importe").toString()).isEqualTo("50.00");
        assertThat(reservas.detalleAdmin(r.getIdReserva()).getPago().getEstado()).isEqualTo("REGISTRADO");
        assertThatThrownBy(()->reservas.cancelarPropia(r.getIdReserva(),2L)).isInstanceOf(IllegalStateException.class);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM devolucion",Integer.class)).isEqualTo(1);
        assertThat(crear(12).getIdReserva()).isPositive();
    }
    @Test void cancelacionRevierteSiFallaDevolucion(){
        Reserva r=crear(12);
        jdbc.execute("ALTER TABLE devolucion ADD CONSTRAINT fallo_test CHECK(importe < 0)");
        assertThatThrownBy(()->reservas.cancelarPropia(r.getIdReserva(),2L)).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
        assertThat(reservas.detalleAdmin(r.getIdReserva()).getEstado()).isEqualTo("CONFIRMADA");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM devolucion",Integer.class)).isZero();
    }
    @Test void regularizaCancelacionHistoricaConPermisoAdmin(){
        Reserva r=crear(12);jdbc.update("UPDATE reserva SET estado='CANCELADA' WHERE id_reserva=?",r.getIdReserva());
        operaciones.regularizarDevolucion(1L,r.getIdReserva());
        assertThat(operaciones.devolucion(r.getIdReserva())).isNotNull();
        assertThatThrownBy(()->operaciones.regularizarDevolucion(1L,r.getIdReserva())).isInstanceOf(IllegalStateException.class);
    }
    @Test void metricasSonCincoNoAcumuladasYExcluyenCanceladasEnUso(){
        Reserva a=crear(12);crear(14);reservas.cancelarPropia(a.getIdReserva(),2L);
        List<Metrica> datos=metricas.graficos(LocalDate.of(2030,3,1),fecha,null);
        assertThat(datos).hasSize(5);
        assertThat(datos.get(0).valores()).extracting(Object::toString).containsExactly("2","0","0","0");
        assertThat(datos.get(1).valores()).extracting(Object::toString).containsExactly("100.00","0","0","0");
        List<?> horas=datos.get(2).valores();assertThat(new java.math.BigDecimal(horas.get(3).toString())).isEqualByComparingTo("1");
        assertThat(datos.get(3).valores()).extracting(Object::toString).containsExactly("1");
        assertThat(datos.get(4).valores()).extracting(Object::toString).containsExactly("1");
        assertThat(metricas.graficos(LocalDate.of(2030,3,1),fecha,2L).get(3).valores()).isEmpty();
        assertThatThrownBy(()->metricas.graficos(fecha,fecha.minusDays(1),null)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void pagosSeFiltranYConservanLaDevolucion(){
        Reserva r=crear(12);reservas.cancelarAdmin(r.getIdReserva(),1L);
        List<Map<String,Object>> pagos=operaciones.pagos(LocalDate.of(2030,3,1),fecha,1L,"DEVUELTO");
        assertThat(pagos).hasSize(1);assertThat(pagos.get(0).get("devuelto").toString()).isEqualTo("50.00");
        assertThat(operaciones.pagos(LocalDate.of(2030,3,1),fecha,null,"SIN_DEVOLUCION")).isEmpty();
    }
    @Test void rutasNuevasRequierenAdminYTokensCsrf() throws Exception{
        for(String ruta:List.of("/admin/disponibilidad","/admin/pagos","/admin/metricas","/admin/metricas/datos?desde=2030-03-01&hasta=2030-03-04")){
            mvc.perform(get(ruta)).andExpect(status().is3xxRedirection());
            mvc.perform(get(ruta).session(sesion(2L))).andExpect(status().is3xxRedirection());
            mvc.perform(get(ruta).session(sesion(1L))).andExpect(status().isOk());
        }
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/usuario/login")
                .param("correo","admin@canchas.pe").param("contrasena","admin123")).andExpect(status().isForbidden());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/disponibilidad/bloquear")
                .session(sesion(1L))).andExpect(status().isForbidden());
        mvc.perform(post("/admin/disponibilidad/bloquear").session(sesion(1L)).param("canchaId","1").param("fecha","2030-03-04")
                .param("inicio","12:15").param("fin","12:45").param("motivo","Mantenimiento"))
                .andExpect(redirectedUrl("/admin/disponibilidad"));
        assertThat(operaciones.bloqueos(1L, fecha)).hasSize(1);
    }
    @Test void contrasenasNuevasYHeredadasUsanHashSinCambiarAcceso(){
        assertThat(usuarios.autenticar("carlos@gmail.com","123456")).isNotNull();
        String hash=jdbc.queryForObject("SELECT contrasena FROM usuario WHERE id_usuario=2",String.class);
        assertThat(Claves.esHash(hash)).isTrue();assertThat(hash).isNotEqualTo("123456");
        Usuario nuevo=new Usuario();nuevo.setNombres("Ana");nuevo.setApellidos("Pérez");nuevo.setDocumento("AB9876");
        nuevo.setCorreo("ana@example.com");nuevo.setContrasena("una contraseña válida y larga");usuarios.registrarCliente(nuevo);
        assertThat(usuarios.autenticar("ana@example.com","una contraseña válida y larga")).isNotNull();
        assertThat(Claves.esHash(jdbc.queryForObject("SELECT contrasena FROM usuario WHERE correo='ana@example.com'",String.class))).isTrue();
    }
    @Test void dosReservasSimultaneasSoloConfirmanUna() throws Exception{
        List<Boolean> resultados=paralelo(()->{crear(12);return true;},()->{crear(12);return true;});
        assertThat(resultados).containsExactlyInAnyOrder(true,false);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM reserva WHERE fecha=?",Integer.class,fecha)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM pago p JOIN reserva r ON r.id_reserva=p.id_reserva WHERE r.fecha=?",Integer.class,fecha)).isEqualTo(1);
    }
    @Test void mantenimientoYReservaSimultaneosNoSeSuperponen() throws Exception{
        List<Boolean> resultados=paralelo(()->{crear(12);return true;},()->{operaciones.bloquear(1L,1L,fecha,LocalTime.NOON,LocalTime.of(13,0),"Limpieza");return true;});
        assertThat(resultados).containsExactlyInAnyOrder(true,false);
    }
    @Test void dosCancelacionesSimultaneasGeneranUnaDevolucion() throws Exception{
        Reserva r=crear(12);List<Boolean> resultados=paralelo(()->{reservas.cancelarPropia(r.getIdReserva(),2L);return true;},()->{reservas.cancelarAdmin(r.getIdReserva(),1L);return true;});
        assertThat(resultados).containsExactlyInAnyOrder(true,false);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM devolucion",Integer.class)).isEqualTo(1);
    }
    private List<Boolean> paralelo(Callable<Boolean> a,Callable<Boolean> b) throws Exception{
        ExecutorService pool=Executors.newFixedThreadPool(2);CountDownLatch inicio=new CountDownLatch(1);
        try{List<Future<Boolean>> futuros=new ArrayList<>();for(Callable<Boolean> accion:List.of(a,b))futuros.add(pool.submit(()->{
            inicio.await();try{return accion.call();}catch(IllegalStateException ex){return false;}
        }));inicio.countDown();return List.of(futuros.get(0).get(10,TimeUnit.SECONDS),futuros.get(1).get(10,TimeUnit.SECONDS));}
        finally{pool.shutdownNow();}
    }
    private Reserva crear(int hora){return reservas.registrarReservaPagada(2L,1L,fecha,LocalTime.of(hora,0),LocalTime.of(hora+1,0));}
    private MockHttpSession sesion(Long id){MockHttpSession s=new MockHttpSession();Usuario u=new Usuario();u.setIdUsuario(id);s.setAttribute("usuarioLogueado",u);return s;}
}
