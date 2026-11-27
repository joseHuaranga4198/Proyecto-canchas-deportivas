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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    public String listar(HttpSession session, Model model, jakarta.servlet.http.HttpServletResponse response) {
        try {
            if (!tienePermisoAdmin(session)) {
                return "redirect:/usuario/login";
            }
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("nuevaCategoria", new Categoria());
            return "categoria/categorias";

        } catch (IllegalArgumentException ex) {
            response.setStatus(400);
            model.addAttribute("error", ex.getMessage());
            return "error/general";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("nuevaCategoria") Categoria categoria, HttpSession session, Model model, jakarta.servlet.http.HttpServletResponse response) {
        try {
            if (!tienePermisoAdmin(session)) {
                return "redirect:/usuario/login";
            }
            try {
                if (categoria.getIdCategoria() == null) categoriaService.guardar(categoria);
                else categoriaService.actualizar(categoria);
            } catch (IllegalArgumentException ex) {
                model.addAttribute("error", ex.getMessage());
                model.addAttribute("categorias", categoriaService.listarTodas());
                return "categoria/categorias";
            }
            return "redirect:/admin/categorias";

        } catch (IllegalArgumentException ex) {
            response.setStatus(400);
            model.addAttribute("error", ex.getMessage());
            return "error/general";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, jakarta.servlet.http.HttpServletResponse response) {
        try {
            Categoria categoria = categoriaService.obtenerPorId(id);
            if (categoria == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            model.addAttribute("nuevaCategoria", categoria);
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "categoria/categorias";

        } catch (IllegalArgumentException ex) {
            response.setStatus(400);
            model.addAttribute("error", ex.getMessage());
            return "error/general";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirect) {
        try {
            if (!tienePermisoAdmin(session)) {
                return "redirect:/usuario/login";
            }
            try {
                if (!categoriaService.eliminar(id)) redirect.addFlashAttribute("error", "No se puede eliminar una categoría con canchas asociadas.");
            } catch (IllegalArgumentException ex) {
                redirect.addFlashAttribute("error", ex.getMessage());
            }
            return "redirect:/admin/categorias";

        } catch (IllegalArgumentException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
