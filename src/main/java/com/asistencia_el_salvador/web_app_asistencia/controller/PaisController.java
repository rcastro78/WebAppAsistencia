package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.Afiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.Pais;
import com.asistencia_el_salvador.web_app_asistencia.service.PaisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pais")
public class PaisController {

    private final PaisService paisService;

    public PaisController(PaisService paisService) {
        this.paisService = paisService;
    }

    // Listar países
    @GetMapping
    public String listar(Model model) {
        List<Pais> paises = paisService.listarTodos();
        model.addAttribute("paises", paises);
        return "pais/listar"; // Vista thymeleaf
    }

    // Formulario para nuevo país
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("afiliado", new Afiliado());
        model.addAttribute("paises", paisService.listarTodos()); // <--- esto llena el combo
        return "afiliado/nuevo"; // tu vista Thymeleaf del formulario
    }

    // Guardar país
    @PostMapping
    public String guardar(@ModelAttribute Pais pais) {
        paisService.guardar(pais);
        return "redirect:/pais";
    }
}
