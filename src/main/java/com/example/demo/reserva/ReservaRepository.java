package com.example.demo.reserva;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private static final String SELECT_RESERVAS = "SELECT r.id_reserva, r.fecha, r.hora_inicio, r.hora_fin, "
            + "r.importe, r.estado, r.fecha_registro, u.id_usuario, u.nombres, u.apellidos, u.documento, u.correo, "
            + "c.id_cancha, c.nombre AS nombre_cancha, c.precio_hora, p.id_pago, p.importe AS pago_importe, "
            + "p.fecha_pago, p.estado AS pago_estado FROM reserva r "
            + "JOIN usuario u ON u.id_usuario = r.id_usuario "
            + "JOIN cancha c ON c.id_cancha = r.id_cancha "
            + "LEFT JOIN pago p ON p.id_reserva = r.id_reserva ";

    private final JdbcTemplate jdbcTemplate;

    public ReservaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Reserva> reservaRowMapper = (rs, rowNum) -> {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getLong("id_usuario"));
        usuario.setNombres(rs.getString("nombres"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setDocumento(rs.getString("documento"));
        usuario.setCorreo(rs.getString("correo"));

        Cancha cancha = new Cancha();
        cancha.setIdCancha(rs.getLong("id_cancha"));
        cancha.setNombre(rs.getString("nombre_cancha"));
        cancha.setPrecioHora(rs.getBigDecimal("precio_hora"));

        Reserva reserva = new Reserva(rs.getLong("id_reserva"), usuario, cancha,
                rs.getDate("fecha").toLocalDate(), rs.getTime("hora_inicio").toLocalTime(),
                rs.getTime("hora_fin").toLocalTime(), rs.getBigDecimal("importe"),
                rs.getString("estado"), rs.getTimestamp("fecha_registro").toLocalDateTime());
        long idPago = rs.getLong("id_pago");
        if (!rs.wasNull()) {
            Pago pago = new Pago();
            pago.setIdPago(idPago);
            pago.setReserva(reserva);
            pago.setImporte(rs.getBigDecimal("pago_importe"));
            if (rs.getTimestamp("fecha_pago") != null) {
                pago.setFechaPago(rs.getTimestamp("fecha_pago").toLocalDateTime());
            }
            pago.setEstado(rs.getString("pago_estado"));
            reserva.setPago(pago);
        }
        return reserva;
    };

    @Override
    public List<Reserva> listarTodas() {
        return jdbcTemplate.query(SELECT_RESERVAS + "ORDER BY r.fecha DESC, r.hora_inicio DESC", reservaRowMapper);
    }

    @Override
    public List<Reserva> listarPorUsuario(Long idUsuario) {
        return jdbcTemplate.query(SELECT_RESERVAS
                + "WHERE r.id_usuario = ? ORDER BY r.fecha DESC, r.hora_inicio DESC", reservaRowMapper, idUsuario);
    }

    @Override
    public List<Reserva> listarPorUsuarioYEstado(Long idUsuario, String estado) {
        return jdbcTemplate.query(SELECT_RESERVAS
                + "WHERE r.id_usuario = ? AND UPPER(r.estado) = UPPER(?) ORDER BY r.fecha DESC, r.hora_inicio DESC",
                reservaRowMapper, idUsuario, estado);
    }

    @Override
    public Reserva obtenerPorId(Long idReserva) {
        List<Reserva> reservas = jdbcTemplate.query(SELECT_RESERVAS + "WHERE r.id_reserva = ?",
                reservaRowMapper, idReserva);
        return reservas.isEmpty() ? null : reservas.get(0);
    }

    @Override
    public Long guardar(Reserva reserva) {
        String sql = "INSERT INTO reserva (id_usuario, id_cancha, fecha, hora_inicio, hora_fin, importe, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[] { "ID_RESERVA" });
            statement.setLong(1, reserva.getUsuario().getIdUsuario());
            statement.setLong(2, reserva.getCancha().getIdCancha());
            statement.setDate(3, java.sql.Date.valueOf(reserva.getFecha()));
            statement.setTime(4, java.sql.Time.valueOf(reserva.getHoraInicio()));
            statement.setTime(5, java.sql.Time.valueOf(reserva.getHoraFin()));
            statement.setBigDecimal(6, reserva.getImporte());
            statement.setString(7, reserva.getEstado());
            return statement;
        }, keyHolder);

        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("ID_RESERVA")) {
            return ((Number) keyHolder.getKeys().get("ID_RESERVA")).longValue();
        }
        return keyHolder.getKey() == null ? null : keyHolder.getKey().longValue();
    }

    @Override
    public int cambiarEstado(Long idReserva, String estado) {
        return jdbcTemplate.update("UPDATE reserva SET estado = ? WHERE id_reserva = ?", estado, idReserva);
    }

    @Override
    public List<Reserva> buscarSuperpuestas(Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        String sql = SELECT_RESERVAS + "WHERE r.id_cancha = ? AND r.fecha = ? AND r.estado <> 'CANCELADA' "
                + "AND (? < r.hora_fin AND ? > r.hora_inicio)";
        return jdbcTemplate.query(sql, reservaRowMapper, idCancha, java.sql.Date.valueOf(fecha),
                java.sql.Time.valueOf(horaInicio), java.sql.Time.valueOf(horaFin));
    }

    @Override
    public List<Reserva> buscarAdministrativamente(Long idReserva, String cliente, String cancha, String estado,
            LocalDate fechaDesde, LocalDate fechaHasta) {
        StringBuilder sql = new StringBuilder(SELECT_RESERVAS).append("WHERE 1 = 1 ");
        List<Object> parametros = new ArrayList<>();
        if (idReserva != null) {
            sql.append("AND r.id_reserva = ? ");
            parametros.add(idReserva);
        }
        if (cliente != null) {
            sql.append("AND (LOWER(CONCAT(u.nombres, ' ', u.apellidos)) LIKE ? OR LOWER(u.correo) LIKE ? "
                    + "OR LOWER(u.documento) LIKE ?) ");
            String patron = "%" + cliente.toLowerCase() + "%";
            parametros.add(patron);
            parametros.add(patron);
            parametros.add(patron);
        }
        if (cancha != null) {
            sql.append("AND LOWER(c.nombre) LIKE ? ");
            parametros.add("%" + cancha.toLowerCase() + "%");
        }
        if (estado != null) {
            sql.append("AND UPPER(r.estado) = UPPER(?) ");
            parametros.add(estado);
        }
        if (fechaDesde != null) {
            sql.append("AND r.fecha >= ? ");
            parametros.add(java.sql.Date.valueOf(fechaDesde));
        }
        if (fechaHasta != null) {
            sql.append("AND r.fecha <= ? ");
            parametros.add(java.sql.Date.valueOf(fechaHasta));
        }
        sql.append("ORDER BY r.fecha DESC, r.hora_inicio DESC");
        return jdbcTemplate.query(sql.toString(), reservaRowMapper, parametros.toArray());
    }

    @Override
    public void bloquearCancha(Long idCancha) {
        List<Long> canchas = jdbcTemplate.query("SELECT id_cancha FROM cancha WHERE id_cancha = ? FOR UPDATE",
                (rs, rowNum) -> rs.getLong(1), idCancha);
        if (canchas.isEmpty()) throw new IllegalStateException("La cancha ya no está disponible.");
    }

    @Override
    public int registrarPago(Long idReserva, BigDecimal importe) {
        return jdbcTemplate.update("INSERT INTO pago (id_reserva, importe, estado) VALUES (?, ?, 'REGISTRADO')",
                idReserva, importe);
    }
}
