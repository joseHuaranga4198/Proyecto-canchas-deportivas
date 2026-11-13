package com.example.demo.metrica;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MetricasRepository implements MetricasDAO {
    private final JdbcTemplate jdbc;
    public MetricasRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}
    @Override
    public Map<LocalDate,BigDecimal> serie(int tipo,LocalDate desde,LocalDate hasta,Long cancha){
        String columna=tipo==0?"r.fecha_registro":tipo==1?"p.fecha_pago":"r.fecha";
        String valor=tipo==0?"COUNT(*)":tipo==1?"SUM(p.importe)":"SUM(DATEDIFF('MINUTE',r.hora_inicio,r.hora_fin))/60.0";
        String sql="SELECT CAST("+columna+" AS DATE) AS dia,"+valor+" AS valor FROM reserva r ";
        if(tipo==1)sql+="JOIN pago p ON p.id_reserva=r.id_reserva ";
        sql+="WHERE "+columna+">=? AND "+columna+"<? ";
        List<Object> args=new ArrayList<>(List.of(desde.atStartOfDay(),hasta.plusDays(1).atStartOfDay()));
        if(tipo==2)sql+="AND r.estado='CONFIRMADA' ";
        if(tipo==1)sql+="AND p.estado='REGISTRADO' ";
        if(cancha!=null){sql+="AND r.id_cancha=? ";args.add(cancha);}
        Map<LocalDate,BigDecimal> mapa=new HashMap<>();
        jdbc.query(sql+"GROUP BY CAST("+columna+" AS DATE)",rs->{mapa.put(rs.getDate("dia").toLocalDate(),rs.getBigDecimal("valor"));},args.toArray());
        return mapa;
    }
    @Override
    public LinkedHashMap<String,BigDecimal> categorias(boolean porCategoria,LocalDate desde,LocalDate hasta,Long cancha){
        String grupo=porCategoria?"cat":"c";String id=porCategoria?"id_categoria":"id_cancha";
        String sql="SELECT "+grupo+"."+id+" AS id,"+grupo+".nombre,COUNT(*) AS valor FROM reserva r JOIN cancha c ON c.id_cancha=r.id_cancha "
                +"JOIN categoria cat ON cat.id_categoria=c.id_categoria WHERE r.estado='CONFIRMADA' AND r.fecha>=? AND r.fecha<? ";
        List<Object> args=new ArrayList<>(List.of(desde,hasta.plusDays(1)));
        if(cancha!=null){sql+="AND r.id_cancha=? ";args.add(cancha);}
        LinkedHashMap<String,BigDecimal> mapa=new LinkedHashMap<>();
        jdbc.query(sql+"GROUP BY "+grupo+"."+id+","+grupo+".nombre ORDER BY "+grupo+".nombre,"+grupo+"."+id,
                rs->{mapa.put(rs.getString("nombre")+" (#"+rs.getLong("id")+")",rs.getBigDecimal("valor"));},args.toArray());
        return mapa;
    }
}
