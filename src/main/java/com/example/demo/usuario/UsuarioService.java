package com.example.demo.usuario;

import java.util.List;

public interface UsuarioService {

    Usuario autenticar(String correo, String contrasena);

    boolean registrarCliente(Usuario usuario);

    List<Usuario> listarClientes();

    Usuario obtenerPorId(Long idUsuario);

    void alternarBloqueo(Long idUsuario);
}