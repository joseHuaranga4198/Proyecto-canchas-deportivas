package com.example.demo.reserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.example.demo.cancha.Cancha;
import com.example.demo.usuario.Usuario;

public class Reserva {

    private Long idReserva;
    private Usuario usuario;
    private Cancha cancha;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal importe;
    private String estado;
    private LocalDateTime fechaRegistro;

    public Reserva() {
    }

    public Reserva(Long idReserva, Usuario usuario, Cancha cancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, BigDecimal importe, String estado, LocalDateTime fechaRegistro) {
        this.idReserva = idReserva;
        this.usuario = usuario;
        this.cancha = cancha;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.importe = importe;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Cancha getCancha() {
        return cancha;
    }

    public void setCancha(Cancha cancha) {
        this.cancha = cancha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}