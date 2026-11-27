package com.example.demo.usuario;

import java.util.List;

public interface UsuarioService {

    Usuario autenticar(String correo, String contrasena);

    boolean estaSuspendido(String correo, String contrasena);

    boolean registrarCliente(Usuario usuario);

    List<Usuario> listarClientes();

    Usuario obtenerPorId(Long idUsuario);

    void alternarSuspension(Long idUsuario);

    void actualizarCliente(Long idUsuario, Usuario datos);
}
