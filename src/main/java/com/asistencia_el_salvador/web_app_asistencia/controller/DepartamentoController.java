package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.Departamento;
import com.asistencia_el_salvador.web_app_asistencia.service.DepartamentoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    // Endpoint para obtener departamentos por país
    @GetMapping("/pais/{idPais}")
    public List<Departamento> getDepartamentosByPais(@PathVariable Integer idPais) {
        return departamentoService.getDepartamentosByPais(idPais);
    }
}
