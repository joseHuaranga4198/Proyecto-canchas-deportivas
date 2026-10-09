package com.example.demo.usuario;

public class Usuario {

    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String documento;
    private String telefono;
    private String correo;
    private String contrasena;
    private String rol;    // "CLIENTE", "ADMINISTRADOR"
    private String estado; // "ACTIVO", "BLOQUEADO"

    public Usuario() {
    }

    public Usuario(Long idUsuario, String nombres, String apellidos, String documento, String telefono, String correo, String contrasena, String rol, String estado) {
        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estado = estado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}