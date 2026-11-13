package com.example.demo;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static com.example.demo.TestRequests.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.config.ConfiguracionAdministrador;
import com.example.demo.usuario.*;
import com.example.demo.categoria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NavegacionYEdicionIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired UsuarioService usuarios;
    @Autowired CategoriaService categorias;
    @Autowired ConfiguracionAdministrador administrador;

    @ParameterizedTest
    @ValueSource(strings={"/publicidad", "/contacto", "/nosotros", "/usuario/registro"})
    void paginasPublicasSoloSeMuestranSinAutenticacion(String ruta) throws Exception {
        mvc.perform(get(ruta)).andExpect(status().isOk());
        mvc.perform(get(ruta).session(sesion(2L))).andExpect(redirectedUrl("/"));
        mvc.perform(get(ruta).session(sesion(1L))).andExpect(redirectedUrl("/"));
    }

    @Test
    void cuentaFijaSeCreaSoloSiNoExisteAdministrador() throws Exception {
        jdbc.update("DELETE FROM usuario WHERE id_usuario=1");
        administrador.run(new DefaultApplicationArguments());
        Usuario admin = usuarios.autenticar(ConfiguracionAdministrador.CORREO, ConfiguracionAdministrador.CLAVE);
        assertThat(admin).isNotNull();
        assertThat(admin.getRol()).isEqualTo("ADMINISTRADOR");
        administrador.run(new DefaultApplicationArguments());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM usuario WHERE rol='ADMINISTRADOR'", Integer.class)).isEqualTo(1);
    }

    @Test
    void administradorExistenteNoSeReemplaza() throws Exception {
        jdbc.update("UPDATE usuario SET contrasena='claveAnterior' WHERE id_usuario=1");
        administrador.run(new DefaultApplicationArguments());
        assertThat(usuarios.autenticar("admin@canchas.pe", "claveAnterior")).isNotNull();
    }

    @Test
    void perfilModificaSoloAlUsuarioAutenticadoSinCambiarPermisos() throws Exception {
        MockHttpSession sesion = sesion(2L);
        mvc.perform(get("/usuario/perfil").session(sesion)).andExpect(view().name("usuario/formulario"));
        mvc.perform(post("/usuario/perfil").session(sesion)
                .param("idUsuario","3").param("nombres","Carlos editado").param("apellidos","Mendoza")
                .param("documento","74859612").param("correo","carlos@gmail.com").param("telefono","987654321")
                .param("rol","ADMINISTRADOR").param("estado","BLOQUEADO").param("contrasena","otraClave"))
                .andExpect(redirectedUrl("/usuario/perfil"));
        Usuario actual = usuarios.obtenerPorId(2L);
        assertThat(actual.getNombres()).isEqualTo("Carlos editado");
        assertThat(actual.getRol()).isEqualTo("CLIENTE");
        assertThat(actual.getEstado()).isEqualTo("ACTIVO");
        assertThat(usuarios.autenticar("carlos@gmail.com","123456")).isNotNull();
        assertThat(usuarios.obtenerPorId(3L).getNombres()).isEqualTo("Juan");
    }

    @Test
    void perfilYEdicionAdminExigenElRolCorrespondiente() throws Exception {
        mvc.perform(get("/usuario/perfil")).andExpect(redirectedUrl("/usuario/login"));
        mvc.perform(post("/usuario/perfil").session(sesion(1L))).andExpect(redirectedUrl("/"));
        mvc.perform(get("/usuario/admin/editar/3").session(sesion(2L))).andExpect(redirectedUrl("/"));
        mvc.perform(post("/usuario/admin/editar/3").session(sesion(2L))).andExpect(redirectedUrl("/"));
        mvc.perform(get("/usuario/admin/editar/3").session(sesion(1L))).andExpect(view().name("usuario/formulario"));
    }

    @Test
    void edicionRechazaDuplicadosYDatosInvalidosSinCambiarRegistros() {
        Usuario datos = usuarios.obtenerPorId(2L);
        datos.setCorreo("JUAN@gmail.com");
        assertThatThrownBy(() -> usuarios.actualizarCliente(2L,datos)).isInstanceOf(IllegalArgumentException.class);
        assertThat(usuarios.obtenerPorId(2L).getCorreo()).isEqualTo("carlos@gmail.com");
        datos.setCorreo("carlos@gmail.com"); datos.setNombres(" ");
        assertThatThrownBy(() -> usuarios.actualizarCliente(2L,datos)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> usuarios.actualizarCliente(1L,datos)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void categoriaSeCargaYActualizaDesdeSuInterfaz() throws Exception {
        mvc.perform(get("/admin/categorias/editar/1").session(sesion(1L)))
                .andExpect(view().name("categoria/categorias")).andExpect(model().attributeExists("nuevaCategoria"));
        mvc.perform(post("/admin/categorias/guardar").session(sesion(1L)).param("idCategoria","1")
                .param("nombre","Fútbol en grass").param("descripcion","Grass con iluminación").param("estado","ACTIVA"))
                .andExpect(redirectedUrl("/admin/categorias"));
        assertThat(categorias.obtenerPorId(1L).getNombre()).isEqualTo("Fútbol en grass");
        mvc.perform(get("/admin/categorias/editar/99999").session(sesion(1L))).andExpect(status().isNotFound());
    }

    private MockHttpSession sesion(Long id) {
        MockHttpSession session = new MockHttpSession(); Usuario usuario = new Usuario();
        usuario.setIdUsuario(id); session.setAttribute("usuarioLogueado",usuario); return session;
    }
}
