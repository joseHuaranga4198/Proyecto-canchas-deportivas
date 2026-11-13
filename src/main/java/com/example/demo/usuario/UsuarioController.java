package com.example.demo.usuario;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @InitBinder("usuario")
    public void camposRegistro(WebDataBinder binder) {
        binder.setAllowedFields("nombres", "apellidos", "documento", "telefono", "correo", "contrasena");
    }

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String loginView(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) return "redirect:/";
        return "usuario/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam(name = "correo", required = false) String correo,
                                @RequestParam(name = "contrasena", required = false) String contrasena,
                                @RequestParam(name = "password", required = false) String password,
                                HttpSession session, HttpServletRequest request,
                                Model model) {

        String passFinal = (contrasena != null && !contrasena.isBlank()) ? contrasena : password;

        if (correo == null || passFinal == null || correo.isBlank() || passFinal.isBlank()) {
            model.addAttribute("error", "Por favor ingrese su correo y contraseña.");
            return "usuario/login";
        }

        Usuario user = usuarioService.autenticar(correo, passFinal);

        if (user == null) {
            model.addAttribute("error", "Credenciales incorrectas.");
            return "usuario/login";
        }

        if ("BLOQUEADO".equalsIgnoreCase(user.getEstado())) {
            model.addAttribute("error", "Su cuenta se encuentra bloqueada por administración.");
            return "usuario/login";
        }

        request.changeSessionId();
        session.removeAttribute(com.example.demo.seguridad.ProteccionCsrf.ATRIBUTO);
        user.setContrasena(null);
        session.removeAttribute("reservaBorrador");
        session.setAttribute("usuarioLogueado", user);

        if ("ADMINISTRADOR".equalsIgnoreCase(user.getRol())) {
            return "redirect:/admin/panel";
        } else {
            return "redirect:/canchas/catalogo";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/registro")
    public String registroView(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("usuario") Usuario usuario, Model model) {
        boolean registrado;
        try {
            registrado = usuarioService.registrarCliente(usuario);
        } catch (IllegalArgumentException ex) {
            usuario.setContrasena(null);
            model.addAttribute("error", ex.getMessage());
            return "usuario/registro";
        }
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
    public String bloquearCliente(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirect) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"ADMINISTRADOR".equalsIgnoreCase(user.getRol())) {
            return "redirect:/usuario/login";
        }
        try {
            usuarioService.alternarBloqueo(id);
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuario/admin/lista";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario sesion = (Usuario) session.getAttribute("usuarioLogueado");
        return formularioCliente(sesion.getIdUsuario(), true, model);
    }

    @GetMapping("/admin/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        return formularioCliente(id, false, model);
    }

    private String formularioCliente(Long id, boolean perfil, Model model) {
        Usuario cliente = usuarioService.obtenerPorId(id);
        if (cliente == null || !"CLIENTE".equalsIgnoreCase(cliente.getRol())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        cliente.setContrasena(null);
        model.addAttribute("usuario", cliente);
        model.addAttribute("esPerfil", perfil);
        model.addAttribute("idCliente", id);
        return "usuario/formulario";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@ModelAttribute("usuario") Usuario datos, HttpSession session,
            Model model, RedirectAttributes redirect) {
        Usuario sesion = (Usuario) session.getAttribute("usuarioLogueado");
        return actualizar(sesion.getIdUsuario(), datos, true, model, redirect);
    }

    @PostMapping("/admin/editar/{id}")
    public String actualizarAdmin(@PathVariable Long id, @ModelAttribute("usuario") Usuario datos,
            Model model, RedirectAttributes redirect) {
        return actualizar(id, datos, false, model, redirect);
    }

    private String actualizar(Long id, Usuario datos, boolean perfil, Model model, RedirectAttributes redirect) {
        try {
            usuarioService.actualizarCliente(id, datos);
            redirect.addFlashAttribute("mensaje", "Los datos se actualizaron correctamente.");
            return perfil ? "redirect:/usuario/perfil" : "redirect:/usuario/admin/lista";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("esPerfil", perfil);
            model.addAttribute("idCliente", id);
            model.addAttribute("error", ex.getMessage());
            return "usuario/formulario";
        }
    }
}
