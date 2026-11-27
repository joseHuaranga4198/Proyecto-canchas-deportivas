package com.example.demo.metrica;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Consultas SQL de las diez métricas. El filtro de cancha es opcional:
 * "CAST(? AS BIGINT) IS NULL OR r.id_cancha = ?" deja pasar todas las canchas cuando llega null.
 */
@Repository
public class MetricasRepository implements MetricasDAO {

    private static final String FILTRO_CANCHA = " AND (CAST(? AS BIGINT) IS NULL OR r.id_cancha = ?) ";

    private final JdbcTemplate jdbc;

    public MetricasRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------------- Series de tiempo ----------------

    @Override
    public Map<LocalDate, BigDecimal> reservasRegistradasPorDia(LocalDate desde, LocalDate hasta, Long cancha) {
        String sql = "SELECT CAST(r.fecha_registro AS DATE) AS dia, COUNT(*) AS valor "
                + "FROM reserva r "
                + "WHERE r.fecha_registro >= ? AND r.fecha_registro < ?" + FILTRO_CANCHA
                + "GROUP BY CAST(r.fecha_registro AS DATE) ORDER BY dia";
        return porDia(sql, desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay(), cancha, cancha);
    }

    @Override
    public Map<LocalDate, BigDecimal> importePagadoPorDia(LocalDate desde, LocalDate hasta, Long cancha) {
        String sql = "SELECT CAST(p.fecha_pago AS DATE) AS dia, SUM(p.importe) AS valor "
                + "FROM pago p JOIN reserva r ON r.id_reserva = p.id_reserva "
                + "WHERE p.estado = 'REGISTRADO' AND p.fecha_pago >= ? AND p.fecha_pago < ?" + FILTRO_CANCHA
                + "GROUP BY CAST(p.fecha_pago AS DATE) ORDER BY dia";
        return porDia(sql, desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay(), cancha, cancha);
    }

    @Override
    public Map<LocalDate, BigDecimal> horasReservadasPorDia(LocalDate desde, LocalDate hasta, Long cancha) {
        String sql = "SELECT r.fecha AS dia, SUM(DATEDIFF('MINUTE', r.hora_inicio, r.hora_fin)) / 60.0 AS valor "
                + "FROM reserva r "
                + "WHERE r.estado = 'CONFIRMADA' AND r.fecha >= ? AND r.fecha <= ?" + FILTRO_CANCHA
                + "GROUP BY r.fecha ORDER BY dia";
        return porDia(sql, desde, hasta, cancha, cancha);
    }

    @Override
    public Map<LocalDate, BigDecimal> importeDevueltoPorDia(LocalDate desde, LocalDate hasta, Long cancha) {
        String sql = "SELECT CAST(d.fecha_devolucion AS DATE) AS dia, SUM(d.importe) AS valor "
                + "FROM devolucion d JOIN pago p ON p.id_pago = d.id_pago "
                + "JOIN reserva r ON r.id_reserva = p.id_reserva "
                + "WHERE d.fecha_devolucion >= ? AND d.fecha_devolucion < ?" + FILTRO_CANCHA
                + "GROUP BY CAST(d.fecha_devolucion AS DATE) ORDER BY dia";
        return porDia(sql, desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay(), cancha, cancha);
    }

    @Override
    public Map<LocalDate, BigDecimal> clientesConReservaPorDia(LocalDate desde, LocalDate hasta, Long cancha) {
        String sql = "SELECT CAST(r.fecha_registro AS DATE) AS dia, COUNT(DISTINCT r.id_usuario) AS valor "
                + "FROM reserva r "
                + "WHERE r.fecha_registro >= ? AND r.fecha_registro < ?" + FILTRO_CANCHA
                + "GROUP BY CAST(r.fecha_registro AS DATE) ORDER BY dia";
        return porDia(sql, desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay(), cancha, cancha);
    }

    // ---------------- Estado puntual ----------------

    @Override
    public BigDecimal reservasConfirmadasDelDia(LocalDate dia, Long cancha) {
        String sql = "SELECT COUNT(*) FROM reserva r "
                + "WHERE r.estado = 'CONFIRMADA' AND r.fecha = ?" + FILTRO_CANCHA;
        return valor(sql, dia, cancha, cancha);
    }

    @Override
    public BigDecimal horasConfirmadasDelDia(LocalDate dia, Long cancha) {
        String sql = "SELECT COALESCE(SUM(DATEDIFF('MINUTE', r.hora_inicio, r.hora_fin)), 0) / 60.0 FROM reserva r "
                + "WHERE r.estado = 'CONFIRMADA' AND r.fecha = ?" + FILTRO_CANCHA;
        return valor(sql, dia, cancha, cancha);
    }

    @Override
    public BigDecimal canchasHabilitadas(Long cancha) {
        String sql = "SELECT COUNT(*) FROM cancha r JOIN categoria cat ON cat.id_categoria = r.id_categoria "
                + "WHERE r.estado = 'ACTIVA' AND cat.estado = 'ACTIVA'" + FILTRO_CANCHA;
        return valor(sql, cancha, cancha);
    }

    @Override
    public BigDecimal reservasPendientesDeUso(LocalDateTime ahora, Long cancha) {
        String sql = "SELECT COUNT(*) FROM reserva r "
                + "WHERE r.estado = 'CONFIRMADA' "
                + "AND (r.fecha > ? OR (r.fecha = ? AND r.hora_inicio > ?))" + FILTRO_CANCHA;
        return valor(sql, ahora.toLocalDate(), ahora.toLocalDate(), ahora.toLocalTime(), cancha, cancha);
    }

    @Override
    public BigDecimal saldoRetenido(Long cancha) {
        String sql = "SELECT COALESCE(SUM(p.importe - COALESCE(d.importe, 0)), 0) "
                + "FROM pago p JOIN reserva r ON r.id_reserva = p.id_reserva "
                + "LEFT JOIN devolucion d ON d.id_pago = p.id_pago "
                + "WHERE p.estado = 'REGISTRADO'" + FILTRO_CANCHA;
        return valor(sql, cancha, cancha);
    }

    @Override
    public BigDecimal mantenimientosProgramados(LocalDate dia, Long cancha) {
        String sql = "SELECT COUNT(*) FROM bloqueo_cancha r "
                + "WHERE r.estado = 'ACTIVO' AND r.fecha >= ?" + FILTRO_CANCHA;
        return valor(sql, dia, cancha, cancha);
    }

    // ---------------- Apoyo ----------------

    private Map<LocalDate, BigDecimal> porDia(String sql, Object... parametros) {
        Map<LocalDate, BigDecimal> mapa = new HashMap<>();
        jdbc.query(sql, rs -> {
            mapa.put(rs.getDate("dia").toLocalDate(), rs.getBigDecimal("valor"));
        }, parametros);
        return mapa;
    }

    private BigDecimal valor(String sql, Object... parametros) {
        BigDecimal resultado = jdbc.queryForObject(sql, BigDecimal.class, parametros);
        return resultado == null ? BigDecimal.ZERO : resultado;
    }
}
