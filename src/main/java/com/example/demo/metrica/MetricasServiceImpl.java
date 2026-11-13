package com.example.demo.metrica;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.example.demo.validacion.Validaciones;
import com.example.demo.operacion.OperacionService;
import org.springframework.stereotype.Service;

@Service
public class MetricasServiceImpl implements MetricasService {
    private final MetricasDAO repository;
    private final OperacionService operaciones;

    public MetricasServiceImpl(MetricasDAO repository, OperacionService operaciones) {
        this.repository = repository;
        this.operaciones = operaciones;
    }

    @Override
    public void validarRango(LocalDate desde, LocalDate hasta) {
        operaciones.validarRango(desde, hasta);
    }

    @Override
    public List<Metrica> graficos(LocalDate desde, LocalDate hasta, Long cancha) {
        validarRango(desde, hasta);
        if (cancha != null) Validaciones.id(cancha, "La cancha");
        String[] titulos = {"Reservas registradas por día","Pagos registrados por día (bruto)","Horas reservadas por día","Reservas por cancha","Reservas por categoría"};
        String[] notas = {"Fecha de registro. Incluye reservas posteriormente canceladas.","Fecha del pago. Las devoluciones se consultan por separado en Pagos; este gráfico no representa ingreso neto.",
            "Fecha de uso. Solo reservas confirmadas.","Fecha de uso dentro del período. Solo reservas confirmadas.","Fecha de uso dentro del período. Solo reservas confirmadas."};
        List<Metrica> lista = new ArrayList<>();
        for (int i = 0; i < titulos.length; i++) {
            Map<String, BigDecimal> valores;
            if (i < 3) {
                valores = new LinkedHashMap<>();
                Map<LocalDate, BigDecimal> serie = repository.serie(i, desde, hasta, cancha);
                for (LocalDate dia = desde; !dia.isAfter(hasta); dia = dia.plusDays(1)) {
                    valores.put(dia.toString(), serie.getOrDefault(dia, BigDecimal.ZERO));
                }
            } else {
                valores = repository.categorias(i == 4, desde, hasta, cancha);
            }
            lista.add(new Metrica(titulos[i], notas[i], i < 3 ? "line" : "bar",
                    i < 3 ? "Fecha" : i == 3 ? "Cancha" : "Categoría",
                    i == 1 ? "Soles (S/)" : i == 2 ? "Horas" : "Cantidad",
                    List.copyOf(valores.keySet()), List.copyOf(valores.values())));
        }
        return lista;
    }
}
