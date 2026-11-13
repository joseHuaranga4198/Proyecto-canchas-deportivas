package com.example.demo.operacion;

import java.time.*;
import com.example.demo.cancha.CanchaService;
import com.example.demo.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class OperacionController {
    private final OperacionService operaciones;private final CanchaService canchas;private final Clock clock;
    public OperacionController(OperacionService operaciones,CanchaService canchas,Clock clock){this.operaciones=operaciones;this.canchas=canchas;this.clock=clock;}
    @GetMapping("/disponibilidad")
    public String disponibilidad(@RequestParam(required=false) Long canchaId,Model model){
        model.addAttribute("canchas",canchas.listarTodas());model.addAttribute("bloqueos",operaciones.bloqueos(canchaId,null));
        model.addAttribute("fechaMinima",LocalDate.now(clock));return "operacion/disponibilidad";
    }
    @PostMapping("/disponibilidad/bloquear")
    public String bloquear(@RequestParam Long canchaId,@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.TIME) LocalTime inicio,@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.TIME) LocalTime fin,
            @RequestParam String motivo,HttpSession session,RedirectAttributes redirect){
        try{operaciones.bloquear(actor(session),canchaId,fecha,inicio,fin,motivo);redirect.addFlashAttribute("mensaje","Horario bloqueado correctamente.");}
        catch(IllegalArgumentException|IllegalStateException ex){redirect.addFlashAttribute("error",ex.getMessage());}
        return "redirect:/admin/disponibilidad";
    }
    @PostMapping("/disponibilidad/anular/{id}")
    public String anular(@PathVariable Long id,HttpSession session,RedirectAttributes redirect){
        try{operaciones.anular(actor(session),id);redirect.addFlashAttribute("mensaje","Bloqueo anulado. El horario vuelve a estar disponible si no tiene reservas.");}
        catch(IllegalArgumentException|IllegalStateException ex){redirect.addFlashAttribute("error",ex.getMessage());}
        return "redirect:/admin/disponibilidad";
    }
    @GetMapping("/pagos")
    public String pagos(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required=false) Long canchaId,@RequestParam(required=false) String estado,Model model){
        hasta=hasta==null?LocalDate.now(clock):hasta;desde=desde==null?hasta.minusDays(29):desde;
        model.addAttribute("desde",desde);model.addAttribute("hasta",hasta);model.addAttribute("canchaId",canchaId);model.addAttribute("estado",estado);
        model.addAttribute("canchas",canchas.listarTodas());
        try{model.addAttribute("pagos",operaciones.pagos(desde,hasta,canchaId,estado));}
        catch(IllegalArgumentException ex){model.addAttribute("error",ex.getMessage());model.addAttribute("pagos",java.util.List.of());}
        return "operacion/pagos";
    }
    @PostMapping("/pagos/devolver/{idReserva}")
    public String devolver(@PathVariable Long idReserva,HttpSession session,RedirectAttributes redirect){
        try{operaciones.regularizarDevolucion(actor(session),idReserva);redirect.addFlashAttribute("mensaje","Devolución simulada registrada.");}
        catch(IllegalArgumentException|IllegalStateException ex){redirect.addFlashAttribute("error",ex.getMessage());}
        return "redirect:/admin/pagos";
    }
    private Long actor(HttpSession session){return ((Usuario)session.getAttribute("usuarioLogueado")).getIdUsuario();}
}
