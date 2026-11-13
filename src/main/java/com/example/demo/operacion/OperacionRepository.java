package com.example.demo.operacion;

import java.time.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OperacionRepository implements OperacionDAO {
    private final JdbcTemplate jdbc;
    private final Clock clock;
    public OperacionRepository(JdbcTemplate jdbc,Clock clock) { this.jdbc = jdbc;this.clock=clock; }

    @Override
    public boolean bloqueada(Long cancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM bloqueo_cancha WHERE id_cancha=? AND fecha=? AND estado='ACTIVO' "
                + "AND hora_inicio < ? AND hora_fin > ?", Integer.class, cancha, fecha, fin, inicio) > 0;
    }

    @Override
    public List<Map<String,Object>> bloqueos(Long cancha, LocalDate fecha) {
        String sql = "SELECT b.*,c.nombre FROM bloqueo_cancha b JOIN cancha c ON c.id_cancha=b.id_cancha WHERE 1=1";
        List<Object> args = new ArrayList<>();
        if (cancha != null) { sql += " AND b.id_cancha=?"; args.add(cancha); }
        if (fecha != null) { sql += " AND b.fecha=? AND b.estado='ACTIVO'"; args.add(fecha); }
        return jdbc.query(sql + " ORDER BY b.fecha DESC,b.hora_inicio", (rs,n) -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id",rs.getLong("id_bloqueo")); m.put("canchaId",rs.getLong("id_cancha")); m.put("cancha",rs.getString("nombre"));
            m.put("fecha",rs.getDate("fecha").toLocalDate()); m.put("inicio",rs.getTime("hora_inicio").toLocalTime());
            m.put("fin",rs.getTime("hora_fin").toLocalTime()); m.put("motivo",rs.getString("motivo")); m.put("estado",rs.getString("estado"));
            return m;
        }, args.toArray());
    }

    @Override
    public void guardarBloqueo(Long cancha, LocalDate fecha, LocalTime inicio, LocalTime fin, String motivo, Long admin) {
        jdbc.update("INSERT INTO bloqueo_cancha(id_cancha,fecha,hora_inicio,hora_fin,motivo,id_administrador,fecha_registro) VALUES(?,?,?,?,?,?,?)",
                cancha,fecha,inicio,fin,motivo,admin,LocalDateTime.now(clock));
    }
    @Override
    public Long canchaBloqueo(Long id) {
        List<Long> ids=jdbc.query("SELECT id_cancha FROM bloqueo_cancha WHERE id_bloqueo=?",(rs,n)->rs.getLong(1),id);
        return ids.isEmpty()?null:ids.get(0);
    }
    @Override
    public int anularBloqueo(Long id) { return jdbc.update("UPDATE bloqueo_cancha SET estado='ANULADO' WHERE id_bloqueo=? AND estado='ACTIVO'",id); }

    @Override
    public void devolver(Long reserva, Long actor, String motivo) {
        jdbc.update("INSERT INTO devolucion(id_pago,importe,motivo,id_usuario,fecha_devolucion) SELECT id_pago,importe,?,?,? FROM pago WHERE id_reserva=? "
                + "AND estado='REGISTRADO' AND NOT EXISTS(SELECT 1 FROM devolucion d WHERE d.id_pago=pago.id_pago)", motivo,actor,LocalDateTime.now(clock),reserva);
    }
    @Override
    public Map<String,Object> devolucion(Long reserva) {
        List<Map<String,Object>> datos=jdbc.query("SELECT d.* FROM devolucion d JOIN pago p ON p.id_pago=d.id_pago WHERE p.id_reserva=?",(rs,n)-> {
            Map<String,Object> m=new LinkedHashMap<>();m.put("importe",rs.getBigDecimal("importe"));m.put("fecha",formato(rs.getTimestamp("fecha_devolucion")));
            m.put("motivo",rs.getString("motivo"));m.put("id",rs.getLong("id_devolucion"));return m;
        },reserva);
        return datos.isEmpty()?null:datos.get(0);
    }

    @Override
    public List<Map<String,Object>> pagos(LocalDate desde, LocalDate hasta, Long cancha, String estado) {
        String sql="SELECT p.*,r.estado AS reserva_estado,u.nombres,u.apellidos,c.nombre,d.id_devolucion,d.importe AS devuelto,d.fecha_devolucion "
                + "FROM pago p JOIN reserva r ON r.id_reserva=p.id_reserva JOIN usuario u ON u.id_usuario=r.id_usuario "
                + "JOIN cancha c ON c.id_cancha=r.id_cancha LEFT JOIN devolucion d ON d.id_pago=p.id_pago WHERE p.fecha_pago>=? AND p.fecha_pago<?";
        List<Object> args=new ArrayList<>(List.of(desde.atStartOfDay(),hasta.plusDays(1).atStartOfDay()));
        if(cancha!=null){sql+=" AND r.id_cancha=?";args.add(cancha);}
        if("DEVUELTO".equals(estado))sql+=" AND d.id_devolucion IS NOT NULL";
        if("SIN_DEVOLUCION".equals(estado))sql+=" AND d.id_devolucion IS NULL";
        return jdbc.query(sql+" ORDER BY p.fecha_pago DESC,p.id_pago DESC",(rs,n)->{
            Map<String,Object> m=new LinkedHashMap<>();m.put("id",rs.getLong("id_pago"));m.put("reservaId",rs.getLong("id_reserva"));
            m.put("cliente",rs.getString("nombres")+" "+rs.getString("apellidos"));m.put("cancha",rs.getString("nombre"));
            m.put("importe",rs.getBigDecimal("importe"));m.put("fecha",formato(rs.getTimestamp("fecha_pago")));
            m.put("reservaEstado",rs.getString("reserva_estado"));m.put("devuelto",rs.getBigDecimal("devuelto"));
            m.put("fechaDevolucion",formato(rs.getTimestamp("fecha_devolucion")));return m;
        },args.toArray());
    }
    private String formato(java.sql.Timestamp valor){return valor==null?"":valor.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));}
}
