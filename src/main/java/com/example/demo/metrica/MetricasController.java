package com.example.demo.metrica;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.cancha.CanchaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/admin/metricas")
public class MetricasController {

    private final MetricasService service;
    private final CanchaService canchas;
    private final Clock clock;

    public MetricasController(MetricasService service, CanchaService canchas, Clock clock) {
        this.service = service;
        this.canchas = canchas;
        this.clock = clock;
    }

    @GetMapping
    public String pagina(@RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(required = false) Long canchaId, Model model, HttpServletResponse response) {
        try {
            hasta = hasta == null ? LocalDate.now(clock) : hasta;
            desde = desde == null ? hasta.minusDays(29) : desde;
            service.validarRango(desde, hasta);
            model.addAttribute("desde", desde);
            model.addAttribute("hasta", hasta);
            model.addAttribute("canchaId", canchaId);
            model.addAttribute("canchas", canchas.listarTodas());
            return "metrica/metricas";
        } catch (IllegalArgumentException ex) {
            response.setStatus(400);
            model.addAttribute("error", ex.getMessage());
            return "error/general";
        }
    }

    /** Devuelve en JSON las cinco series de tiempo y los cinco indicadores de estado puntual. */
    @GetMapping("/datos")
    @ResponseBody
    public List<Metrica> datos(@RequestParam LocalDate desde, @RequestParam LocalDate hasta,
            @RequestParam(required = false) Long canchaId) {
        try {
            List<Metrica> todas = new ArrayList<>(service.graficos(desde, hasta, canchaId));
            todas.addAll(service.estadoPuntual(canchaId));
            return todas;
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
