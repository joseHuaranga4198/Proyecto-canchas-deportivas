package com.example.demo.cancha;

import java.math.BigDecimal;
import com.example.demo.categoria.Categoria;

public class Cancha {

    private Long idCancha;
    private Categoria categoria;
    private String nombre;
    private String caracteristicas;
    private BigDecimal precioHora;
    private String estado;

    public Cancha() {
    }

    public Cancha(Long idCancha, Categoria categoria, String nombre, String caracteristicas, BigDecimal precioHora, String estado) {
        this.idCancha = idCancha;
        this.categoria = categoria;
        this.nombre = nombre;
        this.caracteristicas = caracteristicas;
        this.precioHora = precioHora;
        this.estado = estado;
    }

    public Long getIdCancha() {
        return idCancha;
    }

    public void setIdCancha(Long idCancha) {
        this.idCancha = idCancha;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public BigDecimal getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(BigDecimal precioHora) {
        this.precioHora = precioHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}