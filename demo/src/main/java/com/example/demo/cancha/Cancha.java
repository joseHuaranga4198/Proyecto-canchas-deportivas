package com.example.demo.cancha;

import java.math.BigDecimal;

public class Cancha {
    private Long id;
    private String nombre;
    private String categoria;
    private String caracteristicas;
    private BigDecimal precioHora;
    private String estado; // "DISPONIBLE", "MANTENIMIENTO", "INACTIVO"

    public Cancha() {}

    public Cancha(Long id, String nombre, String categoria, String caracteristicas, BigDecimal precioHora, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.caracteristicas = caracteristicas;
        this.precioHora = precioHora;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(String caracteristicas) { this.caracteristicas = caracteristicas; }

    public BigDecimal getPrecioHora() { return precioHora; }
    public void setPrecioHora(BigDecimal precioHora) { this.precioHora = precioHora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}