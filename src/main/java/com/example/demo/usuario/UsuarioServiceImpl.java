package com.example.demo.usuario;

import java.util.List;
import java.util.Locale;
import com.example.demo.validacion.Validaciones;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public Usuario autenticar(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || correo.trim().length() > 100
                || contrasena == null || contrasena.isBlank() || contrasena.length() > 255) return null;
        Usuario usuario = usuarioDAO.autenticar(correo.trim(), contrasena);
        if (usuario == null || !"ACTIVO".equalsIgnoreCase(usuario.getEstado())
                || !("CLIENTE".equalsIgnoreCase(usuario.getRol()) || "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol()))) return null;
        return usuario;
    }

    @Override
    public boolean estaSuspendido(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) return false;
        Usuario usuario = usuarioDAO.autenticar(correo.trim(), contrasena);
        return usuario != null && "SUSPENDIDO".equalsIgnoreCase(usuario.getEstado());
    }

    @Override
    public boolean registrarCliente(Usuario usuario) {
        validarDatosPersonales(usuario);
        if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()
                || usuario.getContrasena().length() < 6 || usuario.getContrasena().length() > 255) {
            throw new IllegalArgumentException("La contraseña debe tener entre 6 y 255 caracteres.");
        }
        if (usuarioDAO.existeCorreo(usuario.getCorreo()) || usuarioDAO.existeDocumento(usuario.getDocumento())) {
            return false;
        }
        usuario.setRol("CLIENTE");
        usuario.setEstado("ACTIVO");
        usuario.setIdUsuario(null);
        try {
            return usuarioDAO.guardar(usuario) > 0;
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }

    @Override
    public List<Usuario> listarClientes() {
        return usuarioDAO.listarPorRol("CLIENTE");
    }

    @Override
    public Usuario obtenerPorId(Long idUsuario) {
        Validaciones.id(idUsuario, "El usuario");
        return usuarioDAO.obtenerPorId(idUsuario);
    }

    @Override
    public void alternarSuspension(Long idUsuario) {
        Validaciones.id(idUsuario, "El cliente");
        Usuario u = usuarioDAO.obtenerPorId(idUsuario);
        if (u == null || !"CLIENTE".equalsIgnoreCase(u.getRol())) {
            throw new IllegalArgumentException("No se encontró el cliente.");
        }
        String nuevoEstado = "ACTIVO".equalsIgnoreCase(u.getEstado()) ? "SUSPENDIDO" : "ACTIVO";
        usuarioDAO.cambiarEstado(idUsuario, nuevoEstado);
    }

    @Override
    public void actualizarCliente(Long idUsuario, Usuario datos) {
        Usuario actual = obtenerPorId(idUsuario);
        if (actual == null || !"CLIENTE".equalsIgnoreCase(actual.getRol())) {
            throw new IllegalArgumentException("No se encontró el cliente.");
        }
        validarDatosPersonales(datos);
        if (usuarioDAO.datosDuplicadosEnOtroUsuario(idUsuario, datos.getCorreo(), datos.getDocumento())) {
            throw new IllegalArgumentException("El correo o documento ya pertenece a otro usuario.");
        }
        try {
            if (usuarioDAO.actualizarDatos(idUsuario, datos) != 1) throw new IllegalArgumentException("No se pudo actualizar el cliente.");
        } catch (DuplicateKeyException ex) {
            throw new IllegalArgumentException("El correo o documento ya pertenece a otro usuario.");
        }
    }
    private void validarDatosPersonales(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Los datos del cliente son obligatorios.");
        usuario.setNombres(Validaciones.texto(usuario.getNombres(), "Nombres", 100, true));
        usuario.setApellidos(Validaciones.texto(usuario.getApellidos(), "Apellidos", 100, true));
        usuario.setDocumento(Validaciones.texto(usuario.getDocumento(), "Documento", 20, true));
        usuario.setTelefono(Validaciones.texto(usuario.getTelefono(), "Teléfono", 20, false));
        usuario.setCorreo(Validaciones.texto(usuario.getCorreo(), "Correo", 100, true).toLowerCase(Locale.ROOT));
        if (!usuario.getCorreo().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Ingresa un correo electrónico válido.");
        }
        if (!usuario.getDocumento().matches("[\\p{L}\\p{N}-]{3,20}")) {
            throw new IllegalArgumentException("El documento debe tener entre 3 y 20 letras, números o guiones.");
        }
        if (!usuario.getTelefono().isEmpty() && !usuario.getTelefono().matches("[+0-9() -]{6,20}")) {
            throw new IllegalArgumentException("El teléfono no tiene un formato válido.");
        }
    }

}
