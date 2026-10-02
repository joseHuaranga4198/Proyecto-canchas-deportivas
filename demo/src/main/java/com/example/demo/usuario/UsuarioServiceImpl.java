package com.example.demo.usuario;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();

    public UsuarioServiceImpl() {
        // Cuentas iniciales de prueba
        usuarios.add(new Usuario(1L, "Carlos", "Escobar", "cliente@canchas.pe", "123456", "987654321", "CLIENTE", "ACTIVO"));
        usuarios.add(new Usuario(2L, "Admin", "General", "admin@canchas.pe", "admin123", "999888777", "ADMIN", "ACTIVO"));
    }

    @Override
    public Usuario autenticar(String correo, String password) {
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .filter(u -> u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean registrarCliente(Usuario usuario) {
        boolean yaExiste = usuarios.stream()
                .anyMatch(u -> u.getCorreo().equalsIgnoreCase(usuario.getCorreo()));
        if (yaExiste) {
            return false;
        }
        usuario.setId((long) (usuarios.size() + 1));
        usuario.setRol("CLIENTE");
        usuario.setEstado("ACTIVO");
        usuarios.add(usuario);
        return true;
    }

    @Override
    public List<Usuario> listarClientes() {
        return usuarios.stream()
                .filter(u -> "CLIENTE".equalsIgnoreCase(u.getRol()))
                .collect(Collectors.toList());
    }

    @Override
    public Usuario obtenerPorId(Long id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void alternarBloqueo(Long id) {
        Usuario u = obtenerPorId(id);
        if (u != null) {
            u.setEstado("ACTIVO".equalsIgnoreCase(u.getEstado()) ? "BLOQUEADO" : "ACTIVO");
        }
    }
}