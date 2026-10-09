package com.example.demo.usuario;

import java.util.List;

public interface UsuarioDAO {

    Usuario autenticar(String correo, String contrasena);

    Usuario obtenerPorId(Long idUsuario);

    List<Usuario> listarPorRol(String rol);

    int guardar(Usuario usuario);

    int cambiarEstado(Long idUsuario, String estado);

    boolean existeCorreo(String correo);

    boolean existeDocumento(String documento);
}