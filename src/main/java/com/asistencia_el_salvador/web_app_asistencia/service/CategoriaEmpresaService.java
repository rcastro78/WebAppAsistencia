package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.CategoriaEmpresa;
import com.asistencia_el_salvador.web_app_asistencia.model.Cobertura;
import com.asistencia_el_salvador.web_app_asistencia.repository.CategoriaEmpresaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaEmpresaService {
    private final CategoriaEmpresaRepository categoriaEmpresaRepository;

    public CategoriaEmpresaService(CategoriaEmpresaRepository categoriaEmpresaRepository) {
        this.categoriaEmpresaRepository = categoriaEmpresaRepository;
    }

    public CategoriaEmpresa buscarPorId(Integer id) {
        return categoriaEmpresaRepository.findById(id).orElse(null);
    }


    public Page<CategoriaEmpresa> listarPaginados(Pageable pageable) {
        return categoriaEmpresaRepository.findByEstado(1, pageable);
    }

    public List<CategoriaEmpresa> listarTodas(){
        return categoriaEmpresaRepository.findByEstado(1);
    }

    public CategoriaEmpresa guardar(CategoriaEmpresa categoriaEmpresa) {
        return categoriaEmpresaRepository.save(categoriaEmpresa);
    }
    public CategoriaEmpresa actualizar(Integer id, CategoriaEmpresa categoriaEmpresa) {
        return categoriaEmpresaRepository.findById(id)
                .map(c->{
                    c.setCatNombre(categoriaEmpresa.getCatNombre());
                    c.setEstado(categoriaEmpresa.getEstado());
                    return categoriaEmpresaRepository.save(c);
                })
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con ID: " + id));
    }

    public CategoriaEmpresa eliminar(Integer id) {
        return categoriaEmpresaRepository.findById(id)
         .map(c->{
            c.setEstado(0);
            return categoriaEmpresaRepository.save(c);
        }).orElseThrow(() -> new RuntimeException("Cobertura no encontrada con ID: " + id));
    }
    /*
    * public Cobertura actualizarCobertura(Integer id, Cobertura datos) {
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
    * */

}
