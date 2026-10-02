package com.example.demo.cancha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/canchas")
public class CanchaController {

    @Autowired
    private CanchaService canchaService;

    // I03: Catálogo para clientes
    @GetMapping("/catalogo")
    public String verCatalogo(Model model) {
        model.addAttribute("canchas", canchaService.listarDisponibles());
        return "cancha/catalogo";
    }

    // I10: Gestión administrativa de canchas
    @GetMapping("/admin")
    public String listarAdmin(Model model) {
        model.addAttribute("canchas", canchaService.listarTodas());
        return "cancha/gestion";
    }

    // I11: Formulario nuevo
    @GetMapping("/admin/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("cancha", new Cancha());
        return "cancha/formulario";
    }

    // Guardar cancha
    @PostMapping("/admin/guardar")
    public String guardarCancha(@ModelAttribute("cancha") Cancha cancha) {
        if (cancha.getId() == null) {
            canchaService.guardar(cancha);
        } else {
            canchaService.actualizar(cancha);
        }
        return "redirect:/canchas/admin";
    }

    // I12: Editar cancha
    @GetMapping("/admin/editar/{id}")
    public String editarCancha(@PathVariable Long id, Model model) {
        Cancha cancha = canchaService.obtenerPorId(id);
        model.addAttribute("cancha", cancha);
        return "cancha/formulario";
    }

    // I13: Inactivar/Cambiar estado
    @PostMapping("/admin/estado/{id}")
    public String cambiarEstado(@PathVariable Long id, @RequestParam("estado") String estado) {
        canchaService.cambiarEstado(id, estado);
        return "redirect:/canchas/admin";
    }
}