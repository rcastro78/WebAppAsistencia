package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.Municipio;
import com.asistencia_el_salvador.web_app_asistencia.repository.MunicipioRepository;
import com.asistencia_el_salvador.web_app_asistencia.service.MunicipioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/municipios")
public class MunicipioController {
    private final MunicipioService municipioService;

    public MunicipioController(MunicipioService municipioService) {
        this.municipioService = municipioService;
    }

    @GetMapping(value = "/departamento/{idDepto}"/*, produces = "application/json"*/)
    public List<Municipio> getMunicipioByDepto(@PathVariable Integer idDepto) {
        return municipioService.getMunicipiosByDepto(idDepto);
    }
}
