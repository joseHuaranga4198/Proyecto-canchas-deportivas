package com.example.demo.metrica;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface MetricasDAO {
    Map<LocalDate, BigDecimal> serie(int tipo, LocalDate desde, LocalDate hasta, Long cancha);
    Map<String, BigDecimal> categorias(boolean porCategoria, LocalDate desde, LocalDate hasta, Long cancha);
}
