package com.example.demo.metrica;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.example.demo.validacion.Validaciones;
import com.example.demo.operacion.OperacionService;
import org.springframework.stereotype.Service;

@Service
public class MetricasServiceImpl implements MetricasService {

    private static final BigDecimal HORAS_ATENCION = BigDecimal.valueOf(14);

    private final MetricasDAO repository;
    private final OperacionService operaciones;
    private final Clock clock;

    public MetricasServiceImpl(MetricasDAO repository, OperacionService operaciones, Clock clock) {
        this.repository = repository;
        this.operaciones = operaciones;
        this.clock = clock;
    }

    @Override
    public void validarRango(LocalDate desde, LocalDate hasta) {
        operaciones.validarRango(desde, hasta);
    }

    @Override
    public List<Metrica> graficos(LocalDate desde, LocalDate hasta, Long cancha) {
        validarRango(desde, hasta);
        if (cancha != null) Validaciones.id(cancha, "La cancha");
        List<Metrica> lista = new ArrayList<>();
        lista.add(serie("ST1. Reservas registradas por día",
                "Fecha de registro. Incluye las reservas que luego se cancelaron.", "Cantidad",
                repository.reservasRegistradasPorDia(desde, hasta, cancha), desde, hasta));
        lista.add(serie("ST2. Importe pagado por día (bruto)",
                "Fecha del pago. No descuenta devoluciones; ver ST4.", "Soles (S/)",
                repository.importePagadoPorDia(desde, hasta, cancha), desde, hasta));
        lista.add(serie("ST3. Horas reservadas por día de uso",
                "Fecha de uso. Solo reservas confirmadas.", "Horas",
                repository.horasReservadasPorDia(desde, hasta, cancha), desde, hasta));
        lista.add(serie("ST4. Importe devuelto por día",
                "Fecha de la devolución. Cada cancelación devuelve el pago completo.", "Soles (S/)",
                repository.importeDevueltoPorDia(desde, hasta, cancha), desde, hasta));
        lista.add(serie("ST5. Clientes distintos que reservaron por día",
                "Fecha de registro. Un cliente con varias reservas el mismo día cuenta una vez.", "Clientes",
                repository.clientesConReservaPorDia(desde, hasta, cancha), desde, hasta));
        return lista;
    }

    @Override
    public List<Metrica> estadoPuntual(Long cancha) {
        if (cancha != null) Validaciones.id(cancha, "La cancha");
        LocalDateTime ahora = LocalDateTime.now(clock);
        LocalDate hoy = ahora.toLocalDate();

        BigDecimal horasHoy = repository.horasConfirmadasDelDia(hoy, cancha);
        BigDecimal capacidad = repository.canchasHabilitadas(cancha).multiply(HORAS_ATENCION);
        BigDecimal ocupacion = capacidad.signum() == 0 ? BigDecimal.ZERO
                : horasHoy.multiply(BigDecimal.valueOf(100)).divide(capacidad, 1, RoundingMode.HALF_UP);

        List<Metrica> lista = new ArrayList<>();
        lista.add(indicador("EP1. Reservas confirmadas para hoy",
                "Reservas CONFIRMADA cuya fecha de uso es hoy.", "Cantidad",
                repository.reservasConfirmadasDelDia(hoy, cancha)));
        lista.add(indicador("EP2. Ocupación de hoy",
                "Horas confirmadas hoy entre las horas disponibles (canchas habilitadas x 14 h).", "Porcentaje (%)",
                ocupacion));
        lista.add(indicador("EP3. Reservas pendientes de uso",
                "Reservas CONFIRMADA que todavía no empiezan.", "Cantidad",
                repository.reservasPendientesDeUso(ahora, cancha)));
        lista.add(indicador("EP4. Saldo retenido de pagos",
                "Pagos registrados menos devoluciones, a la fecha.", "Soles (S/)",
                repository.saldoRetenido(cancha)));
        lista.add(indicador("EP5. Mantenimientos programados",
                "Mantenimientos ACTIVO desde hoy en adelante.", "Cantidad",
                repository.mantenimientosProgramados(hoy, cancha)));
        return lista;
    }

    private Metrica serie(String titulo, String nota, String ejeY, Map<LocalDate, BigDecimal> datos,
            LocalDate desde, LocalDate hasta) {
        List<String> etiquetas = new ArrayList<>();
        List<BigDecimal> valores = new ArrayList<>();
        for (LocalDate dia = desde; !dia.isAfter(hasta); dia = dia.plusDays(1)) {
            etiquetas.add(dia.toString());
            valores.add(datos.getOrDefault(dia, BigDecimal.ZERO));
        }
        return new Metrica(titulo, nota, "line", "Fecha", ejeY, etiquetas, valores);
    }

    private Metrica indicador(String titulo, String nota, String unidad, BigDecimal valor) {
        return new Metrica(titulo, nota, "kpi", "Indicador", unidad, List.of("Valor actual"), List.of(valor));
    }
}
