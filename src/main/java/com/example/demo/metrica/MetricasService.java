package com.example.demo.metrica;

import java.time.LocalDate;
import java.util.List;

public interface MetricasService {
    void validarRango(LocalDate desde, LocalDate hasta);

    /** Cinco métricas de serie de tiempo (un valor por día del rango). */
    List<Metrica> graficos(LocalDate desde, LocalDate hasta, Long cancha);

    /** Cinco métricas de estado puntual (un valor en el momento de la consulta). */
    List<Metrica> estadoPuntual(Long cancha);
}
