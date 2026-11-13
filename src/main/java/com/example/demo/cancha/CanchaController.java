package com.example.demo.cancha;

import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.demo.categoria.Categoria;
import com.example.demo.categoria.CategoriaService;

@Controller
@RequestMapping("/canchas")
public class CanchaController {

    private final CanchaService canchaService;
    private final CategoriaService categoriaService;

    public CanchaController(CanchaService canchaService, CategoriaService categoriaService) {
        this.canchaService = canchaService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("canchas", canchaService.listarDisponibles());
        return "cancha/catalogo";
    }

    @GetMapping("/admin")
    public String listarAdmin(Model model) {
        model.addAttribute("canchas", canchaService.listarTodas());
        return "cancha/gestion";
    }

    @GetMapping("/admin/nueva")
    public String nuevaCancha(Model model) {
        model.addAttribute("cancha", new Cancha());
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "cancha/formulario";
    }

    @PostMapping("/admin/guardar")
    public String guardarCancha(@RequestParam(name = "idCancha", required = false) Long idCancha,
                                @RequestParam("nombre") String nombre,
                                @RequestParam("idCategoria") Long idCategoria,
                                @RequestParam("precioHora") BigDecimal precioHora,
                                @RequestParam("caracteristicas") String caracteristicas,
                                @RequestParam("estado") String estado, Model model) {

        Categoria cat = categoriaService.obtenerPorId(idCategoria);
        Cancha cancha = (idCancha != null) ? canchaService.obtenerPorId(idCancha) : new Cancha();
        if (cancha == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la cancha.");
        cancha.setNombre(nombre);
        cancha.setCategoria(cat);
        cancha.setPrecioHora(precioHora);
        cancha.setCaracteristicas(caracteristicas);
        cancha.setEstado(estado);

        try {
            if (idCancha == null) canchaService.guardar(cancha);
            else canchaService.actualizar(cancha);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("cancha", cancha);
            model.addAttribute("categorias", categoriaService.listarActivas());
            return "cancha/formulario";
        }
        return "redirect:/canchas/admin";
    }

    @GetMapping("/admin/editar/{id}")
    public String editarCancha(@PathVariable("id") Long id, Model model) {
        Cancha cancha = canchaService.obtenerPorId(id);
        if (cancha == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la cancha.");
        model.addAttribute("cancha", cancha);
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "cancha/formulario";
    }

    @PostMapping("/admin/estado/{id}")
    public String cambiarEstado(@PathVariable("id") Long id, @RequestParam("estado") String estado, RedirectAttributes redirect) {
        try {
            canchaService.cambiarEstado(id, estado);
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/canchas/admin";
    }
}
