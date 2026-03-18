package com.asistencia_el_salvador.web_app_asistencia.service;

import org.springframework.stereotype.Service;
import com.asistencia_el_salvador.web_app_asistencia.model.Pais;
import com.asistencia_el_salvador.web_app_asistencia.repository.PaisRepository;
import java.util.List;

@Service
public class PaisService {

    private final PaisRepository paisRepository;

    public PaisService(PaisRepository paisRepository) {
        this.paisRepository = paisRepository;
    }

    public List<Pais> listarTodos() {
        return paisRepository.findAll();
    }

    public Pais guardar(Pais pais) {
        return paisRepository.save(pais);
    }

    public Pais obtenerPorId(Integer id) {
        return paisRepository.findById(id).orElse(null);
    }

    public void eliminar(Integer id) {
        paisRepository.deleteById(id);
    }
}
