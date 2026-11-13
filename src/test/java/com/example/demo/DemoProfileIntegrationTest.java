package com.example.demo;
import static org.assertj.core.api.Assertions.*;
import com.example.demo.usuario.UsuarioService;
import com.example.demo.metrica.MetricasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import java.time.*;

@SpringBootTest(properties="spring.sql.init.schema-locations=classpath:test-schema.sql")
@ActiveProfiles({"test","demo"})
class DemoProfileIntegrationTest {
    @Autowired JdbcTemplate jdbc;@Autowired UsuarioService usuarios;@Autowired MetricasService metricas;
    @Test void demostracionTieneDatosCoherentesYAmbosAccesos(){
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM cancha",Integer.class)).isEqualTo(4);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM reserva",Integer.class)).isEqualTo(28);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM devolucion",Integer.class)).isEqualTo(4);
        assertThat(usuarios.autenticar("admin@canchas.pe","admin123").getRol()).isEqualTo("ADMINISTRADOR");
        assertThat(usuarios.autenticar("cliente@canchaya.pe","cliente123").getRol()).isEqualTo("CLIENTE");
        LocalDate hoy=LocalDate.now(ZoneId.of("America/Lima"));
        assertThat(metricas.graficos(hoy.minusDays(29),hoy,null)).hasSize(5);
    }
}
