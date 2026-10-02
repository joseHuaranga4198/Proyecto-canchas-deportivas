package com.example.demo.usuario;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // I01: Vista de Login
    @GetMapping("/login")
    public String loginView() {
        return "usuario/login";
    }

    // Procesamiento de Login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("correo") String correo,
                                @RequestParam("password") String password,
                                HttpSession session,
                                Model model) {
        Usuario user = usuarioService.autenticar(correo, password);

        if (user == null) {
            model.addAttribute("error", "Credenciales inválidas. Verifica tu correo o contraseña.");
            return "usuario/login";
        }

        if ("BLOQUEADO".equalsIgnoreCase(user.getEstado())) {
            model.addAttribute("error", "Tu cuenta se encuentra bloqueada por administración.");
            return "usuario/login";
        }

        // Guardar sesión
        session.setAttribute("usuarioLogueado", user);

        if ("ADMIN".equalsIgnoreCase(user.getRol())) {
            return "redirect:/admin/panel";
        } else {
            return "redirect:/canchas/catalogo";
        }
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuario/login";
    }

    // I02: Registro de nuevo cliente
    @GetMapping("/registro")
    public String registroView(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/registro";
    }

    // Guardar cliente
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("usuario") Usuario usuario, Model model) {
        boolean registrado = usuarioService.registrarCliente(usuario);
        if (!registrado) {
            model.addAttribute("error", "El correo ingresado ya se encuentra en uso.");
            return "usuario/registro";
        }
        return "redirect:/usuario/login?registrado=true";
    }

    // I14: Gestión de clientes para admin
    @GetMapping("/admin/lista")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", usuarioService.listarClientes());
        return "usuario/gestion-clientes";
    }

    // I14: Bloquear o desbloquear cliente
    @PostMapping("/admin/bloquear/{id}")
    public String bloquearCliente(@PathVariable Long id) {
        usuarioService.alternarBloqueo(id);
        return "redirect:/usuario/admin/lista";
    }
}