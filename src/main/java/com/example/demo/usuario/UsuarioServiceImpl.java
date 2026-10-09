package com.example.demo.usuario;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public Usuario autenticar(String correo, String contrasena) {
        return usuarioDAO.autenticar(correo, contrasena);
    }

    @Override
    public boolean registrarCliente(Usuario usuario) {
        if (usuarioDAO.existeCorreo(usuario.getCorreo()) || usuarioDAO.existeDocumento(usuario.getDocumento())) {
            return false;
        }
        usuario.setRol("CLIENTE");
        usuario.setEstado("ACTIVO");
        return usuarioDAO.guardar(usuario) > 0;
    }

    @Override
    public List<Usuario> listarClientes() {
        return usuarioDAO.listarPorRol("CLIENTE");
    }

    @Override
    public Usuario obtenerPorId(Long idUsuario) {
        return usuarioDAO.obtenerPorId(idUsuario);
    }

    @Override
    public void alternarBloqueo(Long idUsuario) {
        Usuario u = usuarioDAO.obtenerPorId(idUsuario);
        if (u != null) {
            String nuevoEstado = "ACTIVO".equalsIgnoreCase(u.getEstado()) ? "BLOQUEADO" : "ACTIVO";
            usuarioDAO.cambiarEstado(idUsuario, nuevoEstado);
        }
    }
}