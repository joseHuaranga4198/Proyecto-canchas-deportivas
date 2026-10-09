package com.example.demo.reserva;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.cancha.Cancha;
import com.example.demo.usuario.Usuario;

@Repository
public class ReservaRepository implements ReservaDAO {

    private final JdbcTemplate jdbcTemplate;

    public ReservaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Reserva> reservaRowMapper = (rs, rowNum) -> {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getLong("id_usuario"));
        u.setNombres(rs.getString("nombres"));
        u.setApellidos(rs.getString("apellidos"));
        u.setCorreo(rs.getString("correo"));

        Cancha c = new Cancha();
        c.setIdCancha(rs.getLong("id_cancha"));
        c.setNombre(rs.getString("nombre_cancha"));
        c.setPrecioHora(rs.getBigDecimal("precio_hora"));

        return new Reserva(
                rs.getLong("id_reserva"),
                u,
                c,
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora_inicio").toLocalTime(),
                rs.getTime("hora_fin").toLocalTime(),
                rs.getBigDecimal("importe"),
                rs.getString("estado"),
                rs.getTimestamp("fecha_registro").toLocalDateTime()
        );
    };

    @Override
    public List<Reserva> listarTodas() {
        String sql = "SELECT r.id_reserva, r.fecha, r.hora_inicio, r.hora_fin, r.importe, r.estado, r.fecha_registro, " +
                "u.id_usuario, u.nombres, u.apellidos, u.correo, " +
                "c.id_cancha, c.nombre AS nombre_cancha, c.precio_hora " +
                "FROM reserva r " +
                "JOIN usuario u ON u.id_usuario = r.id_usuario " +
                "JOIN cancha c ON c.id_cancha = r.id_cancha " +
                "ORDER BY r.fecha DESC, r.hora_inicio DESC";
        return jdbcTemplate.query(sql, reservaRowMapper);
    }

    @Override
    public List<Reserva> listarPorUsuario(Long idUsuario) {
        String sql = "SELECT r.id_reserva, r.fecha, r.hora_inicio, r.hora_fin, r.importe, r.estado, r.fecha_registro, " +
                "u.id_usuario, u.nombres, u.apellidos, u.correo, " +
                "c.id_cancha, c.nombre AS nombre_cancha, c.precio_hora " +
                "FROM reserva r " +
                "JOIN usuario u ON u.id_usuario = r.id_usuario " +
                "JOIN cancha c ON c.id_cancha = r.id_cancha " +
                "WHERE r.id_usuario = ? " +
                "ORDER BY r.fecha DESC, r.hora_inicio DESC";
        return jdbcTemplate.query(sql, reservaRowMapper, idUsuario);
    }

    @Override
    public Reserva obtenerPorId(Long idReserva) {
        String sql = "SELECT r.id_reserva, r.fecha, r.hora_inicio, r.hora_fin, r.importe, r.estado, r.fecha_registro, " +
                "u.id_usuario, u.nombres, u.apellidos, u.correo, " +
                "c.id_cancha, c.nombre AS nombre_cancha, c.precio_hora " +
                "FROM reserva r " +
                "JOIN usuario u ON u.id_usuario = r.id_usuario " +
                "JOIN cancha c ON c.id_cancha = r.id_cancha " +
                "WHERE r.id_reserva = ?";
        List<Reserva> lista = jdbcTemplate.query(sql, reservaRowMapper, idReserva);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public Long guardar(Reserva reserva) {
        String sql = "INSERT INTO reserva (id_usuario, id_cancha, fecha, hora_inicio, hora_fin, importe, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, reserva.getUsuario().getIdUsuario());
            ps.setLong(2, reserva.getCancha().getIdCancha());
            ps.setDate(3, java.sql.Date.valueOf(reserva.getFecha()));
            ps.setTime(4, java.sql.Time.valueOf(reserva.getHoraInicio()));
            ps.setTime(5, java.sql.Time.valueOf(reserva.getHoraFin()));
            ps.setBigDecimal(6, reserva.getImporte());
            ps.setString(7, reserva.getEstado());
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    @Override
    public int cambiarEstado(Long idReserva, String estado) {
        String sql = "UPDATE reserva SET estado = ? WHERE id_reserva = ?";
        return jdbcTemplate.update(sql, estado, idReserva);
    }

    @Override
    public List<Reserva> buscarSuperpuestas(Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        // RN01: (horaInicio < r.hora_fin) AND (horaFin > r.hora_inicio)
        String sql = "SELECT r.id_reserva, r.fecha, r.hora_inicio, r.hora_fin, r.importe, r.estado, r.fecha_registro, " +
                "u.id_usuario, u.nombres, u.apellidos, u.correo, " +
                "c.id_cancha, c.nombre AS nombre_cancha, c.precio_hora " +
                "FROM reserva r " +
                "JOIN usuario u ON u.id_usuario = r.id_usuario " +
                "JOIN cancha c ON c.id_cancha = r.id_cancha " +
                "WHERE r.id_cancha = ? AND r.fecha = ? AND r.estado <> 'CANCELADA' " +
                "AND (? < r.hora_fin AND ? > r.hora_inicio)";
        return jdbcTemplate.query(sql, reservaRowMapper,
                idCancha,
                java.sql.Date.valueOf(fecha),
                java.sql.Time.valueOf(horaInicio),
                java.sql.Time.valueOf(horaFin)
        );
    }

    @Override
    public int registrarPago(Long idReserva, BigDecimal importe) {
        String sql = "INSERT INTO pago (id_reserva, importe, estado) VALUES (?, ?, 'REGISTRADO')";
        return jdbcTemplate.update(sql, idReserva, importe);
    }
}