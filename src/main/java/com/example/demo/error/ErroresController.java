package com.example.demo.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErroresController implements ErrorController {
    @RequestMapping("/error")
    public String error(HttpServletRequest request, HttpServletResponse response,org.springframework.ui.Model model) {
        Object atributo = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int codigo = atributo instanceof Integer ? (Integer) atributo : 404;
        response.setStatus(codigo);
        if(codigo==403)model.addAttribute("error","Formulario vencido o inválido. Recarga la página e inténtalo de nuevo.");
        if(codigo==400)model.addAttribute("error","Revisa el formato de los datos enviados.");
        return codigo == 404 ? "error/404" : "error/general";
    }
}
