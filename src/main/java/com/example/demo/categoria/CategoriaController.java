package com.example.demo.categoria;

import com.example.demo.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    private boolean tienePermisoAdmin(HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        return user != null && "ADMINISTRADOR".equalsIgnoreCase(user.getRol());
    }

    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (!tienePermisoAdmin(session)) {
            return "redirect:/usuario/login";
        }
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("nuevaCategoria", new Categoria());
        return "admin/categorias";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("nuevaCategoria") Categoria categoria, HttpSession session) {
        if (!tienePermisoAdmin(session)) {
            return "redirect:/usuario/login";
        }
        if (categoria.getIdCategoria() == null) {
            categoriaService.guardar(categoria);
        } else {
            categoriaService.actualizar(categoria);
        }
        return "redirect:/admin/categorias";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id, HttpSession session) {
        if (!tienePermisoAdmin(session)) {
            return "redirect:/usuario/login";
        }
        categoriaService.eliminar(id);
        return "redirect:/admin/categorias";
    }
}