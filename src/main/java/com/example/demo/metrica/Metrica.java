package com.example.demo.metrica;

import java.math.BigDecimal;
import java.util.List;

public record Metrica(String titulo, String nota, String tipo, String ejeX, String ejeY,
                      List<String> etiquetas, List<BigDecimal> valores) {
}
