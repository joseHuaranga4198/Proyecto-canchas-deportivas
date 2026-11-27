package com.example.demo.operacion;

import com.example.demo.cancha.CanchaDAO;
import com.example.demo.reserva.Reserva;
import com.example.demo.reserva.ReservaDAO;
import com.example.demo.reserva.ReservaService;
import com.example.demo.usuario.Usuario;
import com.example.demo.usuario.UsuarioDAO;
import com.example.demo.validacion.Validaciones;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperacionServiceImpl implements OperacionService {
    private final OperacionDAO operaciones;
    private final ReservaDAO reservas;
    private final CanchaDAO canchas;
    private final UsuarioDAO usuarios;
    private final ReservaService reservaService;

    public OperacionServiceImpl(OperacionDAO operaciones, ReservaDAO reservas, CanchaDAO canchas,
            UsuarioDAO usuarios, ReservaService reservaService) {
        this.operaciones = operaciones;
        this.reservas = reservas;
        this.canchas = canchas;
        this.usuarios = usuarios;
        this.reservaService = reservaService;
    }

    @Override
    @Transactional
    public void programarMantenimiento(Long admin, Long cancha, LocalDate fecha, LocalTime inicio, LocalTime fin, String motivo) {
        validarAdmin(admin);
        Validaciones.id(cancha, "La cancha");
        reservaService.validarIntervaloFuturo(fecha, inicio, fin);
        if (!inicio.equals(inicio.truncatedTo(ChronoUnit.MINUTES))
                || !fin.equals(fin.truncatedTo(ChronoUnit.MINUTES))) {
            throw new IllegalArgumentException("El mantenimiento se programa con precisión de minutos.");
        }
        motivo = Validaciones.texto(motivo, "Motivo", 250, true);

        reservas.bloquearCancha(cancha);
        if (canchas.obtenerPorId(cancha) == null) {
            throw new IllegalArgumentException("La cancha no existe.");
        }
        if (!reservas.buscarSuperpuestas(cancha, fecha, inicio, fin).isEmpty()) {
            throw new IllegalStateException(
                    "Hay reservas en ese horario. Cancélalas y devuelve el pago antes de programar el mantenimiento.");
        }
        if (operaciones.bloqueada(cancha, fecha, inicio, fin)) {
            throw new IllegalStateException("Ya existe un mantenimiento en ese horario.");
        }
        operaciones.guardarBloqueo(cancha, fecha, inicio, fin, motivo, admin);
    }

    @Override
    @Transactional
    public void anularMantenimiento(Long admin, Long id) {
        validarAdmin(admin);
        Validaciones.id(id, "El bloqueo");
        Long cancha = operaciones.canchaBloqueo(id);
        if (cancha == null) {
            throw new IllegalArgumentException("No se encontró el mantenimiento.");
        }
        reservas.bloquearCancha(cancha);
        if (operaciones.anularBloqueo(id) != 1) {
            throw new IllegalStateException("El mantenimiento ya fue anulado.");
        }
    }

    @Override
    public List<Map<String, Object>> bloqueos(Long cancha, LocalDate fecha) {
        if (cancha != null) Validaciones.id(cancha, "La cancha");
        return operaciones.bloqueos(cancha, fecha);
    }

    @Override
    public Map<String, Object> devolucion(Long reserva) {
        return operaciones.devolucion(Validaciones.id(reserva, "La reserva"));
    }

    @Override
    public List<Map<String, Object>> pagos(LocalDate desde, LocalDate hasta, Long cancha, String estado) {
        validarRango(desde, hasta);
        if (cancha != null) Validaciones.id(cancha, "La cancha");
        if (estado != null && !estado.isBlank()) {
            estado = Validaciones.estado(estado, null, "DEVUELTO", "SIN_DEVOLUCION");
        }
        return operaciones.pagos(desde, hasta, cancha, estado);
    }

    @Override
    public void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta) || ChronoUnit.DAYS.between(desde, hasta) > 366) {
            throw new IllegalArgumentException("Selecciona un rango ordenado de hasta 366 días.");
        }
    }

    private void validarAdmin(Long id) {
        Usuario usuario = usuarios.obtenerPorId(Validaciones.id(id, "El administrador"));
        if (usuario == null || !"ADMINISTRADOR".equals(usuario.getRol()) || !"ACTIVO".equals(usuario.getEstado())) {
            throw new IllegalStateException("Se requiere un administrador activo.");
        }
    }
}
