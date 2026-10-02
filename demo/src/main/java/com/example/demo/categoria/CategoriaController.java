package com.example.demo.categoria;

import com.example.demo.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    private boolean validarAdmin(HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        return user != null && "ADMIN".equalsIgnoreCase(user.getRol());
    }

    // F22: Consultar categorías (I11)
    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (!validarAdmin(session)) return "redirect:/usuario/login";
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("nuevaCategoria", new Categoria());
        return "admin/categorias";
    }

    // F21 y F23: Crear o Modificar categoría
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("nuevaCategoria") Categoria categoria, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/usuario/login";
        if (categoria.getId() == null) {
            categoriaService.guardar(categoria);
        } else {
            categoriaService.actualizar(categoria);
        }
        return "redirect:/admin/categorias";
    }

    // F24: Eliminar categoría
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/usuario/login";
        categoriaService.eliminar(id);
        return "redirect:/admin/categorias";
    }
}