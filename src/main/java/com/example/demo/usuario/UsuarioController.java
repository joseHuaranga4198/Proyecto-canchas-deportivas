package com.example.demo.usuario;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String loginView() {
        return "usuario/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam(name = "correo", required = false) String correo,
                                @RequestParam(name = "contrasena", required = false) String contrasena,
                                @RequestParam(name = "password", required = false) String password,
                                HttpSession session,
                                Model model) {

        String passFinal = (contrasena != null && !contrasena.isBlank()) ? contrasena : password;

        if (correo == null || passFinal == null || correo.isBlank() || passFinal.isBlank()) {
            model.addAttribute("error", "Por favor ingrese su correo y contraseña.");
            return "usuario/login";
        }

        Usuario user = usuarioService.autenticar(correo.trim(), passFinal.trim());

        if (user == null) {
            model.addAttribute("error", "Credenciales incorrectas.");
            return "usuario/login";
        }

        if ("BLOQUEADO".equalsIgnoreCase(user.getEstado())) {
            model.addAttribute("error", "Su cuenta se encuentra bloqueada por administración.");
            return "usuario/login";
        }

        session.setAttribute("usuarioLogueado", user);

        if ("ADMINISTRADOR".equalsIgnoreCase(user.getRol())) {
            return "redirect:/admin/panel";
        } else {
            return "redirect:/canchas/catalogo";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuario/login";
    }

    @GetMapping("/registro")
    public String registroView(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("usuario") Usuario usuario, Model model) {
        boolean registrado = usuarioService.registrarCliente(usuario);
        if (!registrado) {
            model.addAttribute("error", "El correo o documento ya se encuentra registrado.");
            return "usuario/registro";
        }
        return "redirect:/usuario/login?registrado=true";
    }

    @GetMapping("/admin/lista")
    public String listarClientes(HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"ADMINISTRADOR".equalsIgnoreCase(user.getRol())) {
            return "redirect:/usuario/login";
        }
        model.addAttribute("clientes", usuarioService.listarClientes());
        return "usuario/gestion-clientes";
    }

    @PostMapping("/admin/bloquear/{id}")
    public String bloquearCliente(@PathVariable("id") Long id, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"ADMINISTRADOR".equalsIgnoreCase(user.getRol())) {
            return "redirect:/usuario/login";
        }
        usuarioService.alternarBloqueo(id);
        return "redirect:/usuario/admin/lista";
    }
}