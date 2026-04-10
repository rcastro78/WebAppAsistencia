package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Cobertura;
import com.asistencia_el_salvador.web_app_asistencia.repository.CoberturaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoberturaService {

    private final CoberturaRepository repository;

    public CoberturaService(CoberturaRepository repository) {
        this.repository = repository;
    }

    public List<Cobertura> listarActivas() {
        return repository.findByEstado(1);
    }

    public Page<Cobertura> listarPaginados(Pageable pageable) {
        return repository.findByEstado(1, pageable);
    }

    public Cobertura saveCobertura(Cobertura cobertura) {
        return repository.save(cobertura);
    }

    public Cobertura buscarPorId(Integer id) {
        return repository.findByIdCobertura(id);
    }

    public Cobertura actualizarCobertura(Integer id, Cobertura datos) {
        return repository.findById(String.valueOf(id))
                .map(c -> {
                    c.setNombreCobertura(datos.getNombreCobertura());
                    c.setEstado(datos.getEstado());
                    c.setEventos(datos.getEventos());
                    c.setLimiteEconomico(datos.getLimiteEconomico());
                    c.setPorAnio(datos.getPorAnio());
                    return repository.save(c);
                })
                .orElseThrow(() -> new RuntimeException("Cobertura no encontrada con ID: " + id));
    }

    public void eliminarCobertura(Integer id) {
        repository.findById(String.valueOf(id)).ifPresent(c -> {
            c.setEstado(0);  // borrado lógico
            repository.save(c);
        });
    }
}