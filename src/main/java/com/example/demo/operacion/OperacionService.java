package com.example.demo.operacion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface OperacionService {
    void bloquear(Long admin, Long cancha, LocalDate fecha, LocalTime inicio, LocalTime fin, String motivo);
    void anular(Long admin, Long id);
    List<Map<String, Object>> bloqueos(Long cancha, LocalDate fecha);
    Map<String, Object> devolucion(Long reserva);
    void regularizarDevolucion(Long admin, Long idReserva);
    List<Map<String, Object>> pagos(LocalDate desde, LocalDate hasta, Long cancha, String estado);
    void validarRango(LocalDate desde, LocalDate hasta);
}
