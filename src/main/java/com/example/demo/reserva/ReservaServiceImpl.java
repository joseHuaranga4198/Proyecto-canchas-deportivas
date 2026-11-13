package com.example.demo.reserva;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.cancha.Cancha;
import com.example.demo.operacion.OperacionDAO;
import java.util.ArrayList;
import java.util.Map;
import com.example.demo.cancha.CanchaDAO;
import com.example.demo.usuario.Usuario;
import com.example.demo.usuario.UsuarioDAO;
import com.example.demo.validacion.Validaciones;

@Service
public class ReservaServiceImpl implements ReservaService {
    private static final LocalTime APERTURA = LocalTime.of(8, 0);
    private static final LocalTime CIERRE = LocalTime.of(22, 0);

    private final ReservaDAO reservas;
    private final CanchaDAO canchas;
    private final UsuarioDAO usuarios;
    private final Clock clock;
    private final OperacionDAO operaciones;

    public ReservaServiceImpl(ReservaDAO reservas, CanchaDAO canchas, UsuarioDAO usuarios, Clock clock,
            OperacionDAO operaciones) {
        this.reservas = reservas;
        this.canchas = canchas;
        this.usuarios = usuarios;
        this.clock = clock;
        this.operaciones = operaciones;
    }

    @Override
    public List<Reserva> listarCliente(Long idUsuario, String estado) {
        validarCliente(idUsuario);
        if (estado == null || estado.isBlank()) return reservas.listarPorUsuario(idUsuario);
        return reservas.listarPorUsuarioYEstado(idUsuario, Validaciones.estado(estado, null, "PENDIENTE", "CONFIRMADA", "CANCELADA"));
    }

    @Override
    public Reserva detallePropio(Long idReserva, Long idUsuario) {
        Validaciones.id(idReserva, "La reserva");
        validarCliente(idUsuario);
        Reserva reserva = reservas.obtenerPorId(idReserva);
        if (reserva == null || !reserva.getUsuario().getIdUsuario().equals(idUsuario)) return null;
        return reserva;
    }

    @Override
    public Reserva detalleAdmin(Long idReserva) {
        Validaciones.id(idReserva, "La reserva");
        return reservas.obtenerPorId(idReserva);
    }

    @Override
    public long duracionMinutos(Reserva reserva) {
        return Duration.between(reserva.getHoraInicio(), reserva.getHoraFin()).toMinutes();
    }

    @Override
    public boolean estaDisponible(Long idCancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        Validaciones.id(idCancha, "La cancha");
        validarHorario(inicio, fin);
        if (fecha == null) throw new IllegalArgumentException("La fecha es obligatoria.");
        Cancha cancha = canchas.obtenerPorId(idCancha);
        if (cancha == null || !"ACTIVA".equalsIgnoreCase(cancha.getEstado()) || cancha.getCategoria() == null
                || !"ACTIVA".equalsIgnoreCase(cancha.getCategoria().getEstado())) return false;
        return reservas.buscarSuperpuestas(idCancha, fecha, inicio, fin).isEmpty()
                && !operaciones.bloqueada(idCancha,fecha,inicio,fin);
    }

    @Override
    public boolean puedeCancelar(Reserva reserva) {
        if (reserva == null || !"CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) return false;
        LocalDateTime inicio = LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio());
        return Duration.between(LocalDateTime.now(clock), inicio).compareTo(Duration.ofHours(24)) >= 0;
    }

    @Override
    public boolean puedeCancelarAdmin(Reserva reserva) {
        return reserva != null && "CONFIRMADA".equalsIgnoreCase(reserva.getEstado());
    }

