package com.example.demo.metrica;

import java.time.*;
import java.util.*;
import com.example.demo.cancha.CanchaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

@Controller
@RequestMapping("/admin/metricas")
public class MetricasController {
    private final MetricasService service;private final CanchaService canchas;private final Clock clock;
    public MetricasController(MetricasService service,CanchaService canchas,Clock clock){this.service=service;this.canchas=canchas;this.clock=clock;}
    @GetMapping
    public String pagina(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,@RequestParam(required=false) Long canchaId,Model model, jakarta.servlet.http.HttpServletResponse response){
        try {
            hasta=hasta==null?LocalDate.now(clock):hasta;desde=desde==null?hasta.minusDays(29):desde;
            service.validarRango(desde,hasta);
            model.addAttribute("desde",desde);model.addAttribute("hasta",hasta);model.addAttribute("canchaId",canchaId);model.addAttribute("canchas",canchas.listarTodas());return "metrica/metricas";

        } catch (IllegalArgumentException ex) {
            response.setStatus(400);
            model.addAttribute("error", ex.getMessage());
            return "error/general";
        }
    }
    @GetMapping("/datos") @ResponseBody
    public List<Metrica> datos(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,@RequestParam(required=false) Long canchaId){
        try {
            return service.graficos(desde,hasta,canchaId);

        } catch (IllegalArgumentException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
