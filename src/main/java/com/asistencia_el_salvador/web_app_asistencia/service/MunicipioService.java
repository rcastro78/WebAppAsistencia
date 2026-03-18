package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Municipio;
import com.asistencia_el_salvador.web_app_asistencia.repository.MunicipioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MunicipioService {
    private final MunicipioRepository municipioRepository;

    public MunicipioService(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    public List<Municipio> getMunicipiosByDepto(Integer idDepto){
        return municipioRepository.findByIdDepto(idDepto);
    }
}
