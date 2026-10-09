package com.example.demo.categoria;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CategoriaRepository implements CategoriaDAO {

    private final JdbcTemplate jdbcTemplate;

    public CategoriaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Categoria> categoriaRowMapper = (rs, rowNum) -> new Categoria(
            rs.getLong("id_categoria"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getString("estado")
    );

    @Override
    public List<Categoria> listarTodas() {
        String sql = "SELECT id_categoria, nombre, descripcion, estado FROM categoria ORDER BY id_categoria";
        return jdbcTemplate.query(sql, categoriaRowMapper);
    }

    @Override
    public List<Categoria> listarActivas() {
        String sql = "SELECT id_categoria, nombre, descripcion, estado FROM categoria WHERE estado = 'ACTIVA' ORDER BY id_categoria";
        return jdbcTemplate.query(sql, categoriaRowMapper);
    }

    @Override
    public Categoria obtenerPorId(Long idCategoria) {
        String sql = "SELECT id_categoria, nombre, descripcion, estado FROM categoria WHERE id_categoria = ?";
        List<Categoria> lista = jdbcTemplate.query(sql, categoriaRowMapper, idCategoria);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public int guardar(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre, descripcion, estado) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, categoria.getNombre(), categoria.getDescripcion(), categoria.getEstado());
    }

    @Override
    public int actualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nombre = ?, descripcion = ?, estado = ? WHERE id_categoria = ?";
        return jdbcTemplate.update(sql, categoria.getNombre(), categoria.getDescripcion(), categoria.getEstado(), categoria.getIdCategoria());
    }

    @Override
    public int eliminar(Long idCategoria) {
        String sql = "DELETE FROM categoria WHERE id_categoria = ?";
        return jdbcTemplate.update(sql, idCategoria);
    }

    @Override
    public long contarCanchasAsociadas(Long idCategoria) {
        String sql = "SELECT COUNT(*) FROM cancha WHERE id_categoria = ?";
        Long total = jdbcTemplate.queryForObject(sql, Long.class, idCategoria);
        return total != null ? total : 0L;
    }
}