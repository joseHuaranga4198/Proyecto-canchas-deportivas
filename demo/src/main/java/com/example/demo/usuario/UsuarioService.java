package com.example.demo.usuario;

import java.util.List;

public interface UsuarioService {
    Usuario autenticar(String correo, String password);
    boolean registrarCliente(Usuario usuario);
    List<Usuario> listarClientes();
    Usuario obtenerPorId(Long id);
    void alternarBloqueo(Long id);
}