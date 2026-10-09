package com.example.demo.cancha;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.categoria.Categoria;

@Repository
public class CanchaRepository implements CanchaDAO {

    private final JdbcTemplate jdbcTemplate;

    public CanchaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Cancha> canchaRowMapper = (rs, rowNum) -> {
        Categoria cat = new Categoria();
        cat.setIdCategoria(rs.getLong("id_categoria"));
        cat.setNombre(rs.getString("nombre_categoria"));
        cat.setDescripcion(rs.getString("descripcion_categoria"));
        cat.setEstado(rs.getString("estado_categoria"));

        return new Cancha(
                rs.getLong("id_cancha"),
                cat,
                rs.getString("nombre"),
                rs.getString("caracteristicas"),
                rs.getBigDecimal("precio_hora"),
                rs.getString("estado")
        );
    };

    @Override
    public List<Cancha> listarTodas() {
        String sql = "SELECT c.id_cancha, c.nombre, c.caracteristicas, c.precio_hora, c.estado, " +
                "cat.id_categoria, cat.nombre AS nombre_categoria, cat.descripcion AS descripcion_categoria, cat.estado AS estado_categoria " +
                "FROM cancha c " +
                "JOIN categoria cat ON cat.id_categoria = c.id_categoria " +
                "ORDER BY c.id_cancha";
        return jdbcTemplate.query(sql, canchaRowMapper);
    }

    @Override
    public List<Cancha> listarDisponibles() {
        String sql = "SELECT c.id_cancha, c.nombre, c.caracteristicas, c.precio_hora, c.estado, " +
                "cat.id_categoria, cat.nombre AS nombre_categoria, cat.descripcion AS descripcion_categoria, cat.estado AS estado_categoria " +
                "FROM cancha c " +
                "JOIN categoria cat ON cat.id_categoria = c.id_categoria " +
                "WHERE c.estado = 'ACTIVA' " +
                "ORDER BY c.id_cancha";
        return jdbcTemplate.query(sql, canchaRowMapper);
    }

    @Override
    public Cancha obtenerPorId(Long idCancha) {
        String sql = "SELECT c.id_cancha, c.nombre, c.caracteristicas, c.precio_hora, c.estado, " +
                "cat.id_categoria, cat.nombre AS nombre_categoria, cat.descripcion AS descripcion_categoria, cat.estado AS estado_categoria " +
                "FROM cancha c " +
                "JOIN categoria cat ON cat.id_categoria = c.id_categoria " +
                "WHERE c.id_cancha = ?";
        List<Cancha> resultados = jdbcTemplate.query(sql, canchaRowMapper, idCancha);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public int guardar(Cancha cancha) {
        String sql = "INSERT INTO cancha (id_categoria, nombre, caracteristicas, precio_hora, estado) " +
                "VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                cancha.getCategoria().getIdCategoria(),
                cancha.getNombre(),
                cancha.getCaracteristicas(),
                cancha.getPrecioHora(),
                cancha.getEstado()
        );
    }

    @Override
    public int actualizar(Cancha cancha) {
        String sql = "UPDATE cancha SET id_categoria = ?, nombre = ?, caracteristicas = ?, " +
                "precio_hora = ?, estado = ? WHERE id_cancha = ?";
        return jdbcTemplate.update(sql,
                cancha.getCategoria().getIdCategoria(),
                cancha.getNombre(),
                cancha.getCaracteristicas(),
                cancha.getPrecioHora(),
                cancha.getEstado(),
                cancha.getIdCancha()
        );
    }

    @Override
    public int cambiarEstado(Long idCancha, String nuevoEstado) {
        String sql = "UPDATE cancha SET estado = ? WHERE id_cancha = ?";
        return jdbcTemplate.update(sql, nuevoEstado, idCancha);
    }

    @Override
    public long contarPorCategoria(Long idCategoria) {
        String sql = "SELECT COUNT(*) FROM cancha WHERE id_categoria = ?";
        Long total = jdbcTemplate.queryForObject(sql, Long.class, idCategoria);
        return total != null ? total : 0L;
    }
}