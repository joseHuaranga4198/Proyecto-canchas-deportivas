package com.example.demo.reserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {
    private Long idPago;

    private Reserva reserva;

    private BigDecimal importe;

    private LocalDateTime fechaPago;

    private String estado;

    public Long getIdPago() { return idPago; }
    public void setIdPago(Long idPago) { this.idPago = idPago; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
