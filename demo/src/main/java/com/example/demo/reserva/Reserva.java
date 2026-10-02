package com.example.demo.reserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private Long id;
    private Long usuarioId;
    private String clienteNombre;
    private Long canchaId;
    private String canchaNombre;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal montoTotal;
    private String estado; // "CONFIRMADA", "CANCELADA"

    public Reserva() {}

    public Reserva(Long id, Long usuarioId, String clienteNombre, Long canchaId, String canchaNombre,
                   LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, BigDecimal montoTotal, String estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.clienteNombre = clienteNombre;
        this.canchaId = canchaId;
        this.canchaNombre = canchaNombre;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.montoTotal = montoTotal;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public Long getCanchaId() { return canchaId; }
    public void setCanchaId(Long canchaId) { this.canchaId = canchaId; }

    public String getCanchaNombre() { return canchaNombre; }
    public void setCanchaNombre(String canchaNombre) { this.canchaNombre = canchaNombre; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}