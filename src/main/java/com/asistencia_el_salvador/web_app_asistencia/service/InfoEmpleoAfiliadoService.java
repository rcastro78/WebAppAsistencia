package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.InfoEmpleoAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.repository.InfoEmpleoAfiliadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InfoEmpleoAfiliadoService {
    private final InfoEmpleoAfiliadoRepository repository;

    public InfoEmpleoAfiliadoService(InfoEmpleoAfiliadoRepository repository) {
        this.repository = repository;
    }

    public InfoEmpleoAfiliado listarTodos(String dui){
        return repository.findByDuiAfiliado(dui);
    }

    public InfoEmpleoAfiliado guardar(InfoEmpleoAfiliado infoEmpleo) {
        return repository.save(infoEmpleo);
    }

    public InfoEmpleoAfiliado listarPorDui(String dui) {
        return repository.findByDuiAfiliado(dui);
    }

    public InfoEmpleoAfiliado buscarPorId(String dui) {
        return repository.findByDuiAfiliado(dui);
    }

    public void eliminar(String id) {
        repository.deleteById(id);
    }


}
