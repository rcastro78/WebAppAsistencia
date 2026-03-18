package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.TipoCliente;
import com.asistencia_el_salvador.web_app_asistencia.service.TipoClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tipoCliente")
public class TipoClienteController {
private final TipoClienteService tipoClienteService;

    public TipoClienteController(TipoClienteService tipoClienteService) {
        this.tipoClienteService = tipoClienteService;
    }
    @GetMapping
    public String listar(Model model) {
        List<TipoCliente> tiposCliente = tipoClienteService.listarTodos();
        model.addAttribute("tipoCliente",tiposCliente);
        return "tiposCliente/listar";
    }
}
