package com.example.demo.metrica;

import java.time.LocalDate;
import java.util.List;

public interface MetricasService {
    void validarRango(LocalDate desde, LocalDate hasta);
    List<Metrica> graficos(LocalDate desde, LocalDate hasta, Long cancha);
}
