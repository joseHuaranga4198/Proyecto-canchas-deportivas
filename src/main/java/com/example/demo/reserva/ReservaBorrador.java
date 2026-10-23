package com.example.demo.reserva;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaBorrador implements Serializable {
    private final Long idUsuario;
    private final Long idCancha;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;

    public ReservaBorrador(Long idUsuario, Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        this.idUsuario = idUsuario;
        this.idCancha = idCancha;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Long getIdUsuario() { return idUsuario; }
    public Long getIdCancha() { return idCancha; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
}