    @Override
    public List<Reserva> buscarAdmin(Long idReserva, String cliente, String cancha, String estado,
            LocalDate fechaDesde, LocalDate fechaHasta) {
        if (idReserva != null) Validaciones.id(idReserva, "La reserva");
        if (estado != null && !estado.isBlank()) estado = Validaciones.estado(estado, null, "PENDIENTE", "CONFIRMADA", "CANCELADA");
        Validaciones.texto(cliente, "Cliente", 200, false);
        Validaciones.texto(cancha, "Cancha", 100, false);
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta.");
        }
        return reservas.buscarAdministrativamente(idReserva, vacioANull(cliente), vacioANull(cancha),
                vacioANull(estado), fechaDesde, fechaHasta);
    }

    private String vacioANull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    @Transactional
    @Override
    public Reserva registrarReservaPagada(Long idUsuario, Long idCancha, LocalDate fecha,
            LocalTime inicio, LocalTime fin) {
        Validaciones.id(idCancha, "La cancha");
        validarHorarioFuturo(fecha, inicio, fin);

        // Bloquea la cancha hasta terminar la transacción para serializar reservas simultáneas.
        reservas.bloquearCancha(idCancha);
        validarSolicitud(idUsuario, idCancha, fecha, inicio, fin);
        Usuario usuario = validarCliente(idUsuario);
        Cancha cancha = canchas.obtenerPorId(idCancha);

        BigDecimal importe = calcularImporte(cancha, inicio, fin);
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(inicio);
        reserva.setHoraFin(fin);
        reserva.setImporte(importe);
        reserva.setEstado("CONFIRMADA");
        Long idGenerado = reservas.guardar(reserva);
        if (idGenerado == null) throw new IllegalStateException("No se pudo registrar la reserva.");
        if (reservas.registrarPago(idGenerado, importe) != 1) throw new IllegalStateException("No se pudo registrar el pago.");
        return reservas.obtenerPorId(idGenerado);
    }

    @Transactional
    @Override
    public void cancelarPropia(Long idReserva, Long idUsuario) {
        Validaciones.id(idReserva, "La reserva");
        validarCliente(idUsuario);
        Reserva reserva = reservas.obtenerPorId(idReserva);
        if (reserva == null) throw new IllegalArgumentException("No se encontró la reserva.");
        reservas.bloquearCancha(reserva.getCancha().getIdCancha());
        reserva = reservas.obtenerPorId(idReserva);
        if (!reserva.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("No puedes cancelar una reserva de otro cliente.");
        }
        if (!"CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) {
            throw new IllegalStateException("La reserva no está en estado cancelable.");
        }
        if (!puedeCancelar(reserva)) {
            throw new IllegalStateException("Solo puedes cancelar hasta 24 horas antes del inicio.");
        }
        reservas.cambiarEstado(idReserva, "CANCELADA");
        operaciones.devolver(idReserva,idUsuario,"Cancelación solicitada por el cliente (simulada)");
    }

    @Transactional
    @Override
    public void cancelarAdmin(Long idReserva, Long idAdmin) {
        Usuario admin=usuarios.obtenerPorId(Validaciones.id(idAdmin,"El administrador"));
        if(admin==null||!"ADMINISTRADOR".equals(admin.getRol())||!"ACTIVO".equals(admin.getEstado()))
            throw new IllegalStateException("Se requiere un administrador activo.");
        Validaciones.id(idReserva, "La reserva");
        Reserva reserva = reservas.obtenerPorId(idReserva);
        if (reserva == null) throw new IllegalArgumentException("No se encontró la reserva.");
        reservas.bloquearCancha(reserva.getCancha().getIdCancha());
        reserva = reservas.obtenerPorId(idReserva);
        if (!puedeCancelarAdmin(reserva)) {
            throw new IllegalStateException("Solo se pueden cancelar reservas confirmadas.");
        }
        reservas.cambiarEstado(idReserva, "CANCELADA");
        operaciones.devolver(idReserva,idAdmin,"Cancelación administrativa (simulada)");
    }

    /** Valida el borrador sin persistir; se repite dentro de la transacción de pago. */
    @Override
    public void validarSolicitud(Long idUsuario, Long idCancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        validarCliente(idUsuario);
        Validaciones.id(idCancha, "La cancha");
        validarHorarioFuturo(fecha, inicio, fin);
        Cancha cancha = canchas.obtenerPorId(idCancha);
        if (cancha == null || !"ACTIVA".equalsIgnoreCase(cancha.getEstado())) {
            throw new IllegalStateException("La cancha se encuentra inactiva o no existe.");
        }
        Validaciones.precio(cancha.getPrecioHora());
        if (cancha.getCategoria() == null || !"ACTIVA".equalsIgnoreCase(cancha.getCategoria().getEstado())) {
            throw new IllegalStateException("La categoría de la cancha se encuentra inactiva.");
        }
        if (!estaDisponible(idCancha, fecha, inicio, fin)) throw new IllegalStateException("El horario solicitado ya se encuentra ocupado.");
    }

    private Usuario validarCliente(Long idUsuario) {
        Validaciones.id(idUsuario, "El cliente");
        Usuario usuario = usuarios.obtenerPorId(idUsuario);
        if (usuario == null || !"CLIENTE".equalsIgnoreCase(usuario.getRol()) || !"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            throw new IllegalStateException("El cliente está bloqueado, inactivo o no existe.");
        }
        return usuario;
    }
    @Override
    public List<Reserva> listarTodas() {
        return reservas.listarTodas();
    }

    @Override
    public List<Reserva> listarPorUsuario(Long idUsuario) {
        Validaciones.id(idUsuario, "El cliente");
        return reservas.listarPorUsuario(idUsuario);
    }

    @Override
    public Reserva obtenerPorId(Long idReserva) {
        Validaciones.id(idReserva, "La reserva");
        return reservas.obtenerPorId(idReserva);
    }

    @Override
    public BigDecimal calcularMonto(Long idCancha, LocalTime horaInicio, LocalTime horaFin) {
        Validaciones.id(idCancha, "La cancha");
        validarHorario(horaInicio, horaFin);
        Cancha c = canchas.obtenerPorId(idCancha);
        if (c == null) throw new IllegalArgumentException("No se encontró la cancha.");
        return calcularImporte(c, horaInicio, horaFin);
    }

    private BigDecimal calcularImporte(Cancha cancha, LocalTime inicio, LocalTime fin) {
        BigDecimal precioHora = Validaciones.precio(cancha.getPrecioHora());
        long minutos = Duration.between(inicio, fin).toMinutes();
        return precioHora.multiply(BigDecimal.valueOf(minutos))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }


    @Override
    public List<Map<String, Object>> turnos(Long idCancha, LocalDate fecha) {
        List<Reserva> ocupadas = reservas.buscarSuperpuestas(idCancha, fecha, APERTURA, CIERRE);
        List<Map<String, Object>> bloqueos = operaciones.bloqueos(idCancha, fecha);
        List<Map<String, Object>> turnos = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now(clock);
        for (LocalTime inicio = APERTURA; inicio.isBefore(CIERRE); inicio = inicio.plusHours(1)) {
            LocalTime horaInicio = inicio;
            LocalTime fin = inicio.plusHours(1);
            String estado = "Disponible";
            if (!LocalDateTime.of(fecha, inicio).isAfter(ahora)) {
                estado = "Pasado";
            } else if (bloqueos.stream().anyMatch(b -> horaInicio.isBefore((LocalTime) b.get("fin"))
                    && fin.isAfter((LocalTime) b.get("inicio")))) {
                estado = "Mantenimiento";
            } else if (ocupadas.stream().anyMatch(r -> horaInicio.isBefore(r.getHoraFin())
                    && fin.isAfter(r.getHoraInicio()))) {
                estado = "Reservado";
            }
            turnos.add(Map.of("inicio", inicio, "fin", fin, "estado", estado));
        }
        return turnos;
    }

    private void validarIntervalo(LocalTime inicio, LocalTime fin) {
        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio.");
        }
        if (inicio.isBefore(APERTURA) || fin.isAfter(CIERRE)) {
            throw new IllegalArgumentException("El horario de atención es de 08:00 a 22:00.");
        }
    }

    private void validarHorario(LocalTime inicio, LocalTime fin) {
        validarIntervalo(inicio, fin);
        if (inicio.getMinute() != 0 || inicio.getSecond() != 0 || inicio.getNano() != 0
                || fin.getMinute() != 0 || fin.getSecond() != 0 || fin.getNano() != 0) {
            throw new IllegalArgumentException("Las reservas deben realizarse en bloques de una hora y comenzar en horas exactas.");
        }
    }

    private void validarHorarioFuturo(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        validarHorario(inicio, fin);
        validarInicioFuturo(fecha, inicio);
    }

    @Override
    public void validarIntervaloFuturo(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        validarIntervalo(inicio, fin);
        validarInicioFuturo(fecha, inicio);
    }

    private void validarInicioFuturo(LocalDate fecha, LocalTime inicio) {
        if (fecha == null || !LocalDateTime.of(fecha, inicio).isAfter(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("La fecha y hora de inicio deben estar en el futuro.");
        }
    }
}
