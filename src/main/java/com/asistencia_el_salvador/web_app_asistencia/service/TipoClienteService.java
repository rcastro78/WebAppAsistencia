package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Pais;
import com.asistencia_el_salvador.web_app_asistencia.model.TipoCliente;
import com.asistencia_el_salvador.web_app_asistencia.repository.TipoClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoClienteService {
    private final TipoClienteRepository tipoClienteRepository;

    public TipoClienteService(TipoClienteRepository tipoClienteRepository) {
        this.tipoClienteRepository = tipoClienteRepository;
    }

    public List<TipoCliente> listarTodos() {
        return tipoClienteRepository.findAll();
    }
    public TipoCliente getTipoClienteByIdTipo(int id) {
        return tipoClienteRepository.getById(id);
    }
}
