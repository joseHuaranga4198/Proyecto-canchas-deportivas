package com.example.demo.operacion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface OperacionDAO {
    boolean bloqueada(Long cancha, LocalDate fecha, LocalTime inicio, LocalTime fin);
    List<Map<String, Object>> bloqueos(Long cancha, LocalDate fecha);
    void guardarBloqueo(Long cancha, LocalDate fecha, LocalTime inicio, LocalTime fin, String motivo, Long admin);
    Long canchaBloqueo(Long id);
    int anularBloqueo(Long id);
    void devolver(Long reserva, Long actor, String motivo);
    Map<String, Object> devolucion(Long reserva);
    List<Map<String, Object>> pagos(LocalDate desde, LocalDate hasta, Long cancha, String estado);
}
