package com.example.demo.usuario;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository implements UsuarioDAO {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Usuario> usuarioRowMapper = (rs, rowNum) -> new Usuario(
            rs.getLong("id_usuario"),
            rs.getString("nombres"),
            rs.getString("apellidos"),
            rs.getString("documento"),
            rs.getString("telefono"),
            rs.getString("correo"),
            rs.getString("contrasena"),
            rs.getString("rol"),
            rs.getString("estado")
    );

    @Override
    public Usuario autenticar(String correo, String contrasena) {
        String sql = "SELECT * FROM usuario WHERE correo = ? AND contrasena = ?";
        List<Usuario> lista = jdbcTemplate.query(sql, usuarioRowMapper, correo, contrasena);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public Usuario obtenerPorId(Long idUsuario) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        List<Usuario> lista = jdbcTemplate.query(sql, usuarioRowMapper, idUsuario);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Usuario> listarPorRol(String rol) {
        String sql = "SELECT * FROM usuario WHERE rol = ? ORDER BY id_usuario";
        return jdbcTemplate.query(sql, usuarioRowMapper, rol);
    }

    @Override
    public int guardar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nombres, apellidos, documento, telefono, correo, contrasena, rol, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getDocumento(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                usuario.getContrasena(),
                usuario.getRol(),
                usuario.getEstado()
        );
    }

    @Override
    public int cambiarEstado(Long idUsuario, String estado) {
        String sql = "UPDATE usuario SET estado = ? WHERE id_usuario = ?";
        return jdbcTemplate.update(sql, estado, idUsuario);
    }

    @Override
    public boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, correo);
        return count != null && count > 0;
    }

    @Override
    public boolean existeDocumento(String documento) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE documento = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, documento);
        return count != null && count > 0;
    }
}