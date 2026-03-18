package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.EstadoAfiliacion;
import com.asistencia_el_salvador.web_app_asistencia.service.EstadoAfiliacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/estadoAfiliacion")
public class EstadoAfiliacionController {
    private final EstadoAfiliacionService estadoAfiliacionService;

    public EstadoAfiliacionController(EstadoAfiliacionService estadoAfiliacionService) {
        this.estadoAfiliacionService = estadoAfiliacionService;
    }
    @GetMapping
    public String listar(Model model) {
        List<EstadoAfiliacion> estadosAfiliacion = estadoAfiliacionService.listarTodos();
        model.addAttribute("estadoAfiliacion", estadosAfiliacion);
        return "estados/listar"; // Vista thymeleaf
    }
}
